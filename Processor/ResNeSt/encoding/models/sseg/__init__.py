from .base import *
from .deeplab import *

def get_segmentation_model(name, **kwargs):
    models = {
        'deeplab': get_deeplab,
    }
    return models[name.lower()](**kwargs)
