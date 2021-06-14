import math
import pickle

import matplotlib.pyplot as plt
from PIL import Image
from matplotlib import cm
from tqdm import tqdm
from diode_original_plus import DIODE
import numpy as np

meta_fname = 'C:\\Users\\George\\Datasets\\DIODE\\diode_meta.json'
data_root = 'C:\\Users\\George\\Datasets\\DIODE\\Depth\\'


def compute_and_save_data():
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'])
    dset = DIODE(meta_fname, data_root, splits=['train'], scene_types=['indoors', 'outdoor'])
    # dset = DIODE(meta_fname, data_root, splits=['val'], scene_types=['indoors'])
    # dset = DIODE(meta_fname, data_root, splits=['val'], scene_types=['outdoor'])
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors'])
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['outdoor'])

    photo_data_string_csv = []
    rnd = 0
    for im, dm, mask, label, name in tqdm(dset, "computing mean rgb", len(dset)):  # for each image in the dataset...
        depth_value = 0
        red_value = 0
        green_value = 0
        blue_value = 0
        count_valid = 0
        count_all = 0

        if rnd % 100 == 0:  # this is for increased speed
            for row_im, row_dm, row_mask in zip(im, dm, mask):  # for each row...

                for cell_im, cell_dm, cell_mask in zip(row_im, row_dm, row_mask):
                    count_all += 1
                    if cell_mask == 1.:
                        depth_value += cell_dm  # for each value in each row..
                        count_valid += 1
                    # print(row_im[indx])
                    red_value += cell_im[0]
                    green_value += cell_im[1]
                    blue_value += cell_im[2]
            avg_r = red_value / count_all
            avg_g = green_value / count_all
            avg_b = blue_value / count_all
            avg_d = depth_value / count_valid
            string_csv = name + "," + label + "," + str(avg_r) + "," + str(avg_g) + "," + str(avg_b) + "," + str(
                avg_d) + "," + str(count_valid) + "\n"
            photo_data_string_csv.append(string_csv)
        rnd += 1  # this is for faster computation

    # write data in .csv file
    a_file = open("photo_data.csv", "w")
    # a_file = open("photo_data.csv", "a")  # for appending other half of the data
    a_file.write("name,label,avg_red,avg_green,avg_blue,avg_depth,valid_pixels\n")
    # pickle.dump(photo_data, a_file)
    a_file.writelines(photo_data_string_csv)
    a_file.close()


def compute_and_save_data_subimages4():
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'])
    dset = DIODE(meta_fname, data_root, splits=['train'], scene_types=['indoors', 'outdoor'])
    # dset = DIODE(meta_fname, data_root, splits=['val'], scene_types=['indoors', 'outdoor'])
    # dset = DIODE(meta_fname, data_root, splits=['val'], scene_types=['indoors'])
    # dset = DIODE(meta_fname, data_root, splits=['val'], scene_types=['outdoor'])
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors'])
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['outdoor'])

    photo_data_string_csv = []
    rnd = 0
    skip_step = 100
    for im, dm, mask, label, name in tqdm(dset, "computing mean rgb", len(dset)):  # for each image in the dataset...
        depth_value = [0, 0, 0, 0]  # split the image into 4 equal subimages of 512x384
        red_value = [0, 0, 0, 0]
        green_value = [0, 0, 0, 0]
        blue_value = [0, 0, 0, 0]
        count_valid = [0, 0, 0, 0]
        count_all = [0, 0, 0, 0]

        if rnd % skip_step == 0:  # this is for increased speed
            avg_r = [0, 0, 0, 0]
            avg_g = [0, 0, 0, 0]
            avg_b = [0, 0, 0, 0]
            avg_d = [0, 0, 0, 0]
            for i_indx, (row_im, row_dm, row_mask) in enumerate(zip(im, dm, mask)):  # for each row...
                for j_indx, (cell_im, cell_dm, cell_mask) in enumerate(zip(row_im, row_dm, row_mask)):
                    index = None
                    if i_indx < 384 and j_indx < 512:  # stanga-sus
                        index = 0
                    elif i_indx < 384 and j_indx >= 512:  # dreapta-sus
                        index = 2
                    elif i_indx >= 384 and j_indx < 512:  # stanga-jos
                        index = 1
                    elif i_indx >= 384 and j_indx >= 512:  # dreapta-jos
                        index = 3
                    count_all[index] += 1
                    if cell_mask == 1.:
                        depth_value[index] += cell_dm  # for each value in each row..
                        count_valid[index] += 1
                    # print(row_im[indx])
                    red_value[index] += cell_im[0]
                    green_value[index] += cell_im[1]
                    blue_value[index] += cell_im[2]
            string_csv = name + "," + label + ","
            for i in range(0, 4):
                avg_r[i] = red_value[i] / count_all[i]
                avg_g[i] = green_value[i] / count_all[i]
                avg_b[i] = blue_value[i] / count_all[i]
                try:
                    avg_d[i] = depth_value[i] / count_valid[i]
                except ZeroDivisionError:
                    avg_d[i] = -1
                string_csv += str(avg_r[i]) + "," + str(avg_g[i]) + "," + str(avg_b[i]) + "," + str(avg_d[i]) + "," + str(count_valid[i]) + ","
            string_csv = string_csv[:-1]
            string_csv += "\n"
            photo_data_string_csv.append(string_csv)
        rnd += 1  # this is for faster computation

    # write data in .csv file
    a_file = open("photo_data_split4.csv", "w")
    # a_file = open("photo_data.csv", "a")  # for appending other half of the data
    a_file.write("name,label,avg_red1,avg_green1,avg_blue1,avg_depth1,valid_pixels1,avg_red2,avg_green2,avg_blue2,avg_depth2,valid_pixels2,avg_red3,avg_green3,avg_blue3,avg_depth3,valid_pixels3,avg_red4,avg_green4,avg_blue4,avg_depth4,valid_pixels4\n")
    # pickle.dump(photo_data, a_file)
    a_file.writelines(photo_data_string_csv)
    a_file.close()


