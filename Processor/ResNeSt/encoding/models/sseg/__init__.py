from .base import *
from .fcn import *
from .atten import *
from .encnet import *
from .deeplab import *

def get_segmentation_model(name, **kwargs):
    models = {
        'fcn': get_fcn,
        'atten': get_atten,
        'encnet': get_encnet,
        'deeplab': get_deeplab,
    }
    return models[name.lower()](**kwargs)
