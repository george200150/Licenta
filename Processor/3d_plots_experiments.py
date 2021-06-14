# %matplotlib notebook
# import matplotlib.pyplot as plt
# from mpl_toolkits.mplot3d import Axes3D
# import numpy as np
# from scipy.stats import multivariate_normal
#
# X = np.linspace(-5,5,50)
# Y = np.linspace(-5,5,50)
# X, Y = np.meshgrid(X,Y)
# X_mean = 0; Y_mean = 0
# X_var = 5; Y_var = 8
#
# pos = np.empty(X.shape+(2,))
# pos[:,:,0]=X
# pos[:,:,1]=Y
# rv = multivariate_normal([X_mean, Y_mean],[[X_var, 0], [0, Y_var]])
#
# fig = plt.figure()
# ax = fig.add_subplot(111, projection='3d')
# ax.plot_surface(X, Y, rv.pdf(pos), cmap="plasma")
# plt.show()

import matplotlib as mpl
# mpl.use('Qt5Agg')
mpl.use('TkAgg')

import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

import numpy as np


def main():
    np.random.seed(42)
    xs = np.random.random(100) * 10 + 20
    ys = np.random.random(100) * 5 + 7
    zs = np.random.random(100) * 15 + 50

    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    ax.scatter(xs, ys, zs)
    ax.scatter(xs, ys, zs, marker="x", c="red")

    ax.set_xlabel("Atomic mass (dalton)")
    ax.set_ylabel("Atomic radius (pm)")
    ax.set_zlabel("Atomic velocity (x10⁶ m/s)")

    plt.show()

    from mpl_toolkits.mplot3d import axes3d

    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')

    # load some test data for demonstration and plot a wireframe
    X, Y, Z = axes3d.get_test_data(0.1)
    ax.plot_wireframe(X, Y, Z, rstride=5, cstride=5)

    # rotate the axes and update
    for angle in range(0, 360):
        ax.view_init(30, angle)
        plt.draw()
        plt.pause(.001)


if __name__ == '__main__':
    main()
