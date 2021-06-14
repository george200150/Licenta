import argparse
import pickle
import random

import cv2
import matplotlib.pyplot as plt
import numpy as np
import torch
from PIL import Image
from sklearn.decomposition import PCA, FastICA
from sklearn.neural_network import MLPClassifier
from torchvision import transforms
from sklearn.manifold import TSNE, MDS
from tqdm import tqdm

from ResNeSt.encoding.models.backbone import resnest101, resnest200
# from diode import DIODE, save_plot_dm
from diode import DIODE, save_plot_dm, plot_depth_map

import matplotlib as mpl

from tsne_DIODE import visualize_tsne

mpl.use('TkAgg')


def generate_colors_per_class():
    colors = {}
    colors['indoors'] = [255, 127, 0]
    colors['outdoor'] = [0, 127, 255]

    return colors


# def generate_colors_per_class():
#     scan = 'scan_'
#
#     colors = {}
#     for i in range(240):
#         key = scan + str(i).zfill(5)
#         colors[key] = [i, i, i]
#     return colors


colors_per_class = generate_colors_per_class()
print(colors_per_class)


# Skips empty samples in a batch
def collate_skip_empty(batch):
    batch = [sample for sample in batch if sample]  # check that sample is not None
    return torch.utils.data.dataloader.default_collate(batch)


def fix_random_seeds():
    seed = 10
    random.seed(seed)
    torch.manual_seed(seed)
    np.random.seed(seed)


def get_features(data_root, meta_fname, batch, num_images):
    # move the input and model to GPU for speed if available
    if torch.cuda.is_available():
        device = 'cuda'
    else:
        device = 'cpu'

    # initialize our implementation of ResNet
    # model = resnest101(pretrained=True)
    model = resnest200(pretrained=True)
    model.eval()
    model.to(device)

    # read the dataset and initialize the data loader
    # dataset = DIODE(dataset, num_images)
    dataset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'],
                    num_images=num_images)
    dataloader = torch.utils.data.DataLoader(dataset, batch_size=batch, collate_fn=collate_skip_empty, shuffle=True)

    # we'll store the features as NumPy array of size num_images x feature_size
    features = None

    # we'll also store the image labels and paths to visualize them later
    labels = []
    image_paths = []

    for idx, batch in enumerate(tqdm(dataloader, desc='Running the model inference')):
        im_file, cls, im, dm, dm_valid = batch
        im = torch.tensor(im, dtype=torch.float32)

        newsize = (384, 512)
        # newsize = (192, 256)
        # newsize = (3, 4)
        # dms = []
        # for one_dm, one_dm_valid in zip(dm, dm_valid):
        # save_plot_dm(one_dm, one_dm_valid)

        # one_dm = Image.open("AAA.png")
        # one_dm = one_dm.convert('RGB')
        # one_dm = one_dm.resize(newsize)  # limited GPU resources
        # one_dm = transforms.ToTensor()(one_dm)
        # dms.append(one_dm)
        # dms = torch.cat(dms, dim=1)

        # print(dms)
        # im = dms

        # im = torch.reshape(im, [64, 3, 768, 1024])  # cuda ran out of memory...
        # im = torch.reshape(im, [64, 3, 192, 256])
        # im = torch.reshape(im, [1, 3, 192, 256])
        try:
            im = torch.reshape(im, [32, 3, 384, 512])
        except RuntimeError as err:
            dim = int(str(err).split(" ")[-1])
            batch_size = dim // 3 // 384 // 512
            im = torch.reshape(im, [batch_size, 3, 384, 512])
            pass
        # im = torch.reshape(im, [1, 3, 3, 4])  # 1/10/32/64 is the batch size
        images = im.to(device)
        # idk what I am doing
        # labels.append(cls)  # append for batchsize == 1

        # labels += list(cls)  # extend / += for batchsize >= 2
        labels += list(cls)  # extend / += for batchsize >= 2

        # image_paths.append(im_file)
        image_paths += list(im_file)

        with torch.no_grad():
            # print(images.size())
            output = model.forward(images)

        current_features = output.cpu().numpy()
        if features is not None:
            features = np.concatenate((features, current_features))
        else:
            features = current_features

    # print(features)
    # print(labels)
    # print(image_paths)

    a_file = open("features_labels_imagepaths_resnest200_256.pkl", "wb")
    pickle.dump(zip(features, labels, image_paths), a_file)
    a_file.close()

    return features, labels, image_paths


