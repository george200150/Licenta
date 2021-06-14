import cv2
import numpy as np
import torch
from PIL import Image
from torchvision.transforms import Compose

from DPT.dpt.models import DPTDepthModel, DPTSegmentationModel
from DPT.dpt.transforms import Resize, NormalizeImage, PrepareForNet
from DPT.util.pallete import get_mask_pallete


def loadModel(model_task):
    if model_task == "DE":
        model_path = "DPT/weights/dpt_hybrid-midas-501f0c75.pt"
        model = DPTDepthModel(
            path=model_path,
            backbone="vitb_rn50_384",
            non_negative=True,
            enable_attention_hooks=False,
        )
    elif model_task == "SS":
        model_path = "DPT/weights/dpt_hybrid-ade20k-53898607.pt"
        model = DPTSegmentationModel(
            150,
            path=model_path,
            backbone="vitb_rn50_384",
        )
    else:
        raise Exception("wrong architecture type!")

    # load network
    model.eval()
    model.cuda()
    return model


def inference(model_task, model, img):
    img_height, img_width = img.shape[:-1]  # opencv size format
    net_w = img_width
    net_h = img_height

    transform = Compose(
        [
            Resize(
                net_w,
                net_h,
                resize_target=None,
                keep_aspect_ratio=True,
                ensure_multiple_of=32,
                resize_method="minimal",
                image_interpolation_method=cv2.INTER_CUBIC,
            ),
            NormalizeImage(mean=[0.5, 0.5, 0.5], std=[0.5, 0.5, 0.5]),
            PrepareForNet(),
        ]
    )
    # set torch options
    torch.backends.cudnn.enabled = True
    torch.backends.cudnn.benchmark = True

    # input
    img_input = transform({"image": img})["image"]

    # compute
    with torch.no_grad():
        sample = torch.from_numpy(img_input).cuda().unsqueeze(0)
        prediction = model.forward(sample)

        if model_task == "DE":
            prediction = post_process_prediction_de(img, prediction)
            out = prepare_for_visualisation_de(prediction)
            net_w, net_h = out.size

        elif model_task == "SS":
            prediction = post_process_prediction_ss(img, prediction)
            out = prepare_for_visualisation_ss(img, prediction)

    return net_w, net_h, out


def prepare_for_visualisation_ss(img, prediction):
    mask = get_mask_pallete(prediction, "ade20k")
    img = Image.fromarray(np.uint8(255 * img)).convert("RGB")
    seg = mask.convert("RGB")
    out = Image.blend(img, seg, 1.0)
    return out


def prepare_for_visualisation_de(prediction):
    bits = 1  # 1 - use bytes (RGB compatible format)
    depth = prediction
    depth_min = depth.min()
    depth_max = depth.max()
    max_val = (2 ** (8 * bits)) - 1
    if depth_max - depth_min > np.finfo("float").eps:
        out = max_val * (depth - depth_min) / (depth_max - depth_min)
    else:
        out = np.zeros(depth.shape, dtype=depth.dtype)
    out = Image.fromarray(out.astype("uint8"))
    return out


def post_process_prediction_ss(img, prediction):
    prediction = torch.nn.functional.interpolate(
        prediction, size=img.shape[:2], mode="bicubic", align_corners=False
    )
    prediction = torch.argmax(prediction, dim=1) + 1
    prediction = prediction.squeeze().cpu().numpy()
    return prediction


def post_process_prediction_de(img, prediction):
    prediction = (
        torch.nn.functional.interpolate(
            prediction.unsqueeze(1),
            size=img.shape[:2],
            mode="bicubic",
            align_corners=False,
        )
        .squeeze()
        .cpu()
        .numpy()
    )
    return prediction


if __name__ == '__main__':
    pass
