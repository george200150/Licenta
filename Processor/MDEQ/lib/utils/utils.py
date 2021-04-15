# Modified based on the HRNet repo.

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import torch


def get_world_size():
    if not torch.distributed.is_initialized():
        return 1
    return torch.distributed.get_world_size()