# scale and move the coordinates so they fit [0; 1] range
def scale_to_01_range(x):
    # compute the distribution range
    value_range = (np.max(x) - np.min(x))

    # move the distribution so that it starts from zero
    # by extracting the minimal value from all its values
    starts_from_zero = x - np.min(x)

    # make the distribution fit [0; 1] by dividing by its range
    return starts_from_zero / value_range


def scale_image(image, max_image_size):
    image_height, image_width, _ = image.shape

    scale = max(1, image_width / max_image_size, image_height / max_image_size)
    image_width = int(image_width / scale)
    image_height = int(image_height / scale)

    image = cv2.resize(image, (image_width, image_height))
    return image


def draw_rectangle_by_class(image, label):
    image_height, image_width, _ = image.shape

    # get the color corresponding to image class
    color = colors_per_class[label]
    image = cv2.rectangle(image, (0, 0), (image_width - 1, image_height - 1), color=color, thickness=5)

    return image


def compute_plot_coordinates(image, x, y, image_centers_area_size, offset):
    image_height, image_width, _ = image.shape

    # compute the image center coordinates on the plot
    center_x = int(image_centers_area_size * x) + offset

    # in matplotlib, the y axis is directed upward
    # to have the same here, we need to mirror the y coordinate
    center_y = int(image_centers_area_size * (1 - y)) + offset

    # knowing the image center, compute the coordinates of the top left and bottom right corner
    tl_x = center_x - int(image_width / 2)
    tl_y = center_y - int(image_height / 2)

    br_x = tl_x + image_width
    br_y = tl_y + image_height

    return tl_x, tl_y, br_x, br_y


def visualize_tsne_images(tx, ty, tz, images, labels, plot_size=1000, max_image_size=100):
    # we'll put the image centers in the central area of the plot
    # and use offsets to make sure the images fit the plot
    offset = max_image_size // 2
    image_centers_area_size = plot_size - 2 * offset

    tsne_plot = 255 * np.ones((plot_size, plot_size, 3), np.uint8)

    # now we'll put a small copy of every image to its corresponding T-SNE coordinate
    for image_path, label, x, y in tqdm(
            zip(images, labels, tx, ty),
            desc='Building the T-SNE plot',
            total=len(images)
    ):
        image = cv2.imread(image_path)

        # scale the image to put it to the plot
        image = scale_image(image, max_image_size)

        # draw a rectangle with a color corresponding to the image class
        image = draw_rectangle_by_class(image, label)

        # compute the coordinates of the image on the scaled plot visualization
        tl_x, tl_y, br_x, br_y = compute_plot_coordinates(image, x, y, image_centers_area_size, offset)

        # put the image to its TSNE coordinates using numpy subarray indices
        tsne_plot[tl_y:br_y, tl_x:br_x, :] = image

    plt.imshow(tsne_plot[:, :, ::-1])
    plt.show()


def visualize_tsne_points(tx, ty, tz, labels):
    # initialize matplotlib plot
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    # fig = plt.figure()
    # ax = fig.add_subplot(111)

    # for every class, we'll add a scatter plot separately
    for label in colors_per_class:
        # find the samples of the current class in the data
        indices = [i for i, l in enumerate(labels) if l == label]

        # extract the coordinates of the points of this class only
        current_tx = np.take(tx, indices)
        current_ty = np.take(ty, indices)
        current_tz = np.take(tz, indices)

        # convert the class color to matplotlib format:
        # BGR -> RGB, divide by 255, convert to np.array
        color = np.array([colors_per_class[label][::-1]], dtype=np.float) / 255

        # add a scatter plot with the corresponding color and label
        ax.scatter(current_tx, current_ty, current_tz, c=color, label=label)

    # build a legend using the labels we set previously
    ax.legend(loc='best')

    # finally, show the plot
    plt.show()


def visualize_3d_tsne(tsne, images, labels, plot_size=1000, max_image_size=100):
    # extract x and y coordinates representing the positions of the images on T-SNE plot
    tx = tsne[:, 0]
    ty = tsne[:, 1]
    tz = tsne[:, 2]

    # scale and move the coordinates so they fit [0; 1] range
    tx = scale_to_01_range(tx)
    ty = scale_to_01_range(ty)
    tz = scale_to_01_range(tz)

    # visualize the plot: samples as colored points
    visualize_tsne_points(tx, ty, tz, labels)

    # visualize the plot: samples as images
    visualize_tsne_images(tx, ty, tz, images, labels, plot_size=plot_size, max_image_size=max_image_size)


