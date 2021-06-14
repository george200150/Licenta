import numpy as np
from diode import plot_depth_map

# root = 'C:/Users/George/Datasets/DIODE/Depth/val/indoors/scene_00020/scan_00185/'
# root = 'C:/Users/George/Datasets/DIODE/Depth/train/indoors/scene_00003/scan_00029/'
root = 'C:/Users/George/Datasets/DIODE/Depth/train/indoors/scene_00002/scan_00022/'
# root = 'C:/Users/George/Datasets/DIODE/Depth/val/outdoor/scene_00023/scan_00200/'

# filename_depth = '00020_00185_indoors_000_000_depth.npy'
# filename_depth = '00003_00029_indoors_030_000_depth.npy'
filename_depth = '00002_00022_indoors_000_000_depth.npy'
# filename_depth = '00023_00200_outdoor_000_010_depth.npy'
# filename_valid = '00020_00185_indoors_000_000_depth_mask.npy'
# filename_valid = '00003_00029_indoors_030_000_depth_mask.npy'
filename_valid = '00002_00022_indoors_000_000_depth_mask.npy'
# filename_valid = '00023_00200_outdoor_000_010_depth_mask.npy'


def main():
    np_array_depth = np.load(root + filename_depth)
    np_array_valid = np.load(root + filename_valid)

    plot_depth_map(np_array_depth[:, :, 0], np_array_valid)

    print(np_array_depth.size)

    for x in np_array_depth[:, :, 0][0]:
        print(x)


if __name__ == '__main__':
    main()
