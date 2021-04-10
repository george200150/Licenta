# pylint: disable=wildcard-import, unused-wildcard-import

from .sseg import *

__all__ = ['model_list', 'get_model']

models = {
    # segmentation resnest models
    'fcn_resnest50_ade': get_fcn_resnest50_ade,
    'deeplab_resnest50_ade': get_deeplab_resnest50_ade,
    'deeplab_resnest101_ade': get_deeplab_resnest101_ade,
    'deeplab_resnest200_ade': get_deeplab_resnest200_ade,
    'deeplab_resnest269_ade': get_deeplab_resnest269_ade,
    'fcn_resnest50_pcontext': get_fcn_resnest50_pcontext,
    'deeplab_resnest50_pcontext': get_deeplab_resnest50_pcontext,
    'deeplab_resnest101_pcontext': get_deeplab_resnest101_pcontext,
    'deeplab_resnest200_pcontext': get_deeplab_resnest200_pcontext,
    'deeplab_resnest269_pcontext': get_deeplab_resnest269_pcontext,
}

model_list = list(models.keys())

def get_model(name, **kwargs):
    """Returns a pre-defined model by name

    Parameters
    ----------
    name : str
        Name of the model.
    pretrained : bool
        Whether to load the pretrained weights for model.
    root : str, default '~/.encoding/models'
        Location for keeping the model parameters.

    Returns
    -------
    Module:
        The model.
    """
    name = name.lower()
    if name not in models:
        raise ValueError('%s\n\t%s' % (str(name), '\n\t'.join(sorted(models.keys()))))
    net = models[name](**kwargs)
    return net
