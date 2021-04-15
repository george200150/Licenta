# Modified based on the HRNet repo.

import argparse

import torch
import torch.backends.cudnn as cudnn
import torchvision.transforms as transform
from PIL import Image
from torch import nn
from yacs.config import CfgNode as CN

from MDEQ.lib.models.mdeq import get_seg_net

_C = CN()

_C.OUTPUT_DIR = ''
_C.LOG_DIR = ''
_C.DATA_DIR = ''
_C.GPUS = (0,)
_C.WORKERS = 0
_C.PERCENT = 1.0
_C.PRINT_FREQ = 20
_C.AUTO_RESUME = False
_C.PIN_MEMORY = True
_C.RANK = 0

# Cudnn related params
_C.CUDNN = CN()
_C.CUDNN.BENCHMARK = True
_C.CUDNN.DETERMINISTIC = False
_C.CUDNN.ENABLED = True

# common params for NETWORK
_C.MODEL = CN()
_C.MODEL.NAME = 'mdeq'  # Default for classification
_C.MODEL.INIT_WEIGHTS = True
_C.MODEL.WNORM = False
_C.MODEL.PRETRAINED = ''
_C.MODEL.NUM_JOINTS = 17
_C.MODEL.NUM_LAYERS = 5
_C.MODEL.NUM_GROUPS = 4
_C.MODEL.DROPOUT = 0.0
_C.MODEL.NUM_CLASSES = 1000
_C.MODEL.F_THRES = 30
_C.MODEL.B_THRES = 40
_C.MODEL.DOWNSAMPLE_TIMES = 2
_C.MODEL.EXPANSION_FACTOR = 5
_C.MODEL.TAG_PER_JOINT = True
_C.MODEL.TARGET_TYPE = 'gaussian'
_C.MODEL.IMAGE_SIZE = [256, 256]  # width * height, ex: 192 * 256
_C.MODEL.HEATMAP_SIZE = [64, 64]  # width * height, ex: 24 * 32
_C.MODEL.SIGMA = 2
_C.MODEL.EXTRA = CN(new_allowed=True)

_C.LOSS = CN()
_C.LOSS.USE_OHEM = False
_C.LOSS.OHEMTHRES = 0.9
_C.LOSS.OHEMKEEP = 100000
_C.LOSS.CLASS_BALANCE = True
_C.LOSS.TOPK = 8
_C.LOSS.USE_TARGET_WEIGHT = True
_C.LOSS.USE_DIFFERENT_JOINTS_WEIGHT = False

# DATASET related params
_C.DATASET = CN()
_C.DATASET.ROOT = 'C:/Users/George/Datasets/cityscapes'
_C.DATASET.DATASET = 'cityscapes'
_C.DATASET.TRAIN_SET = 'train'
_C.DATASET.NUM_CLASSES = 1000
_C.DATASET.TEST_SET = 'valid'
_C.DATASET.DATA_FORMAT = 'jpg'
_C.DATASET.HYBRID_JOINTS_TYPE = ''
_C.DATASET.SELECT_DATA = False
_C.DATASET.EXTRA_TRAIN_SET = ''

# training data augmentation
_C.DATASET.AUGMENT = True
_C.DATASET.FLIP = True
_C.DATASET.SCALE_FACTOR = 0.25
_C.DATASET.ROT_FACTOR = 30
_C.DATASET.PROB_HALF_BODY = 0.0
_C.DATASET.NUM_JOINTS_HALF_BODY = 8
_C.DATASET.COLOR_RGB = False

# train
_C.TRAIN = CN()
_C.TRAIN.IMAGE_SIZE = [1024, 512]  # width * height
_C.TRAIN.BASE_SIZE = 2048
_C.TRAIN.DOWNSAMPLERATE = 1
_C.TRAIN.FLIP = True
_C.TRAIN.MULTI_SCALE = True
_C.TRAIN.SCALE_FACTOR = 16
_C.TRAIN.CLIP = -1.0

_C.TRAIN.LR_FACTOR = 0.1
_C.TRAIN.LR_STEP = [90, 110]
_C.TRAIN.LR = 0.001
_C.TRAIN.LR_SCHEDULER = 'step'
_C.TRAIN.EXTRA_LR = 0.001

