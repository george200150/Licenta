from os import path, listdir
import torch
from torchvision import transforms
import random

from PIL import Image, ImageFile

ImageFile.LOAD_TRUNCATED_IMAGES = True


colors_per_class = {
    'dog': [254, 202, 87],
    'horse': [255, 107, 107],
    'elephant': [10, 189, 227],
    'butterfly': [255, 159, 243],
    'chicken': [16, 172, 132],
    'cat': [128, 80, 128],
    'cow': [87, 101, 116],
    'sheep': [52, 31, 151],
    'spider': [0, 0, 0],
    'squirrel': [100, 100, 255],
}


#
# colors_per_class = {
#
#     'scan_00000': (55, 228, 254),
#     'scan_00001': (136, 113, 46),
#     'scan_00002': (125, 140, 195),
#     'scan_00003': (134, 13, 54),
#     'scan_00004': (55, 77, 244),
#     'scan_00005': (91, 196, 49),
#     'scan_00006': [87, 101, 116],
#     'scan_00007': (92, 255, 86),
#     'scan_00008': (149, 19, 207),
#     'scan_00009': (159, 1, 40),
#     'scan_00010': (36, 156, 144),
#     'scan_00011': (39, 166, 29),
#     'scan_00012': (234, 99, 249),
#     'scan_00013': (140, 54, 252),
#     'scan_00014': (83, 92, 180),
#     'scan_00015': (122, 40, 95),
#     'scan_00016': (139, 155, 175),
#     'scan_00017': (145, 78, 168),
#     'scan_00018': (215, 233, 93),
#     'scan_00019': (129, 57, 69),
#
#     'scan_00020': (115, 246, 8),
#     'scan_00021': (185, 73, 114),
#     'scan_00022': (57, 13, 104),
#     'scan_00023': (225, 114, 80),
#     'scan_00024': (240, 208, 163),
#     'scan_00025': (210, 82, 77),
#     'scan_00026': (132, 131, 87),
#     'scan_00027': (163, 31, 229),
#     'scan_00028': (59, 221, 54),
#     'scan_00029': (57, 253, 149),
#
#     'scan_00030': (61, 170, 20),
#     'scan_00031': (133, 199, 250),
#     'scan_00032': (206, 184, 216),
#     'scan_00033': (110, 121, 214),
#     'scan_00034': (239, 169, 232),
#     'scan_00035': (223, 249, 228),
#     'scan_00036': (183, 103, 10),
#     'scan_00037': (229, 57, 202),
#     'scan_00038': (253, 85, 94),
#     'scan_00039': (8, 207, 6),
#
#     'scan_00040': (89, 251, 7),
#     'scan_00041': (33, 125, 207),
#     'scan_00042': (255, 82, 50),
#     'scan_00043': (157, 99, 196),
#     'scan_00044': (254, 253, 2),
#     'scan_00045': (104, 27, 214),
#     'scan_00046': (196, 214, 144),
#     'scan_00047': (19, 171, 21),
#     'scan_00048': (26, 37, 96),
#     'scan_00049': (104, 164, 216),
#
#     'scan_00050': (51, 33, 130),
#     'scan_00051': (107, 182, 78),
#     'scan_00052': (23, 221, 1),
#     'scan_00053': (36, 153, 187),
#     'scan_00054': (243, 93, 159),
#     'scan_00055': (129, 29, 112),
#     'scan_00056': (248, 148, 48),
#     'scan_00057': (36, 216, 51),
#     'scan_00058': (86, 131, 163),
#     'scan_00059': (87, 73, 248),
#
#     'scan_00060': (158, 36, 81),
#     'scan_00061': (156, 203, 36),
#     'scan_00062': (70, 219, 130),
#     'scan_00063': (176, 208, 170),
#     'scan_00064': (190, 125, 76),
#     'scan_00065': (220, 51, 57),
#     'scan_00066': (235, 219, 108),
#     'scan_00067': (149, 156, 205),
#     'scan_00068': (80, 227, 247),
#     'scan_00069': (87, 249, 87),
#
#     'scan_00070': (117, 141, 52),
#     'scan_00071': (210, 179, 43),
#     'scan_00072': (106, 121, 141),
#     'scan_00073': (3, 248, 83),
#     'scan_00074': (247, 60, 194),
#     'scan_00075': (104, 104, 247),
#     'scan_00076': (168, 65, 188),
#     'scan_00077': (247, 20, 219),
#     'scan_00078': (218, 169, 192),
#     'scan_00079': (131, 223, 169),
#
#     'scan_00080': (17, 37, 174),
#     'scan_00081': (198, 113, 94),
#     'scan_00082': (128, 209, 226),
#     'scan_00083': (147, 57, 151),
#     'scan_00084': (114, 61, 74),
#     'scan_00085': (27, 63, 136),
#     'scan_00086': (201, 56, 184),
#     'scan_00087': (65, 2, 139),
#     'scan_00088': (70, 182, 225),
#     'scan_00089': (29, 170, 98),
#
#     'scan_00090': (50, 16, 55),
#     'scan_00091': (242, 22, 155),
#     'scan_00092': (59, 255, 76),
#     'scan_00093': (234, 243, 109),
#     'scan_00094': (48, 169, 0),
#     'scan_00095': (254, 123, 33),
#     'scan_00096': (60, 227, 134),
#     'scan_00097': (238, 200, 107),
#     'scan_00098': (207, 239, 59),
#     'scan_00099': (226, 38, 193),
#
#     'scan_00100': (77, 184, 214),
#     'scan_00101': (252, 152, 142),
#     'scan_00102': (46, 1, 221),
#     'scan_00103': (118, 241, 141),
#     'scan_00104': (187, 24, 139),
#     'scan_00105': (251, 156, 163),
#     'scan_00106': [87, 101, 116],
#     'scan_00107': (171, 42, 93),
#     'scan_00108': (54, 239, 255),
#     'scan_00109': (156, 48, 81),
#     'scan_00110': (196, 252, 130),
#     'scan_00111': (122, 176, 138),
#     'scan_00112': (6, 223, 163),
#     'scan_00113': (184, 100, 97),
#     'scan_00114': (18, 231, 113),
#     'scan_00115': [10, 189, 227],
#     'scan_00116': (239, 131, 212),
#     'scan_00117': (152, 31, 74),
#     'scan_00118': (92, 57, 102),
#     'scan_00119': (232, 59, 21),
#
#     'scan_00120': (217, 202, 30),
#     'scan_00121': (71, 197, 237),
#     'scan_00122': [0, 0, 0],
#     'scan_00123': (221, 140, 192),
#     'scan_00124': (186, 2, 38),
#     'scan_00125': (20, 138, 161),
#     'scan_00126': [16, 172, 132],
#     'scan_00127': [52, 31, 151],
#     'scan_00128': (209, 169, 206),
#     'scan_00129': (245, 228, 168),
#
#     'scan_00130': (18, 60, 55),
#     'scan_00131': (96, 204, 49),
#     'scan_00132': (96, 234, 146),
#     'scan_00133': (95, 133, 200),
#     'scan_00134': (249, 2, 245),
#     'scan_00135': (128, 215, 120),
#     'scan_00136': (112, 6, 250),
#     'scan_00137': (182, 169, 242),
#     'scan_00138': (106, 248, 175),
#     'scan_00139': (24, 153, 147),
#
#     'scan_00140': [100, 100, 255],
#     'scan_00141': [100, 100, 255],
#     'scan_00142': [100, 100, 255],
#     'scan_00143': [100, 100, 255],
#     'scan_00144': [100, 100, 255],
#     'scan_00145': [254, 202, 87],
#     'scan_00146': [100, 100, 255],
#     'scan_00147': [100, 100, 255],
#     'scan_00148': [100, 100, 255],
#     'scan_00149': [100, 100, 255],
#
#     'scan_00150': [100, 100, 255],
#     'scan_00151': [100, 100, 255],
#     'scan_00152': [100, 100, 255],
#     'scan_00153': [100, 100, 255],
#     'scan_00154': [100, 100, 255],
#     'scan_00155': [100, 100, 255],
#     'scan_00156': [100, 100, 255],
#     'scan_00157': [100, 100, 255],
#     'scan_00158': [100, 100, 255],
#     'scan_00159': [100, 100, 255],
#
#     'scan_00160': [255, 159, 243],
#     'scan_00161': [100, 100, 255],
#     'scan_00162': [100, 100, 255],
#     'scan_00163': [100, 100, 255],
#     'scan_00164': [100, 100, 255],
#     'scan_00165': [100, 100, 255],
#     'scan_00166': [100, 100, 255],
#     'scan_00167': [100, 100, 255],
#     'scan_00168': [100, 100, 255],
#     'scan_00169': [100, 100, 255],
#
#     'scan_00170': [100, 100, 255],
#     'scan_00171': [100, 100, 255],
#     'scan_00172': [100, 100, 255],
#     'scan_00173': [100, 100, 255],
#     'scan_00174': [100, 100, 255],
#     'scan_00175': [100, 100, 255],
#     'scan_00176': [100, 100, 255],
#     'scan_00177': [100, 100, 255],
#     'scan_00178': [100, 100, 255],
#     'scan_00179': [100, 100, 255],
#
#     'scan_00180': [100, 100, 255],
#     'scan_00181': [100, 100, 255],
#     'scan_00182': [100, 100, 255],
#     'scan_00183': [100, 100, 255],
#     'scan_00184': [100, 100, 255],
#     'scan_00185': [255, 107, 107],
#     'scan_00186': [100, 100, 255],
#     'scan_00187': [100, 100, 255],
#     'scan_00188': [100, 100, 255],
#     'scan_00189': [100, 100, 255],
#
#     'scan_00190': [100, 100, 255],
#     'scan_00191': [100, 100, 255],
#     'scan_00192': [100, 100, 255],
#     'scan_00193': [100, 100, 255],
#     'scan_00194': [100, 100, 255],
#     'scan_00195': [100, 100, 255],
#     'scan_00196': [100, 100, 255],
#     'scan_00197': [100, 100, 255],
#     'scan_00198': [100, 100, 255],
#     'scan_00199': [100, 100, 255],
#
#     'scan_00200': (55, 228, 254),
#     'scan_00201': (136, 113, 46),
#     'scan_00202': (125, 140, 195),
#     'scan_00203': (134, 13, 54),
#     'scan_00204': (55, 77, 244),
#     'scan_00205': (91, 196, 49),
#     'scan_00206': [87, 101, 116],
#     'scan_00207': (92, 255, 86),
#     'scan_00208': (149, 19, 207),
#     'scan_00209': (159, 1, 40),
#     'scan_00210': (36, 156, 144),
#     'scan_00211': (39, 166, 29),
#     'scan_00212': (234, 99, 249),
#     'scan_00213': (140, 54, 252),
#     'scan_00214': (83, 92, 180),
#     'scan_00215': (122, 40, 95),
#     'scan_00216': (139, 155, 175),
#     'scan_00217': (145, 78, 168),
#     'scan_00218': (215, 233, 93),
#     'scan_00219': (129, 57, 69),
#
#     'scan_00220': (115, 246, 8),
#     'scan_00221': (185, 73, 114),
#     'scan_00222': (57, 13, 104),
#     'scan_00223': (225, 114, 80),
#     'scan_00224': (240, 208, 163),
#     'scan_00225': (210, 82, 77),
#     'scan_00226': (132, 131, 87),
#     'scan_00227': (163, 31, 229),
#     'scan_00228': (59, 221, 54),
#     'scan_00229': (57, 253, 149),
#
#     'scan_00230': (61, 170, 20),
#     'scan_00231': (133, 199, 250),
#     'scan_00232': (206, 184, 216),
#     'scan_00233': (110, 121, 214),
#     'scan_00234': (239, 169, 232),
#     'scan_00235': (223, 249, 228),
#     'scan_00236': (183, 103, 10),
#     'scan_00237': (229, 57, 202),
#     'scan_00238': (253, 85, 94),
#     'scan_00239': (8, 207, 6),
# }