def compute_and_save_data_subimages16():
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'])
    dset = DIODE(meta_fname, data_root, splits=['train'], scene_types=['indoors', 'outdoor'])
    # dset = DIODE(meta_fname, data_root, splits=['val'], scene_types=['indoors', 'outdoor'])

    photo_data_string_csv = []
    rnd = 0
    skip_step = 100
    for im, dm, mask, label, name in tqdm(dset, "computing mean rgb", len(dset)):  # for each image in the dataset...
        depth_value = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        # split the image into 16 equal subimages of 256x192
        red_value = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        green_value = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        blue_value = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        count_valid = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        count_all = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

        if rnd % skip_step == 0:  # this is for increased speed
            avg_r = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
            avg_g = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
            avg_b = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
            avg_d = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
            "i: 0-192-384-576-768"
            "j: 0-256-512-768-1024"
            for i_indx, (row_im, row_dm, row_mask) in enumerate(zip(im, dm, mask)):  # for each row...
                for j_indx, (cell_im, cell_dm, cell_mask) in enumerate(zip(row_im, row_dm, row_mask)):
                    index = None
                    # --------------------------------------------------- cluster 1,2;5,6;
                    if i_indx < 384 and j_indx < 512:  # stanga-sus
                        if i_indx < 192 and j_indx < 256:  # stanga-sus
                            index = 1
                        elif i_indx < 192 and j_indx >= 256:  # dreapta-sus
                            index = 5
                        elif i_indx >= 192 and j_indx < 256:  # stanga-jos
                            index = 2
                        elif i_indx >= 192 and j_indx >= 256:  # dreapta-jos
                            index = 6

                    # --------------------------------------------------- cluster 3,4;7,8;
                    elif i_indx < 384 and j_indx >= 512:  # dreapta-sus
                        if i_indx < 192 and j_indx < 768:  # stanga-sus
                            index = 3
                        elif i_indx < 192 and j_indx >= 768:  # dreapta-sus
                            index = 7
                        elif i_indx >= 192 and j_indx < 768:  # stanga-jos
                            index = 4
                        elif i_indx >= 192 and j_indx >= 768:  # dreapta-jos
                            index = 8

                    # --------------------------------------------------- cluster 9,10;13,14;
                    elif i_indx >= 384 and j_indx < 512:  # stanga-jos
                        if i_indx < 576 and j_indx < 256:  # stanga-sus
                            index = 9
                        elif i_indx < 576 and j_indx >= 256:  # dreapta-sus
                            index = 13
                        elif i_indx >= 576 and j_indx < 256:  # stanga-jos
                            index = 10
                        elif i_indx >= 576 and j_indx >= 256:  # dreapta-jos
                            index = 14

                    # --------------------------------------------------- cluster 11,12;15,16;
                    elif i_indx >= 384 and j_indx >= 512:  # dreapta-jos
                        if i_indx < 576 and j_indx < 768:  # stanga-sus
                            index = 11
                        elif i_indx < 576 and j_indx >= 768:  # dreapta-sus
                            index = 15
                        elif i_indx >= 576 and j_indx < 768:  # stanga-jos
                            index = 12
                        elif i_indx >= 576 and j_indx >= 768:  # dreapta-jos
                            index = 16

                    index = index - 1

                    count_all[index] += 1
                    if cell_mask == 1.:
                        depth_value[index] += cell_dm  # for each value in each row..
                        count_valid[index] += 1
                    # print(row_im[indx])
                    red_value[index] += cell_im[0]
                    green_value[index] += cell_im[1]
                    blue_value[index] += cell_im[2]
            string_csv = name + "," + label + ","
            for i in range(0, 16):
                avg_r[i] = red_value[i] / count_all[i]
                avg_g[i] = green_value[i] / count_all[i]
                avg_b[i] = blue_value[i] / count_all[i]
                try:
                    avg_d[i] = depth_value[i] / count_valid[i]
                except ZeroDivisionError:
                    avg_d[i] = -1
                string_csv += str(avg_r[i]) + "," + str(avg_g[i]) + "," + str(avg_b[i]) + "," + str(avg_d[i]) + "," + str(count_valid[i]) + ","
            string_csv = string_csv[:-1]
            string_csv += "\n"
            photo_data_string_csv.append(string_csv)
        rnd += 1  # this is for faster computation

    string_header_table = "name,label"
    for i in range(0, 16):
        string_header_table += ",avg_red" + str(i+1) + ",avg_green" + str(i+1) + ",avg_blue" + str(i+1) + ",avg_depth" + str(i+1) + ",valid_pixels" + str(i+1)
    string_header_table += "\n"

    # write data in .csv file
    a_file = open("photo_data_split16.csv", "w")
    # a_file = open("photo_data.csv", "a")  # for appending other half of the data

    # a_file.write(",avg_red1,avg_green1,avg_blue1,avg_depth1,valid_pixels1,avg_red2,avg_green2,avg_blue2,avg_depth2,valid_pixels2,avg_red3,avg_green3,avg_blue3,avg_depth3,valid_pixels3,avg_red4,avg_green4,avg_blue4,avg_depth4,valid_pixels4\n")
    a_file.write(string_header_table)

    # pickle.dump(photo_data, a_file)
    a_file.writelines(photo_data_string_csv)
    a_file.close()