_C.TRAIN.OPTIMIZER = 'adam'
_C.TRAIN.MOMENTUM = 0.9
_C.TRAIN.WD = 0.0001
_C.TRAIN.NESTEROV = False
_C.TRAIN.GAMMA1 = 0.99
_C.TRAIN.GAMMA2 = 0.0
_C.TRAIN.PRETRAIN_STEPS = 100000
_C.TRAIN.IGNORE_LABEL = -1

_C.TRAIN.BEGIN_EPOCH = 0
_C.TRAIN.END_EPOCH = 140
_C.TRAIN.EXTRA_EPOCH = 0

_C.TRAIN.RESUME = False
_C.TRAIN.CHECKPOINT = ''
_C.TRAIN.MODEL_FILE = ''

_C.TRAIN.BATCH_SIZE_PER_GPU = 32
_C.TRAIN.SHUFFLE = True

# testing
_C.TEST = CN()

# size of images for each device
_C.TEST.BATCH_SIZE_PER_GPU = 32

# For segmentation
_C.TEST.IMAGE_SIZE = [2048, 1024]  # width * height
_C.TEST.BASE_SIZE = 2048
_C.TEST.NUM_SAMPLES = 0

# Test Model Epoch
_C.TEST.FLIP_TEST = False
_C.TEST.POST_PROCESS = False
_C.TEST.SHIFT_HEATMAP = False
_C.TEST.MULTI_SCALE = False
_C.TEST.CENTER_CROP_TEST = False
_C.TEST.SCALE_LIST = [1]
_C.TEST.USE_GT_BBOX = False

# nms
_C.TEST.IMAGE_THRE = 0.1
_C.TEST.NMS_THRE = 0.6
_C.TEST.SOFT_NMS = False
_C.TEST.OKS_THRE = 0.5
_C.TEST.IN_VIS_THRE = 0.0
_C.TEST.COCO_BBOX_FILE = ''
_C.TEST.BBOX_THRE = 1.0
_C.TEST.MODEL_FILE = 'MDEQ/pretrained_models/MDEQ_Small_Seg.pkl'

# debug
_C.DEBUG = CN()
_C.DEBUG.DEBUG = False
_C.DEBUG.SAVE_BATCH_IMAGES_GT = False
_C.DEBUG.SAVE_BATCH_IMAGES_PRED = False
_C.DEBUG.SAVE_HEATMAPS_GT = False
_C.DEBUG.SAVE_HEATMAPS_PRED = False


def update_config(cfg, args):
    cfg.defrost()
    cfg.merge_from_file(args.cfg)

    if args.modelDir:
        cfg.OUTPUT_DIR = args.modelDir

    if args.logDir:
        cfg.LOG_DIR = args.logDir

    if args.dataDir:
        cfg.DATA_DIR = args.dataDir

    if args.testModel:
        cfg.TEST.MODEL_FILE = args.testModel

    if args.percent < 1:
        cfg.PERCENT = args.percent

    cfg.merge_from_list(args.opts)

    cfg.freeze()


def parse_args():
    parser = argparse.ArgumentParser(description='Train segmentation network')

    parser.add_argument('--cfg',
                        help='experiment configure file name',
                        required=False,
                        type=str)
    parser.add_argument('--modelDir',
                        help='model directory',
                        type=str,
                        default='')
    parser.add_argument('--logDir',
                        help='log directory',
                        type=str,
                        default='')
    parser.add_argument('--dataDir',
                        help='data directory',
                        type=str,
                        default='')
    parser.add_argument('--testModel',
                        help='testModel',
                        type=str,
                        default='')
    parser.add_argument('--percent',
                        help='percentage of training data to use',
                        type=float,
                        default=1.0)
    parser.add_argument("--local_rank", type=int, default=0)
    parser.add_argument('opts',
                        help="Modify config options using the command-line",
                        default=None,
                        nargs=argparse.REMAINDER)

    args = parser.parse_args()

    # inline args
    args.cfg = 'MDEQ/experiments/cityscapes/seg_mdeq_SMALL.yaml'

    update_config(config, args)

    return args


config = _C


def inference(model, img):
    newsize = (2048, 1024)
    img = img.resize(newsize)

    my_image = load_image(img).cuda().unsqueeze(0)

    output = model(x=my_image, train_step=-1)
    predict = torch.max(output, 1)[1].cpu().numpy() + 1

    mask = get_mask_pallete(predict, 'ade20k')  # this should change when using DE

    newsize = (512, 256)
    mask.resize(newsize)
    return 512, 256, mask


