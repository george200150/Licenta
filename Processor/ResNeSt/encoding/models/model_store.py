"""Model store which provides pretrained models."""
from __future__ import print_function
__all__ = ['get_model_file', 'purge']
import os
import zipfile
import portalocker

from ..utils import download, check_sha1

_model_sha1 = {name: checksum for checksum, name in [
    # resnest
    ('fb9de5b360976e3e8bd3679d3e93c5409a5eff3c', 'resnest50'),
    # resnest segmentation models
    ('4aba491aaf8e4866a9c9981b210e3e3266ac1f2a', 'fcn_resnest50_ade'),
    ('2225f09d0f40b9a168d9091652194bc35ec2a5a9', 'deeplab_resnest50_ade'),
    ('06ca799c8cc148fe0fafb5b6d052052935aa3cc8', 'deeplab_resnest101_ade'),
    ('7b9e7d3e6f0e2c763c7d77cad14d306c0a31fe05', 'deeplab_resnest200_ade'),
    ('0074dd10a6e6696f6f521653fb98224e75955496', 'deeplab_resnest269_ade'),
    ('77a2161deeb1564e8b9c41a4bb7a3f33998b00ad', 'fcn_resnest50_pcontext'),
    ('08dccbc4f4694baab631e037a374d76d8108c61f', 'deeplab_resnest50_pcontext'),
    ('faf5841853aae64bd965a7bdc2cdc6e7a2b5d898', 'deeplab_resnest101_pcontext'),
    ('fe76a26551dd5dcf2d474fd37cba99d43f6e984e', 'deeplab_resnest200_pcontext'),
    ('b661fd26c49656e01e9487cd9245babb12f37449', 'deeplab_resnest269_pcontext'),
    ]}

encoding_repo_url = 'https://s3.us-west-1.wasabisys.com/encoding'
_url_format = '{repo_url}models/{file_name}.zip'

def short_hash(name):
    if name not in _model_sha1:
        raise ValueError('Pretrained model for {name} is not available.'.format(name=name))
    return _model_sha1[name][:8]

def get_model_file(name, root=os.path.join('~', '.encoding', 'models')):
    r"""Return location for the pretrained on local file system.

    This function will download from online model zoo when model cannot be found or has mismatch.
    The root directory will be created if it doesn't exist.

    Parameters
    ----------
    name : str
        Name of the model.
    root : str, default '~/.encoding/models'
        Location for keeping the model parameters.

    Returns
    -------
    file_path
        Path to the requested pretrained model file.
    """
    if name not in _model_sha1:
        from torchvision.models.resnet import model_urls
        if name not in model_urls:
            raise ValueError('Pretrained model for {name} is not available.'.format(name=name))
        root = os.path.expanduser(root)
        return download(model_urls[name],
                        path=root,
                        overwrite=True)
    file_name = '{name}-{short_hash}'.format(name=name, short_hash=short_hash(name))
    root = os.path.expanduser(root)
    if not os.path.exists(root):
        os.makedirs(root)

    file_path = os.path.join(root, file_name+'.pth')
    sha1_hash = _model_sha1[name]

    lockfile = os.path.join(root, file_name + '.lock')
    with portalocker.Lock(lockfile, timeout=300):
        if os.path.exists(file_path):
            if check_sha1(file_path, sha1_hash):
                return file_path
            else:
                print('Mismatch in the content of model file {} detected.' +
                      ' Downloading again.'.format(file_path))
        else:
            print('Model file {} is not found. Downloading.'.format(file_path))

        zip_file_path = os.path.join(root, file_name+'.zip')
        repo_url = os.environ.get('ENCODING_REPO', encoding_repo_url)
        if repo_url[-1] != '/':
            repo_url = repo_url + '/'
        download(_url_format.format(repo_url=repo_url, file_name=file_name),
                 path=zip_file_path,
                 overwrite=True)
        with zipfile.ZipFile(zip_file_path) as zf:
            zf.extractall(root)
        os.remove(zip_file_path)

        if check_sha1(file_path, sha1_hash):
            return file_path
        else:
            raise ValueError('Downloaded file has different hash. Please try again.')

def purge(root=os.path.join('~', '.encoding', 'models')):
    r"""Purge all pretrained model files in local file store.

    Parameters
    ----------
    root : str, default '~/.encoding/models'
        Location for keeping the model parameters.
    """
    root = os.path.expanduser(root)
    files = os.listdir(root)
    for f in files:
        if f.endswith(".pth"):
            os.remove(os.path.join(root, f))

def pretrained_model_list():
    return list(_model_sha1.keys())
