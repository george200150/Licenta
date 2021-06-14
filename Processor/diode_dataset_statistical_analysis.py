import pickle

import matplotlib.pyplot as plt
from PIL import Image
from matplotlib import cm
from tqdm import tqdm
from diode import DIODE, plot_depth_map
import numpy as np

meta_fname = 'C:\\Users\\George\\Datasets\\DIODE\\diode_meta.json'
data_root = 'C:\\Users\\George\\Datasets\\DIODE\\Depth\\'


def show_outlier_images_from_depth_dict():
    depth_dict = {}

    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoors'])
    dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors'])

    for img, dm, mask in tqdm(dset, "analysing data distribution", len(dset)):  # for each image in the dataset...
        has_outliers = False
        for indx, (row_dm, row_mask) in enumerate(zip(dm, mask)):  # for each row...
            if row_mask[indx] == 1.:
                if row_dm[indx] > 20:  # d > 20m
                    has_outliers = True
                try:
                    depth_dict[row_dm[indx]] += 1  # for each value in each row..
                except KeyError:
                    depth_dict[row_dm[indx]] = 1
        if has_outliers:
            im = Image.fromarray(np.uint8(img))
            im.show()
            plot_depth_map(dm, mask)
    # a_file = open("data_distribution_indoors.pkl", "wb")
    # pickle.dump(depth_dict, a_file)
    # a_file.close()
    # print(depth_dict)


def save_depth_dict():
    depth_dict = {}

    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoors'])
    dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors'])

    for _, dm, mask in tqdm(dset, "analysing data distribution", len(dset)):  # for each image in the dataset...
        for indx, (row_dm, row_mask) in enumerate(zip(dm, mask)):  # for each row...
            if row_mask[indx] == 1.:
                try:
                    depth_dict[row_dm[indx]] += 1  # for each value in each row..
                except KeyError:
                    depth_dict[row_dm[indx]] = 1
    a_file = open("data_distribution_indoors.pkl", "wb")
    pickle.dump(depth_dict, a_file)
    a_file.close()
    print(depth_dict)


def plot_data_distribution(filename):
    a_file = open(filename, "rb")
    distribution_dict = pickle.load(a_file)

    print(len(distribution_dict.keys()))
    plt.scatter(distribution_dict.keys(), distribution_dict.values())
    plt.show()
    pass


def group_data(filename, decimal_precision, outfile):
    a_file = open(filename, "rb")
    distribution_dict = pickle.load(a_file)
    coarser_dict = {}
    for k, v in tqdm(distribution_dict.items(), "redistributing data...", len(distribution_dict.keys())):
        try:
            # print(k, '\t', v)
            coarser_dict[round(k, decimal_precision)] += v
        except KeyError:
            coarser_dict[round(k, decimal_precision)] = v
            pass
        pass
    a_file = open(outfile, "wb")
    pickle.dump(coarser_dict, a_file)
    a_file.close()
    pass


def plot_distribution(filename):
    a_file = open(filename, "rb")
    distribution_dict = pickle.load(a_file)
    total_pixels_count = sum(distribution_dict.values())

    buckets = [
        [0, 1.7],
        [1.7, 2.6],
        [2.6, 3.6],
        [3.6, 5.3],
        [5.3, 8.3],
        [8.3, 16.0],
        [16.0, 300],
    ]
    buckets_counter = [0 for _ in range(len(buckets))]
    print(total_pixels_count)

    for k, v in tqdm(distribution_dict.items(), "redistributing data", total_pixels_count):
        for bkt_i, bucket in enumerate(buckets):
            if bucket[0] <= k < bucket[1]:
                print(k, '\t', v)
                buckets_counter[bkt_i] += v

    plt.scatter([x[0] for x in buckets], buckets_counter)
    plt.show()
    print(buckets_counter)

    # bins = [x[0] for x in buckets]
    bins = [0, 2, 4, 6, 8, 10, 16]  # histograms are weird...
    counts = buckets_counter

    print(bins, '\t', counts)

    plt.hist(bins, bins=len(bins) + 1, weights=counts, rwidth=0.5)
    plt.show()
    pass


def create_whole_array(distribution_dict):
    arr = []
    for k, v in tqdm(distribution_dict.items(), "creating array"):
        arr.extend([k] * v)
    return arr


def create_all_depths_array(filename, outfile):
    a_file = open(filename, "rb")
    distribution_dict = pickle.load(a_file)
    arr = create_whole_array(distribution_dict)

    a_file = open(outfile, "wb")
    pickle.dump(arr, a_file)
    a_file.close()

    print(len(arr))  # 15701297 entries
    pass