def loadModel():
    args = parse_args()

    # cudnn related setting
    cudnn.benchmark = config.CUDNN.BENCHMARK
    cudnn.deterministic = config.CUDNN.DETERMINISTIC
    cudnn.enabled = config.CUDNN.ENABLED

    # build model
    model = get_seg_net(config)

    # print(model)

    dump_input = torch.rand(
        (1, 3, config.TRAIN.IMAGE_SIZE[1], config.TRAIN.IMAGE_SIZE[0])
    )

    model_state_file = 'MDEQ/pretrained_models/MDEQ_SMALL_Seg.pkl'

    pretrained_dict = torch.load(model_state_file)
    model_dict = model.state_dict()
    pretrained_dict = {k[6:]: v for k, v in pretrained_dict.items()
                       if k[6:] in model_dict.keys()}  # To remove the "model." from state dict

    model_dict.update(pretrained_dict)
    model.load_state_dict(model_dict)

    gpus = list(config.GPUS)
    model = nn.DataParallel(model, device_ids=gpus).cuda()
    return model


input_transform = transform.Compose([
    transform.ToTensor(),
    transform.Normalize([.485, .456, .406], [.229, .224, .225])])


def load_image(img, size=None, scale=None, keep_asp=True, img_transform=input_transform):
    """Load the image for demos"""
    if size is not None:
        if keep_asp:
            size2 = int(size * 1.0 / img.size[0] * img.size[1])
            img = img.resize((size, size2), Image.ANTIALIAS)
        else:
            img = img.resize((size, size), Image.ANTIALIAS)
    elif scale is not None:
        img = img.resize((int(img.size[0] / scale), int(img.size[1] / scale)), Image.ANTIALIAS)

    if img_transform:
        img = img_transform(img)
    return img


def get_mask_pallete(npimg, dataset='detail'):
    """Get image color pallete for visualizing masks"""
    # recovery boundary
    if dataset == 'pascal_voc':
        npimg[npimg == 21] = 255
    # put colormap
    out_img = Image.fromarray(npimg.squeeze().astype('uint8'))
    if dataset == 'ade20k':
        out_img.putpalette(adepallete)
    elif dataset == 'citys':
        out_img.putpalette(citypallete)
    elif dataset in ('detail', 'pascal_voc', 'pascal_aug'):
        out_img.putpalette(vocpallete)
    return out_img


def _get_voc_pallete(num_cls):
    n = num_cls
    pallete = [0] * (n * 3)
    for j in range(0, n):
        lab = j
        pallete[j * 3 + 0] = 0
        pallete[j * 3 + 1] = 0
        pallete[j * 3 + 2] = 0
        i = 0
        while lab > 0:
            pallete[j * 3 + 0] |= (((lab >> 0) & 1) << (7 - i))
            pallete[j * 3 + 1] |= (((lab >> 1) & 1) << (7 - i))
            pallete[j * 3 + 2] |= (((lab >> 2) & 1) << (7 - i))
            i = i + 1
            lab >>= 3
    return pallete


vocpallete = _get_voc_pallete(256)

