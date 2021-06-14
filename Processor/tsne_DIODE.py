import argparse
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
from ResNeSt.encoding.models.backbone import resnest101, resnest200
# from diode import DIODE, save_plot_dm
from diode import DIODE, save_plot_dm


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
    # TODO: other model...
    # model = loadModel()
    # model = resnest101(pretrained=True)
    model = resnest200(pretrained=True)
    model.eval()
    model.to(device)

    # read the dataset and initialize the data loader
    # dataset = DIODE(dataset, num_images)
    dataset = DIODE(meta_fname, data_root, splits=['train', 'val'], scene_types=['indoors', 'outdoor'], num_images=num_images)
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
            print(images.size())
            output = model.forward(images)

            # # TODO: copied from inference code
            # my_image = images[0].cuda().unsqueeze(0)
            #
            # output = model.evaluate(my_image)
            # predict = torch.max(output, 1)[1].cpu().numpy() + 1
            #
            # mask = get_mask_pallete(predict, 'ade20k')  # this should change when using DE

            # mask.show()
            # plt.plot(images[0].cpu().numpy()[0])
            # plt.show()

            # something = output[0]
            # something_else = something[0]
            # my_img = images[0]
            #
            # trans = transforms.ToPILImage()
            # trans1 = transforms.ToTensor()
            #
            # img = my_img.cpu().numpy()[0]
            # # img = np.transpose(img, (1, 2, 0))
            # # show the image
            # plt.imshow(img)
            # plt.show()
            #
            # plt.imshow(trans(trans1(my_img).convert("RGB")))
            #
            # width, height, pred_img = inference(model, transforms.ToPILImage(my_img).convert("RGB"))


        current_features = output.cpu().numpy()


        if features is not None:
            features = np.concatenate((features, current_features))
        else:
            features = current_features

    # print(features)
    print(labels)
    print(image_paths)

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
    # parser.add_argument('--batch', type=int, default=32)
    parser.add_argument('--batch', type=int, default=4)
    parser.add_argument('--num_images', type=int, default=128)
    args = parser.parse_args()

    fix_random_seeds()

    features, labels, image_paths = get_features(
        data_root=args.path,
        meta_fname=args.meta,
        batch=args.batch,
        num_images=args.num_images
    )

    # print("features = ", features)
    print("features.size = ", features.size)
    print("labels = ", labels)
    print("image_paths = ", image_paths)

    tsne = TSNE(n_components=2).fit_transform(features)

    visualize_tsne(tsne, image_paths, labels)


if __name__ == '__main__':
    main()
