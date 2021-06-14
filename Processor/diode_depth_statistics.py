import pickle

import matplotlib.pyplot as plt
from PIL import Image
from matplotlib import cm
from tqdm import tqdm
from diode_original import DIODE, plot_depth_map
import numpy as np

meta_fname = 'C:\\Users\\George\\Datasets\\DIODE\\diode_meta.json'
data_root = 'C:\\Users\\George\\Datasets\\DIODE\\Depth\\'


def save_avg_depth():
    avg_depth = 0
    nr = 0

    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'])
    dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors'])
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['outdoor'])

    for _, dm, mask in tqdm(dset, "computing mean distance", len(dset)):  # for each image in the dataset...
        for indx, (row_dm, row_mask) in enumerate(zip(dm, mask)):  # for each row...
            # TODO: total gresit. este doar o citire pe diagonala... vai de capul meu, am compromis toate rezultatele
            if row_mask[indx] == 1.:
                avg_depth += row_dm[indx]  # for each value in each row..
                nr += 1

    assert len(dset) == nr
    print(avg_depth)

    avg_depth = avg_depth / nr
    a_file = open("avg_depth_indoor.pkl", "wb")
    pickle.dump(avg_depth, a_file)
    a_file.close()


def save_avg_depth_from_pkl():
    total_depth = 0
    nr = 0

    a_file = open("data_distribution_indoors.pkl", "rb")
    # a_file = open("data_distribution.pkl", "rb")
    distribution_dict = pickle.load(a_file)

    items = distribution_dict.items()

    # for k,v in items:
    #     print(k,v)

    for depth, count in tqdm(items, "computing avg", len(items)):  # for each image in the dataset...
        total = depth * count
        # print(depth, "\t", count)
        total_depth += total  # for each value in each row..
        nr += count

    assert sum(distribution_dict.values()) == nr
    print("total_depth = ", total_depth)
    print("nr = ", nr)

    avg_depth = total_depth / nr
    a_file = open("avg_depth_indoor.pkl", "wb")
    # a_file = open("avg_depth_all.pkl", "wb")
    pickle.dump(avg_depth, a_file)
    a_file.close()
    print("avg_depth = ", avg_depth)

    # INDOORS
    # SUM = 24'445'429.191400826
    # NUM = 6'721'457
    # AVG = 3,6369241358534058910144035735109 (mai multe zecimale)
    # AVG = 3.636924135853406

    # ALL
    # SUM = 138'495'357.49421614
    # NUM = 15'701'297
    # AVG = 8.820631664646312

    # ==> therefore... OUTDOOR
    # NUM = 114'049'928.302815314
    # NUM = 8'979'840
    # NUM = 12,700663742651908497256075832086


def main():
    # save_avg_depth()
    save_avg_depth_from_pkl()


if __name__ == '__main__':
    main()