# processes Animals10 dataset: https://www.kaggle.com/alessiocorrado99/animals10
class AnimalsDataset(torch.utils.data.Dataset):
    def __init__(self, data_path, num_images=1000):
        translation = {'cane': 'dog',
                       'cavallo': 'horse',
                       'elefante': 'elephant',
                       'farfalla': 'butterfly',
                       'gallina': 'chicken',
                       'gatto': 'cat',
                       'mucca': 'cow',
                       'pecora': 'sheep',
                       'ragno': 'spider',
                       'scoiattolo': 'squirrel'}

        self.classes = translation.values()

        if not path.exists(data_path):
            raise Exception(data_path + ' does not exist!')

        self.data = []

        folders = listdir(data_path)
        for folder in folders:
            label = translation[folder]

            full_path = path.join(data_path, folder)
            images = listdir(full_path)

            current_data = [(path.join(full_path, image), label) for image in images]
            self.data += current_data

        num_images = min(num_images, len(self.data))
        self.data = random.sample(self.data, num_images)  # only use num_images images

        # We use the transforms described in official PyTorch ResNet inference example:
        # https://pytorch.org/hub/pytorch_vision_resnet/.
        self.transform = transforms.Compose([
            transforms.Resize(256),
            transforms.CenterCrop(224),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
        ])

    def __len__(self):
        return len(self.data)

    def __getitem__(self, index):
        image_path, label = self.data[index]

        image = Image.open(image_path)

        try:
            image = self.transform(image)  # some images in the dataset cannot be processed - we'll skip them
        except Exception:
            return None

        dict_data = {
            'image': image,
            'label': label,
            'image_path': image_path
        }
        return dict_data


# Skips empty samples in a batch
def collate_skip_empty(batch):
    batch = [sample for sample in batch if sample]  # check that sample is not None
    return torch.utils.data.dataloader.default_collate(batch)