adepallete = [0, 0, 0, 120, 120, 120, 180, 120, 120, 6, 230, 230, 80, 50, 50, 4, 200, 3, 120, 120, 80, 140, 140, 140,
              204, 5, 255, 230, 230, 230, 4, 250, 7, 224, 5, 255, 235, 255, 7, 150, 5, 61, 120, 120, 70, 8, 255, 51,
              255, 6, 82, 143, 255, 140, 204, 255, 4, 255, 51, 7, 204, 70, 3, 0, 102, 200, 61, 230, 250, 255, 6, 51, 11,
              102, 255, 255, 7, 71, 255, 9, 224, 9, 7, 230, 220, 220, 220, 255, 9, 92, 112, 9, 255, 8, 255, 214, 7, 255,
              224, 255, 184, 6, 10, 255, 71, 255, 41, 10, 7, 255, 255, 224, 255, 8, 102, 8, 255, 255, 61, 6, 255, 194,
              7, 255, 122, 8, 0, 255, 20, 255, 8, 41, 255, 5, 153, 6, 51, 255, 235, 12, 255, 160, 150, 20, 0, 163, 255,
              140, 140, 140, 250, 10, 15, 20, 255, 0, 31, 255, 0, 255, 31, 0, 255, 224, 0, 153, 255, 0, 0, 0, 255, 255,
              71, 0, 0, 235, 255, 0, 173, 255, 31, 0, 255, 11, 200, 200, 255, 82, 0, 0, 255, 245, 0, 61, 255, 0, 255,
              112, 0, 255, 133, 255, 0, 0, 255, 163, 0, 255, 102, 0, 194, 255, 0, 0, 143, 255, 51, 255, 0, 0, 82, 255,
              0, 255, 41, 0, 255, 173, 10, 0, 255, 173, 255, 0, 0, 255, 153, 255, 92, 0, 255, 0, 255, 255, 0, 245, 255,
              0, 102, 255, 173, 0, 255, 0, 20, 255, 184, 184, 0, 31, 255, 0, 255, 61, 0, 71, 255, 255, 0, 204, 0, 255,
              194, 0, 255, 82, 0, 10, 255, 0, 112, 255, 51, 0, 255, 0, 194, 255, 0, 122, 255, 0, 255, 163, 255, 153, 0,
              0, 255, 10, 255, 112, 0, 143, 255, 0, 82, 0, 255, 163, 255, 0, 255, 235, 0, 8, 184, 170, 133, 0, 255, 0,
              255, 92, 184, 0, 255, 255, 0, 31, 0, 184, 255, 0, 214, 255, 255, 0, 112, 92, 255, 0, 0, 224, 255, 112,
              224, 255, 70, 184, 160, 163, 0, 255, 153, 0, 255, 71, 255, 0, 255, 0, 163, 255, 204, 0, 255, 0, 143, 0,
              255, 235, 133, 255, 0, 255, 0, 235, 245, 0, 255, 255, 0, 122, 255, 245, 0, 10, 190, 212, 214, 255, 0, 0,
              204, 255, 20, 0, 255, 255, 255, 0, 0, 153, 255, 0, 41, 255, 0, 255, 204, 41, 0, 255, 41, 255, 0, 173, 0,
              255, 0, 245, 255, 71, 0, 255, 122, 0, 255, 0, 255, 184, 0, 92, 255, 184, 255, 0, 0, 133, 255, 255, 214, 0,
              25, 194, 194, 102, 255, 0, 92, 0, 255]