def compute_quantiles(filename):
    a_file = open(filename, "rb")
    arr = pickle.load(a_file)

    mu = np.mean(arr)
    print("mean of arr : ", mu)

    sigma = np.std(arr)
    print("standard deviation of arr : ", sigma)

    print("Q2 quantile of arr : ", np.quantile(arr, .50))
    print("Q1 quantile of arr : ", np.quantile(arr, .25))
    print("Q3 quantile of arr : ", np.quantile(arr, .75))

    q1_10 = np.quantile(arr, .1)
    print("1/10 : ", q1_10)
    q2_10 = np.quantile(arr, .2)
    print("2/10 : ", q2_10)
    q3_10 = np.quantile(arr, .3)
    print("3/10 : ", q3_10)
    q4_10 = np.quantile(arr, .4)
    print("4/10 : ", q4_10)
    q5_10 = np.quantile(arr, .5)
    print("5/10 : ", q5_10)
    q6_10 = np.quantile(arr, .6)
    print("6/10 : ", q6_10)
    q7_10 = np.quantile(arr, .7)
    print("7/10 : ", q7_10)
    q8_10 = np.quantile(arr, .8)
    print("8/10 : ", q8_10)
    q9_10 = np.quantile(arr, .9)
    print("9/10 : ", q9_10)
    q10_10 = np.quantile(arr, 1.)
    print("10/10 : ", q10_10)

    buckets = [
        [q1_10, q2_10],
        [q2_10, q3_10],
        [q3_10, q4_10],
        [q4_10, q5_10],
        [q5_10, q6_10],
        [q6_10, q7_10],
        [q7_10, q8_10],
        [q8_10, q9_10],
        [q9_10, q10_10]
    ]
    buckets_counter = [0 for _ in range(len(buckets))]

    for k in tqdm(arr, "redistributing data", len(arr)):
        for bkt_i, bucket in enumerate(buckets):
            if bkt_i < len(buckets) - 1:
                if bucket[0] <= k < bucket[1]:
                    buckets_counter[bkt_i] += 1
            else:  # must also consider [x,y] for the last bucket
                if bucket[0] <= k <= bucket[1]:
                    buckets_counter[bkt_i] += 1

    plt.scatter([(x[0] + x[1]) / 2 for x in buckets], buckets_counter)
    plt.show()
    print(buckets_counter)

    fig, ax = plt.subplots()

    num_bins = 9
    n, bins, patches = ax.hist(arr, num_bins, density=True)

    # add a 'best fit' line
    y = ((1 / (np.sqrt(2 * np.pi) * sigma)) *
         np.exp(-0.5 * (1 / sigma * (bins - mu)) ** 2))
    ax.plot(bins, y, '--')
    ax.set_xlabel('Depth')
    ax.set_ylabel('Probability density')

    fig.tight_layout()
    plt.show()


def main2():
    # plot_data_distribution("data_distribution.pkl")
    # group_data("data_distribution.pkl", 1)
    # plot_data_distribution("data_distribution_coarse.pkl")

    # group_data("data_distribution_coarse.pkl", 0, "data_distribution_coarsest.pkl")
    # plot_data_distribution("data_distribution_coarsest.pkl")

    # group_data("correct_depth_dict_val_indoors.pkl", 0, "correct_depth_dict_val_indoors_meters.pkl")
    # print('completed group_data("correct_depth_dict_val_indoors.pkl")')
    # group_data("correct_depth_dict_val_outdoor.pkl", 0, "correct_depth_dict_val_outdoor_meters.pkl")
    # print('completed group_data("correct_depth_dict_val_outdoor.pkl")')
    # group_data("correct_depth_dict_train_indoors.pkl", 0, "correct_depth_dict_train_indoors_meters.pkl")
    # print('completed group_data("correct_depth_dict_val_outdoor.pkl")')
    # group_data("correct_depth_dict_train_outdoor.pkl", 0, "correct_depth_dict_train_outdoor_meters.pkl")

    # plot_proportional_percentage("correct_depth_dict_val_indoors_meters.pkl")
    # print('completed plot_proportional_percentage("correct_depth_dict_val_indoors_meters.pkl")')
    # plot_proportional_percentage("correct_depth_dict_val_outdoor_meters.pkl")
    # print('completed plot_proportional_percentage("correct_depth_dict_val_outdoor_meters.pkl")')
    # plot_proportional_percentage("correct_depth_dict_train_indoors_meters.pkl")
    # print('completed plot_proportional_percentage("correct_depth_dict_train_indoors_meters.pkl")')
    # plot_proportional_percentage("correct_depth_dict_train_outdoor_meters.pkl")

    plot_proportional_percentage_from_list(["correct_depth_dict_val_indoors_meters.pkl", "correct_depth_dict_val_outdoor_meters.pkl"])
    plot_proportional_percentage_from_list(["correct_depth_dict_train_indoors_meters.pkl", "correct_depth_dict_train_outdoor_meters.pkl"])

    # plot_proportional_percentage_from_list(["correct_depth_dict_val_indoors_meters.pkl", "correct_depth_dict_train_indoors_meters.pkl"])
    # plot_proportional_percentage_from_list(["correct_depth_dict_val_outdoor_meters.pkl", "correct_depth_dict_train_outdoor_meters.pkl"])

    # plot_distribution("data_distribution_coarse.pkl")
    pass