def main_TSNE():
    parser = argparse.ArgumentParser()

    parser.add_argument('--path', type=str, default='C:/Users/George/Datasets/DIODE/Depth/')
    parser.add_argument('--meta', type=str, default='C:/Users/George/Datasets/DIODE/diode_meta.json')
    parser.add_argument('--batch', type=int, default=32)
    parser.add_argument('--num_images', type=int, default=256)
    # parser.add_argument('--num_images', type=int, default=64)
    args = parser.parse_args()

    fix_random_seeds()

    # features, labels, image_paths = get_features(
    #     data_root=args.path,
    #     meta_fname=args.meta,
    #     batch=args.batch,
    #     num_images=args.num_images
    # )
    features, labels, image_paths = get_feature_depths(
        data_root=args.path,
        meta_fname=args.meta,
        batch=args.batch,
        num_images=args.num_images
    )

    # print("features = ", features)
    # print("labels = ", labels)
    # print("image_paths = ", image_paths)

    # tsne = TSNE(n_components=3).fit_transform(features)
    tsne = TSNE(n_components=3).fit_transform(features.reshape(-1, 1))  # this is for one single feature

    visualize_3d_tsne(tsne, image_paths, labels)


def main_PCA():
    parser = argparse.ArgumentParser()

    parser.add_argument('--path', type=str, default='C:/Users/George/Datasets/DIODE/Depth/')
    parser.add_argument('--meta', type=str, default='C:/Users/George/Datasets/DIODE/diode_meta.json')
    parser.add_argument('--batch', type=int, default=32)
    parser.add_argument('--num_images', type=int, default=256)
    args = parser.parse_args()

    fix_random_seeds()

    features, labels, image_paths = get_features(
        data_root=args.path,
        meta_fname=args.meta,
        batch=args.batch,
        num_images=args.num_images
    )

    # print("features = ", features)
    # print("labels = ", labels)
    # print("image_paths = ", image_paths)

    pca = PCA(n_components=3).fit_transform(features)

    visualize_3d_tsne(pca, image_paths, labels)


# from sklearn_som.som import SOM

def main_load():
    a_file = open("features_labels_imagepaths_resnest200_2048.pkl", "rb")
    # a_file = open("features_labels_imagepaths_resnest200_256.pkl", "rb")
    values = pickle.load(a_file)
    features, labels, image_paths = zip(*values)

    result = TSNE(n_components=3).fit_transform(features)
    # result = PCA(n_components=3).fit_transform(features)
    # result = MDS(n_components=3).fit_transform(features)
    # result = SOM(m=3, n=1, dim=3).fit_transform(features)
    # result = FastICA(n_components=3).fit_transform(features)

    # TODO: compare results with sklearn.neural_network.MLPClassifier

    visualize_3d_tsne(result, image_paths, labels)
    # visualize_tsne(result, image_paths, labels)


def inverse(x):
    if x:
        return 'outdoor'
    else:
        return 'indoors'


def main_load_MLP():
    a_file = open("features_labels_imagepaths_resnest200_1024.pkl", "rb")
    # a_file = open("features_labels_imagepaths_resnest200_256.pkl", "rb")
    values = pickle.load(a_file)
    features, labels, image_paths = zip(*values)

    clf = MLPClassifier(solver='adam', hidden_layer_sizes=(100, 50, 2), max_iter=1000, random_state=1)
    clf.fit(features, [int(x == 'outdoor') for x in labels])
    result = clf.predict(features)

    result = [inverse(x) for x in result]  # consider outdoor == 1
    tp = 0
    tn = 0
    fp = 0
    fn = 0
    for real, expected in zip(result, labels):
        if real == expected and real == 'outdoor':
            tp += 1
        elif real == expected and real == 'indoor':
            tn += 1
        elif real != expected and real == 'outdoor':
            fp += 1
        elif real == expected and real == 'indoor':
            fn += 1
    print("TP = {}\tFP = {}".format(tp, fp))
    print("FN = {}\tTN = {}".format(fn, tn))
    # assert result == labels


def get_feature_RGBD(data_root, meta_fname, batch, num_images):
    if torch.cuda.is_available():
        device = 'cuda'
    else:
        device = 'cpu'

    dataset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'],
                    num_images=num_images)
    dataloader = torch.utils.data.DataLoader(dataset, batch_size=batch, collate_fn=collate_skip_empty, shuffle=True)

    features = None
    labels = []
    image_paths = []

    for idx, batch in enumerate(tqdm(dataloader, desc='Extracting RGB-D features')):
        im_file, cls, im, dm, dm_valid = batch
        im = torch.tensor(im, dtype=torch.float32)

        pass
    current_features = torch.flatten(list_new_dm)
    if features is not None:
        features = np.concatenate((features, current_features))
    else:
        features = current_features

    return features, labels, image_paths
    pass