def log(x):
    x = float(x)
    return math.log10(x + 1)


def asinh(x):
    x = float(x)
    return math.log(x + math.sqrt(x * x + 1))


def get_data_from_csv():
    # csv_f = open("photo_data.csv", "r")  # not forgetti to uncomentti your appendi ;P
    # csv_f = open("photo_data_split4_novalid.csv", "r")
    csv_f = open("photo_data_split16_novalid.csv", "r")
    lines = csv_f.readlines()
    header = lines[0]
    lines = lines[1:]
    features = []
    labels = []

    # transform = log
    transform = asinh

    header = header.strip().split(",")[2:]
    test_header_features_ok = []

    for line in lines:
        parts = line.strip().split(",")
        if len(parts) > 0:

            # label, name, avg_red, avg_green, avg_blue, avg_depth, valid_pixels = parts
            # labels.append(parts[0])
            labels.append(parts[1])  # [0] doar pentru photo_data.csv (format prost)
            # labels.append(label)

            data = parts[2:]

            filtered_data = []
            for indx, dat in enumerate(data):
                if "depth" not in header[indx] and "valid" not in header[indx]:
                    filtered_data.append(dat)
                    test_header_features_ok.append(header[indx])
            data = filtered_data


            data = [transform(x) for x in data]
            features.append(data)

            # features.append([transform(avg_red), transform(avg_green), transform(avg_blue), transform(avg_depth)])
            # features.append([transform(avg_red), transform(avg_green), transform(avg_blue)])
            # features.append([avg_red, avg_green, avg_blue, avg_depth])
        else:
            continue

    print(test_header_features_ok)
    return features, labels


# data indoors to outdoor ratio ~ 1:2
# indoors: 8574 + 325 + 753 = 9.652
# outdoor: 16884 + 446 + 876 = 18.206