def plot_proportional_percentage_from_list(list):
    a_file = open(list[0], "rb")
    b_file = open(list[1], "rb")
    distribution_dict = pickle.load(a_file)
    distribution_dict_b = pickle.load(b_file)
    total_pixels_count = sum(distribution_dict.values())
    total_pixels_count_b = sum(distribution_dict_b.values())


    total_total_pixels_count = total_pixels_count + total_pixels_count_b

    print("total_total_pixels_count = ", total_total_pixels_count)

    # first, we merge the two dicts...

    for k, v in tqdm(distribution_dict_b.items(), "plotting data...", total_pixels_count_b):
        try:
            distribution_dict[k] += v
        except KeyError:
            distribution_dict[k] = v
        pass

    x = []
    y = []

    for k, v in tqdm(distribution_dict.items(), "plotting data...", total_total_pixels_count):
        x.append(k)
        y.append(v * 100 / total_total_pixels_count)
        pass

    print("completed tqdm...")

    something = sorted(zip(x, y), key=lambda a: a[0])

    sorted_x, sorted_y = zip(*something)  # zip(*smth) = inverse operation

    print("completed sort...")

    plot_histo(sorted_x, sorted_y)
    pass


def plot_proportional_percentage(filename):
    """
    total ... 100
    bucket .. x
    -------------------------
    x = bucket * 100 / total
    """
    a_file = open(filename, "rb")
    distribution_dict = pickle.load(a_file)
    total_pixels_count = sum(distribution_dict.values())

    print("total_pixels_count = ", total_pixels_count)

    x = []
    y = []

    for k, v in tqdm(distribution_dict.items(), "plotting data...", total_pixels_count):
        x.append(k)
        y.append(v * 100 / total_pixels_count)
        pass

    print("completed tqdm...")

    something = sorted(zip(x, y), key=lambda a: a[0])

    sorted_x, sorted_y = zip(*something)  # zip(*smth) = inverse operation

    print("completed sort...")

    plot_histo(sorted_x, sorted_y)
    pass


def plot_histo(unnorm_bins, counts):
    bins = unnorm_bins

    fig, ax = plt.subplots()

    plt.hist(bins, bins=len(bins) + 1, weights=counts)
    ax.set_xlabel('Depth')
    ax.set_ylabel('Percentage')
    plt.show()


def main():
    depth_dict = {}

    dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'])

    print(dset.classes)

    for _, dm, mask in tqdm(dset, "analysing data distribution", len(dset)):  # for each image in the dataset...
        for indx, (row_dm, row_mask) in enumerate(zip(dm, mask)):  # for each row...
            if row_mask[indx] == 1.:
                try:
                    depth_dict[row_dm[indx]] += 1  # for each value in each row..
                except KeyError:
                    depth_dict[row_dm[indx]] = 1


if __name__ == '__main__':
    # main()
    main2()
    # a_file = open("data_distribution_indoors.pkl", "rb")
    # distribution_dict = pickle.load(a_file)
    # print(sorted(distribution_dict.keys()))
    # plot_data_distribution("data_distribution_indoors.pkl")
    # plot_proportional_percentage("data_distribution_indoors_coarse1.pkl")
    # group_data("data_distribution_indoors.pkl", 1, "data_distribution_indoors_coarse1.pkl")
    # show_outlier_images_from_depth_dict()

    # save_depth_dict()


    # create_all_depths_array("data_distribution.pkl", "array_all.pkl")
    # compute_quantiles("array_all.pkl")

"""
mean of arr :  8.820628
standard deviation of arr :  12.665295
Q2 quantile of arr :  4.331818580627441
Q1 quantile of arr :  2.3449740409851074
Q3 quantile of arr :  9.811925888061523
1/10 :  1.3850451707839966
2/10 :  2.007805871963501
3/10 :  2.6860893249511717
4/10 :  3.3946206092834474
5/10 :  4.331818580627441
6/10 :  5.728557872772217
7/10 :  8.012918472290039
8/10 :  12.304681205749517
9/10 :  21.626339721679688
redistributing data:   0%|          | 0/15701297 [00:00<?, ?it/s]10/10 :  299.0405578613281
redistributing data: 100%|██████████| 15701297/15701297 [03:34<00:00, 73363.59it/s]
[1570130, 1570129, 1570130, 1570129, 1570130, 1570128, 1570131, 1570130, 1570130]

Process finished with exit code 0
"""

# https://github.com/spmallick/learnopencv/blob/master/README.md
# la un prim gand, am putea compara fiecare model de segmentare cum encodeaza feature-urile
# si cat de bun calitativ este clustering-ul setului de date...
# voi incerca sa vad cum impart setul de date sa para a fi unul de clasificare...


# am mai citit chestii despre tsne, regresie si clasificare...
# mi-am dat seama ca ar trebui sa ma concentrez pe problemele de regresie vs clasifiecare
# si de cum as putea sa convertesc o problema de regresie la clasificare.
# Raspunsul a fost unul direct, anume sa fac din intervale de depth clase.
# Dar, problema a fost ca nu am o singura clasa in imagine, ci este multilabel clasification.
# Asa ca am decis sa caut mai multe despre multilabel clasification si tsne, sau alte metode.
# Am gasit un articol despre multi-label classification t-sne visualisation...
# sci-hub is great - https://sci-hub.mksa.top/10.1016/j.engappai.2019.01.015