def get_feature_depths(data_root, meta_fname, batch, num_images):
    if torch.cuda.is_available():
        device = 'cuda'
    else:
        device = 'cpu'

    # initialize our implementation of ResNet
    # model = resnest101(pretrained=True)
    model = resnest200(pretrained=True)
    model.eval()
    model.to(device)

    # read the dataset and initialize the data loader
    # dataset = DIODE(dataset, num_images)
    dataset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'],
                    num_images=num_images)
    dataloader = torch.utils.data.DataLoader(dataset, batch_size=batch, collate_fn=collate_skip_empty, shuffle=True)

    # we'll store the features as NumPy array of size num_images x feature_size
    features = None

    # we'll also store the image labels and paths to visualize them later
    labels = []
    image_paths = []
    skip_step = 100  # this is for compressing images
    # thr = 8.820631664646312
    # thr = 3.636924135853406
    # thr = 3.6

    for idx, batch in enumerate(tqdm(dataloader, desc='Running the model inference')):
        im_file, cls, im, dm, dm_valid = batch
        im = torch.tensor(im, dtype=torch.float32)

        list_cls = []
        list_new_dm = []
        list_new_dm_valid = []
        for one_dm, one_dm_valid in zip(dm, dm_valid):
            # plot_depth_map(one_dm, one_dm_valid)
            avg_depth = 0
            count = 0

            new_dm = []
            new_dm_valid = []
            # process one depth map
            for row_indx in range(0, len(one_dm), skip_step):
                new_row = []
                new_row_valid = []
                for cell_indx in range(0, len(one_dm[row_indx]), skip_step):
                    new_row.append(one_dm[row_indx][cell_indx])
                    new_row_valid.append(one_dm_valid[row_indx][cell_indx])
                    if one_dm_valid[row_indx][cell_indx]:
                        avg_depth += one_dm[row_indx][cell_indx]
                        count += 1
                new_dm.append(new_row)
                new_dm_valid.append(new_row_valid)

            # NOT RIGHT NOW ! (because we will transform them later)
            # new_dm = torch.FloatTensor(new_dm)
            # new_dm_valid = torch.FloatTensor(new_dm_valid)
            # NOT RIGHT NOW !

            # print(len(new_dm))
            # print(len(new_dm[0]))
            # new_dm = transforms.ToPILImage(new_dm)
            # new_dm.show()
            # plt.imshow(new_dm)
            # plt.imshow(new_dm_valid)

            plot_depth_map(new_dm, new_dm_valid)
            list_new_dm.append(new_dm)
            list_new_dm_valid.append(new_dm_valid)

            avg = avg_depth / count
            # if avg > thr:
            #     list_cls.append('outdoor')
            # else:
            #     list_cls.append('indoors')

        list_new_dm = torch.FloatTensor(list_new_dm)
        list_new_dm_valid = torch.FloatTensor(list_new_dm_valid)

        # newsize = (384, 512)
        # newsize = (192, 256)
        # newsize = (3, 4)
        # try:
        #     im = torch.reshape(im, [32, 3, 384, 512])
        # except RuntimeError as err:
        #     dim = int(str(err).split(" ")[-1])
        #     batch_size = dim // 3 // 384 // 512
        #     im = torch.reshape(im, [batch_size, 3, 384, 512])
        #     pass
        # images = im.to(device)

        labels += list(cls)
        # labels = list_cls

        image_paths += list(im_file)

        # with torch.no_grad():
        #     # print(images.size())
        #     output = model.forward(images)
        #
        # current_features = output.cpu().numpy()
        print("unflattened: ", list_new_dm.size())
        current_features = torch.flatten(list_new_dm)
        print("flattened: ", current_features.size())
        if features is not None:
            features = np.concatenate((features, current_features))
        else:
            features = current_features

    # print(features)
    # print(labels)
    # print(image_paths)

    # a_file = open("features_labels_imagepaths_resnest200_256.pkl", "wb")
    # pickle.dump(zip(features, labels, image_paths), a_file)
    # a_file.close()

    return features, labels, image_paths


if __name__ == '__main__':
    main_TSNE()
    # main_PCA()
    # main_load()
    # main_load_MLP()