def visualize_tsne():
    features, labels = get_data_from_csv()
    image_paths = []

    print("features = ", features)
    print("labels = ", labels)
    print("image_paths = ", image_paths)

    from sklearn.manifold import TSNE
    tsne = TSNE(n_components=3,
                perplexity=20,
                learning_rate=3.0,
                n_iter=1000,
                metric='euclidean').fit_transform(features)
    from tsne_3d import visualize_3d_tsne
    visualize_3d_tsne(tsne, image_paths, labels)


def inverse(x):
    if x:
        return 'outdoor'  # consider "outdoor" the positive class
    else:
        return 'indoors'


def main_load_MLP():
    features, labels = get_data_from_csv()

    a = 20
    b = -20

    from sklearn.neural_network import MLPClassifier
    # clf = MLPClassifier(solver='adam', hidden_layer_sizes=(100, 50, 2), max_iter=1000, random_state=1)
    clf = MLPClassifier(solver='adam', hidden_layer_sizes=(150, 100, 2), max_iter=1000, activation='relu')
    clf.fit(features[a:b], [int(x == 'outdoor') for x in labels[a:b]])
    # clf.fit(features, [int(x == 'outdoor') for x in labels])
    val_features = []
    val_features += features[:a]
    val_features += features[b:]
    result = clf.predict(val_features)
    # result = clf.predict(features)

    val_labels = []
    val_labels += labels[:a]
    val_labels += labels[b:]

    result = [inverse(x) for x in result]  # consider outdoor == 1
    tp = 0
    tn = 0
    fp = 0
    fn = 0
    # for real, expected in zip(result, labels):
    for real, expected in zip(result, val_labels):
        if real == expected and real == 'outdoor':
            tp += 1
        elif real == expected and real == 'indoors':
            tn += 1
        elif real != expected and real == 'outdoor':
            fp += 1
        elif real != expected and real == 'indoors':
            fn += 1
        else:
            raise Exception("WHY: real = " + real + "; expected = " + expected)
    print("TP = {}\tFP = {}".format(tp, fp))
    print("FN = {}\tTN = {}".format(fn, tn))

    accuracy = (tp + tn) / (tp + fp + fn + tn)
    try:
        precision = tp / (tp + fp)
    except ZeroDivisionError:
        precision = 0

    try:
        recall = tp / (tp + fn)
    except ZeroDivisionError:
        recall = 0

    try:
        f1_score = 2 * recall * precision / (recall + precision)
    except ZeroDivisionError:
        f1_score = 0
    print("accuracy = {}\tprecision = {}\trecall = {}\tf1_score = {}".format(accuracy, precision, recall, f1_score))
    # assert result == labels


def get_dict_of_depths_of_each_pixel(saveFileName, scene_types):
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'])
    # dset = DIODE(meta_fname, data_root, splits=['train'], scene_types=['indoors', 'outdoor'])
    # dset = DIODE(meta_fname, data_root, splits=['val'], scene_types=['indoors'])
    dset = DIODE(meta_fname, data_root, splits=['train'], scene_types=[scene_types])
    # dset = DIODE(meta_fname, data_root, splits=['val'], scene_types=['outdoor'])
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors'])
    # dset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['outdoor'])

    data_dict = {}
    for _, dm, mask, _, _ in tqdm(dset, "computing mean rgb", len(dset)):  # for each image in the dataset...
        for row_dm, row_mask in zip(dm, mask):  # for each row...
            for cell_dm, cell_mask in zip(row_dm, row_mask):  # for each value in each row..
                if cell_mask == 1.:
                    try:
                        data_dict[cell_dm] += 1
                    except KeyError:
                        data_dict[cell_dm] = 1

    # write data in .pkl file
    a_file = open(saveFileName, "wb")
    pickle.dump(data_dict, a_file)
    a_file.close()


def main():
    # compute_and_save_data()
    # compute_and_save_data_subimages4()
    # compute_and_save_data_subimages16()
    visualize_tsne()
    # main_load_MLP()
    # get_dict_of_depths_of_each_pixel("correct_depth_dict_train_indoors.pkl", 'indoors')
    # computing mean rgb: 100%|██████████| 8574/8574 [5:01:22<00:00,  2.11s/it]
    # get_dict_of_depths_of_each_pixel("correct_depth_dict_train_outdoor.pkl", 'outdoor')
    # computing mean rgb: 100%|██████████| 16884/16884 [9:23:26<00:00,  2.00s/it]


if __name__ == '__main__':
    main()