citypallete = [
    128, 64, 128, 244, 35, 232, 70, 70, 70, 102, 102, 156, 190, 153, 153, 153, 153, 153, 250, 170, 30, 220, 220, 0, 107,
    142, 35, 152, 251, 152, 70, 130, 180, 220, 20, 60, 255, 0, 0, 0, 0, 142, 0, 0, 70, 0, 60, 100, 0, 80, 100, 0, 0,
    230, 119, 11, 32, 128, 192, 0, 0, 64, 128, 128, 64, 128, 0, 192, 128, 128, 192, 128, 64, 64, 0, 192, 64, 0, 64, 192,
    0, 192, 192, 0, 64, 64, 128, 192, 64, 128, 64, 192, 128, 192, 192, 128, 0, 0, 64, 128, 0, 64, 0, 128, 64, 128, 128,
    64, 0, 0, 192, 128, 0, 192, 0, 128, 192, 128, 128, 192, 64, 0, 64, 192, 0, 64, 64, 128, 64, 192, 128, 64, 64, 0,
    192, 192, 0, 192, 64, 128, 192, 192, 128, 192, 0, 64, 64, 128, 64, 64, 0, 192, 64, 128, 192, 64, 0, 64, 192, 128,
    64, 192, 0, 192, 192, 128, 192, 192, 64, 64, 64, 192, 64, 64, 64, 192, 64, 192, 192, 64, 64, 64, 192, 192, 64, 192,
    64, 192, 192, 192, 192, 192, 32, 0, 0, 160, 0, 0, 32, 128, 0, 160, 128, 0, 32, 0, 128, 160, 0, 128, 32, 128, 128,
    160, 128, 128, 96, 0, 0, 224, 0, 0, 96, 128, 0, 224, 128, 0, 96, 0, 128, 224, 0, 128, 96, 128, 128, 224, 128, 128,
    32, 64, 0, 160, 64, 0, 32, 192, 0, 160, 192, 0, 32, 64, 128, 160, 64, 128, 32, 192, 128, 160, 192, 128, 96, 64, 0,
    224, 64, 0, 96, 192, 0, 224, 192, 0, 96, 64, 128, 224, 64, 128, 96, 192, 128, 224, 192, 128, 32, 0, 64, 160, 0, 64,
    32, 128, 64, 160, 128, 64, 32, 0, 192, 160, 0, 192, 32, 128, 192, 160, 128, 192, 96, 0, 64, 224, 0, 64, 96, 128, 64,
    224, 128, 64, 96, 0, 192, 224, 0, 192, 96, 128, 192, 224, 128, 192, 32, 64, 64, 160, 64, 64, 32, 192, 64, 160, 192,
    64, 32, 64, 192, 160, 64, 192, 32, 192, 192, 160, 192, 192, 96, 64, 64, 224, 64, 64, 96, 192, 64, 224, 192, 64, 96,
    64, 192, 224, 64, 192, 96, 192, 192, 224, 192, 192, 0, 32, 0, 128, 32, 0, 0, 160, 0, 128, 160, 0, 0, 32, 128, 128,
    32, 128, 0, 160, 128, 128, 160, 128, 64, 32, 0, 192, 32, 0, 64, 160, 0, 192, 160, 0, 64, 32, 128, 192, 32, 128, 64,
    160, 128, 192, 160, 128, 0, 96, 0, 128, 96, 0, 0, 224, 0, 128, 224, 0, 0, 96, 128, 128, 96, 128, 0, 224, 128, 128,
    224, 128, 64, 96, 0, 192, 96, 0, 64, 224, 0, 192, 224, 0, 64, 96, 128, 192, 96, 128, 64, 224, 128, 192, 224, 128, 0,
    32, 64, 128, 32, 64, 0, 160, 64, 128, 160, 64, 0, 32, 192, 128, 32, 192, 0, 160, 192, 128, 160, 192, 64, 32, 64,
    192, 32, 64, 64, 160, 64, 192, 160, 64, 64, 32, 192, 192, 32, 192, 64, 160, 192, 192, 160, 192, 0, 96, 64, 128, 96,
    64, 0, 224, 64, 128, 224, 64, 0, 96, 192, 128, 96, 192, 0, 224, 192, 128, 224, 192, 64, 96, 64, 192, 96, 64, 64,
    224, 64, 192, 224, 64, 64, 96, 192, 192, 96, 192, 64, 224, 192, 192, 224, 192, 32, 32, 0, 160, 32, 0, 32, 160, 0,
    160, 160, 0, 32, 32, 128, 160, 32, 128, 32, 160, 128, 160, 160, 128, 96, 32, 0, 224, 32, 0, 96, 160, 0, 224, 160, 0,
    96, 32, 128, 224, 32, 128, 96, 160, 128, 224, 160, 128, 32, 96, 0, 160, 96, 0, 32, 224, 0, 160, 224, 0, 32, 96, 128,
    160, 96, 128, 32, 224, 128, 160, 224, 128, 96, 96, 0, 224, 96, 0, 96, 224, 0, 224, 224, 0, 96, 96, 128, 224, 96,
    128, 96, 224, 128, 224, 224, 128, 32, 32, 64, 160, 32, 64, 32, 160, 64, 160, 160, 64, 32, 32, 192, 160, 32, 192, 32,
    160, 192, 160, 160, 192, 96, 32, 64, 224, 32, 64, 96, 160, 64, 224, 160, 64, 96, 32, 192, 224, 32, 192, 96, 160,
    192, 224, 160, 192, 32, 96, 64, 160, 96, 64, 32, 224, 64, 160, 224, 64, 32, 96, 192, 160, 96, 192, 32, 224, 192,
    160, 224, 192, 96, 96, 64, 224, 96, 64, 96, 224, 64, 224, 224, 64, 96, 96, 192, 224, 96, 192, 96, 224, 192, 0, 0, 0]

# THCudaCheck FAIL file=..\torch/csrc/generic/StorageSharing.cpp line=247 error=801 : operation not supported
# solved by modifying worker_num == 0
