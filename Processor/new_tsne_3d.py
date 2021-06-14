import argparse
import pickle
import random

import cv2
import matplotlib.pyplot as plt
import numpy as np
import torch
from PIL import Image
from torchvision import transforms
from sklearn.manifold import TSNE
from tqdm import tqdm

from ResNeSt.demo import loadModel, inference, get_mask_pallete
from ResNeSt.encoding.models.backbone import resnest101
# from diode import DIODE, save_plot_dm
from diode import DIODE, save_plot_dm
from tsne_3d import visualize_3d_tsne

import matplotlib as mpl
mpl.use('TkAgg')


def generate_colors_per_class():
    colors = {}
    colors['indoors'] = [255, 127, 0]
    colors['outdoor'] = [0, 127, 255]
    return colors


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

    # TODO: other model...
    model = loadModel()
    model.eval()
    model.to(device)

    # read the dataset and initialize the data loader
    dataset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'], num_images=num_images)
    dataloader = torch.utils.data.DataLoader(dataset, batch_size=batch, collate_fn=collate_skip_empty, shuffle=True)

    # we'll store the features as NumPy array of size num_images x feature_size
    features = None

    # we'll also store the image labels and paths to visualize them later
    labels = []
    image_paths = []

    for idx, batch in enumerate(tqdm(dataloader, desc='Running the model inference')):
        im_file, cls, im, dm, dm_valid = batch

        # TODO: removed this just for testing...
        # im = torch.tensor(im, dtype=torch.float32)

        resized_im = None

        to_img = transforms.ToPILImage()
        to_ten = transforms.ToTensor()

        for one_im in im:

            # print(one_im.size())
            # plt.imshow(one_im)
            # plt.show()

            # TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE
            # one_im = torch.reshape(one_im, [3, 384, 512])
            # print(one_im.size())
            #
            # one_im = to_img(one_im)
            #
            # plt.imshow(one_im)
            # plt.show()
            #
            # # keep 1024:768 ratio (4:3)
            # newsize = (508, 381)
            # # newsize = (1024, 768)
            # # newsize = (1024, 768)
            # # newsize = (256, 192)
            #
            # one_resized = one_im.resize(newsize)
            # # one_resized.show()
            #
            # one_ten = to_ten(one_resized)
            # print(one_ten.size())
            #
            # one_ten = torch.reshape(one_ten, [newsize[0], newsize[1], 3])
            # TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE TODO: RESIZE

            one_ten = []

            my_step_row = 32
            my_step_col = 32
            i = 0
            j = 0

            for row in one_im:
                one_row = []
                if i % my_step_row == 0:
                    for rgb in row:
                        if j % my_step_col == 0:
                            # tup = [int(rgb[0]), int(rgb[1]), int(rgb[2])]
                            red = int(rgb[0])
                            green = int(rgb[1])
                            blue = int(rgb[2])
                            # tup = (0xFF << 24) | (red << 16) | (green << 8) | blue
                            tup = (red << 16) | (green << 8) | blue
                            one_row.append(tup)
                        j += 1
                    one_ten.append(one_row)
                i += 1

            one_ten = torch.IntTensor(one_ten)

            # plt.imshow(one_ten)
            # plt.show()

            if resized_im is not None:
                resized_im = np.concatenate((resized_im, torch.flatten(one_ten)))
            else:
                resized_im = torch.flatten(one_ten)


        labels += list(cls)
        image_paths += list(im_file)

        if features is not None:
            features = np.concatenate((features, resized_im.flatten()))
        else:
            features = resized_im.flatten()

    print(features)
    # print(labels)
    # print(image_paths)

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


def visualize_tsne_images(tx, ty, images, labels, plot_size=1000, max_image_size=100):
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


def visualize_tsne_points(tx, ty, labels):
    # initialize matplotlib plot
    fig = plt.figure()
    ax = fig.add_subplot(111)

    # for every class, we'll add a scatter plot separately
    for label in colors_per_class:
        # find the samples of the current class in the data
        indices = [i for i, l in enumerate(labels) if l == label]

        # extract the coordinates of the points of this class only
        current_tx = np.take(tx, indices)
        current_ty = np.take(ty, indices)

        # convert the class color to matplotlib format:
        # BGR -> RGB, divide by 255, convert to np.array
        color = np.array([colors_per_class[label][::-1]], dtype=np.float) / 255

        # add a scatter plot with the corresponding color and label
        ax.scatter(current_tx, current_ty, c=color, label=label)

    # build a legend using the labels we set previously
    ax.legend(loc='best')

    # finally, show the plot
    plt.show()


def visualize_tsne(tsne, images, labels, plot_size=1000, max_image_size=100):
    # extract x and y coordinates representing the positions of the images on T-SNE plot
    tx = tsne[:, 0]
    ty = tsne[:, 1]

    # scale and move the coordinates so they fit [0; 1] range
    tx = scale_to_01_range(tx)
    ty = scale_to_01_range(ty)

    # visualize the plot: samples as colored points
    visualize_tsne_points(tx, ty, labels)

    # visualize the plot: samples as images
    visualize_tsne_images(tx, ty, images, labels, plot_size=plot_size, max_image_size=max_image_size)


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument('--path', type=str, default='C:/Users/George/Datasets/DIODE/Depth/')
    parser.add_argument('--meta', type=str, default='C:/Users/George/Datasets/DIODE/diode_meta.json')
    parser.add_argument('--batch', type=int, default=4)
    # parser.add_argument('--num_images', type=int, default=512)
    parser.add_argument('--num_images', type=int, default=32)
    args = parser.parse_args()

    fix_random_seeds()

    features, labels, image_paths = get_features(
        data_root=args.path,
        meta_fname=args.meta,
        batch=args.batch,
        num_images=args.num_images
    )

    print("features = ", features)
    print("features.size = ", features.size)
    print("labels = ", labels)
    print("image_paths = ", image_paths)

    tsne = TSNE(n_components=3).fit_transform(features.reshape(-1, 1))
    visualize_3d_tsne(tsne, image_paths, labels)

    a_file = open("tsne_2.pkl", "wb")
    pickle.dump(tsne, a_file)
    a_file.close()


if __name__ == '__main__':
    main()
