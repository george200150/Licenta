import math
from statistics import stdev, mean


# rgb_1_accuracies = [0.846153846, 0.730769231, 0.538461538, 0.5, 0.692307692, 0.730769231, 0.769230769, 0.653846154, 0.730769231, 0.615384615]
# rgb_1_auc = [0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5]
# rgb_1_specificity = [1., 1., 1., 1., 1., 1., 1., 1., 1., 1.]
# rgb_1_recall = [0., 0., 0., 0., 0., 0., 0., 0., 0., 0., ]
rgb_1_accuracies = [0.846153846, 0.730769231, 0.5, 0.5, 0.846153846, 0.730769231, 0.769230769, 0.653846154, 0.730769231, 0.615384615]
rgb_1_auc = [0.5, 0.5, 0.470238095, 0.5, 0.784722222, 0.5, 0.5, 0.5, 0.5, 0.5]
rgb_1_specificity = [1.0, 1.0, 0.857142857, 1.0, 0.944444444, 1.0, 1.0, 1.0, 1.0, 1.0]
rgb_1_recall = [0.0, 0.0, 0.083333333, 0.0, 0.625, 0.0, 0.0, 0.0, 0.0, 0.0]

rgb_4_accuracies = [0.846153846, 0.730769231, 0.538461538, 0.5, 0.692307692, 0.769230769, 0.730769231, 0.692307692, 0.730769231, 0.653846154]
rgb_4_auc = [0.5, 0.5, 0.5, 0.5, 0.5, 0.571428571, 0.475, 0.581699346, 0.5, 0.55]
rgb_4_specificity = [1., 1., 1., 1., 1., 1., 0.95, 0.941176471, 1., 1.]
rgb_4_recall = [0., 0., 0., 0., 0., 0.142857143, 0., 0.222222222, 0., 0.1]

rgb_16_accuracies = [0.730769231, 0.730769231, 0.615384615, 0.5, 0.692307692, 0.730769231, 0.615384615, 0.653846154, 0.769230769, 0.653846154]
rgb_16_auc = [0.636363636, 0.5, 0.607142857, 0.5, 0.5, 0.571428571, 0.4, 0.526143791, 0.661654135, 0.55]
rgb_16_specificity = [0.772727273, 1., 0.714285714, 1., 1., 1., 0.8, 0.941176471, 0.894736842, 1.]
rgb_16_recall = [0.5, 0., 0.5, 0., 0., 0., 0., 0.111111111, 0.428571429, 0.1]


rgbd_1_accuracies = [0.961538462, 0.884615385, 0.807692308, 0.769230769, 0.923076923, 0.923076923, 0.923076923, 0.923076923, 0.884615385, 0.807692308]
rgbd_1_auc = [0.875, 0.785714286, 0.803571429, 0.769230769, 0.944444444, 0.902255639, 0.95, 0.91503268, 0.830827068, 0.80625]
rgbd_1_specificity = [1., 1., 0.857142857, 0.692307692, 0.888888889, 0.947368421, 0.9, 0.941176471, 0.947368421, 0.8125]
rgbd_1_recall = [0.75, 0.571428571, 0.75, 0.846153846, 1., 0.857142857, 1., 0.888888889, 0.714285714, 0.8]

rgbd_4_accuracies = [0.923076923, 0.923076923, 0.769230769, 0.769230769, 0.923076923, 0.961538462, 0.923076923, 0.884615385, 0.884615385, 0.807692308]
rgbd_4_auc = [0.852272727, 0.902255639, 0.767857143, 0.769230769, 0.909722222, 0.973684211, 0.95, 0.859477124, 0.830827068, 0.80625]
rgbd_4_specificity = [0.954545455, 0.947368421, 0.785714286, 0.769230769, 0.944444444, 0.947368421, 0.9, 0.941176471, 0.947368421, 0.8125]
rgbd_4_recall = [0.75, 0.857142857, 0.75, 0.769230769, 0.875, 1., 1., 0.777777778, 0.714285714, 0.8]

rgbd_16_accuracies = [0.884615385, 0.846153846, 0.730769231, 0.730769231, 0.923076923, 0.923076923, 0.884615385, 0.846153846, 0.846153846, 0.769230769]
rgbd_16_auc = [0.829545455, 0.804511278, 0.720238095, 0.730769231, 0.944444444, 0.947368421, 0.925, 0.830065359, 0.759398496, 0.775]
rgbd_16_specificity = [0.909090909, 0.894736842, 0.857142857, 0.615384615, 0.888888889, 0.894736842, 0.85, 0.882352941, 0.947368421, 0.75]
rgbd_16_recall = [0.75, 0.714285714, 0.583333333, 0.846153846, 1., 1., 1., 0.777777778, 0.571428571, 0.8]


# DE (before)
# de_dpt_feat_before_accuracies = [0.884615385, 0.961538462, 0.807692308, 0.846153846, 0.884615385, 0.923076923, 0.230769231, 0.923076923, 0.884615385, 0.884615385]
# de_dpt_feat_before_auc = [0.829545455, 0.928571429, 0.803571429, 0.846153846, 0.916666667, 0.902255639, 0.5, 0.888888889, 0.830827068, 0.86875]
# de_dpt_feat_before_specificity = [0.909090909, 1.0, 0.857142857, 0.692307692, 0.833333333, 0.947368421, 0.0, 1.0, 0.947368421, 0.9375]
# de_dpt_feat_before_recall = [0.75, 0.857142857, 0.75, 1.0, 1.0, 0.857142857, 1.0, 0.777777778, 0.714285714, 0.8]

# DE 384 (after)
de_dpt_feat_before_accuracies = [0.923076923, 0.884615385, 0.807692308, 0.5, 0.884615385, 0.884615385, 0.692307692, 0.923076923, 0.346153846, 0.961538462]
de_dpt_feat_before_auc = [0.954545455, 0.785714286, 0.821428571, 0.5, 0.916666667, 0.921052632, 0.8, 0.888888889, 0.552631579, 0.96875]
de_dpt_feat_before_specificity = [0.909090909, 1.0, 0.642857143, 1.0, 0.833333333, 0.842105263, 0.6, 1.0, 0.105263158, 0.9375]
de_dpt_feat_before_recall = [1.0, 0.571428571, 1.0, 0.0, 1.0, 1.0, 1.0, 0.777777778, 1.0, 1.0]


# this was SS with w=h=480 (which cannot be compared to 384 as easily)
# ss_dpt_feat_before_accuracies = [1.0, 0.884615385, 1.0, 0.884615385, 0.961538462, 1.0, 0.923076923, 0.961538462, 0.923076923, 0.961538462]
# ss_dpt_feat_before_auc = [1.0, 0.785714286, 1.0, 0.884615385, 0.972222222, 1.0, 0.891666667, 0.944444444, 0.947368421, 0.96875]
# ss_dpt_feat_before_specificity = [1.0, 1.0, 1.0, 0.846153846, 0.944444444, 1.0, 0.95, 1.0, 0.894736842, 0.9375]
# ss_dpt_feat_before_recall = [1.0, 0.571428571, 1.0, 0.923076923, 1.0, 1.0, 0.833333333, 0.888888889, 1.0, 1.0]

# # TODO: now SS has w=h=384 (before)
# ss_dpt_feat_before_accuracies = [1.0, 0.961538462, 0.884615385, 0.884615385, 0.961538462, 1.0, 0.923076923, 0.961538462, 1.0, 0.961538462]
# ss_dpt_feat_before_auc = [1.0, 0.928571429, 0.875, 0.884615385, 0.972222222, 1.0, 0.891666667, 0.944444444, 1.0, 0.95]
# ss_dpt_feat_before_specificity = [1.0, 1.0, 1.0, 0.846153846, 0.944444444, 1.0, 0.95, 1.0, 1.0, 1.0]
# ss_dpt_feat_before_recall = [1.0, 0.857142857, 0.75, 0.923076923, 1.0, 1.0, 0.833333333, 0.888888889, 1.0, 0.9]

# TODO: now SS has w=h=384 (after)
# ss_dpt_feat_before_accuracies = [0.846153846, 0.923076923, 0.884615385, 0.807692308, 0.884615385, 0.961538462, 0.884615385, 0.961538462, 0.923076923, 0.807692308]
# ss_dpt_feat_before_auc = [0.909090909, 0.947368421, 0.886904762, 0.807692308, 0.916666667, 0.973684211, 0.808333333, 0.944444444, 0.857142857, 0.84375]
# ss_dpt_feat_before_specificity = [0.818181818, 0.894736842, 0.857142857, 0.769230769, 0.833333333, 0.947368421, 0.95, 1.0, 1.0, 0.6875]
# ss_dpt_feat_before_recall = [1.0, 1.0, 0.916666667, 0.846153846, 1.0, 1.0, 0.666666667, 0.888888889, 0.714285714, 1.0]

# ss_dpt_feat_before_accuracies = [1.0, 0.961538462, 0.961538462, 0.923076923, 1.0, 0.961538462, 0.923076923, 0.961538462, 0.961538462, 0.961538462]
# ss_dpt_feat_before_auc = [1.0, 0.928571429, 0.958333333, 0.923076923, 1.0, 0.973684211, 0.891666667, 0.944444444, 0.973684211, 0.96875]
# ss_dpt_feat_before_specificity = [1.0, 1.0, 1.0, 0.923076923, 1.0, 0.947368421, 0.95, 1.0, 0.947368421, 0.9375]
# ss_dpt_feat_before_recall = [1.0, 0.857142857, 0.916666667, 0.923076923, 1.0, 1.0, 0.833333333, 0.888888889, 1.0, 1.0]

ss_dpt_feat_before_accuracies = [1.0, 0.961538462, 0.884615385, 0.884615385, 1.0, 1.0, 0.923076923, 0.961538462, 0.923076923, 0.961538462]
ss_dpt_feat_before_auc = [1.0, 0.928571429, 0.875, 0.884615385, 1.0, 1.0, 0.891666667, 0.944444444, 0.947368421, 0.95]
ss_dpt_feat_before_specificity = [1.0, 1.0, 1.0, 0.846153846, 1.0, 1.0, 0.95, 1.0, 0.894736842, 1.0]
ss_dpt_feat_before_recall = [1.0, 0.857142857, 0.75, 0.923076923, 1.0, 1.0, 0.833333333, 0.888888889, 1.0, 0.9]


def compute_ci(data):
    std_dev = stdev(data)
    n = 10  # (10-fold validation)
    confidence_interval = 1.96 * std_dev / math.sqrt(n)
    print(confidence_interval)


def main():
    print("-------dpt_de-------")
    print("mean acc = ", mean(de_dpt_feat_before_accuracies))
    print("mean auc = ", mean(de_dpt_feat_before_auc))
    print("mean spe = ", mean(de_dpt_feat_before_specificity))
    print("mean rec = ", mean(de_dpt_feat_before_recall))
    compute_ci(de_dpt_feat_before_accuracies)
    compute_ci(de_dpt_feat_before_auc)
    compute_ci(de_dpt_feat_before_specificity)
    compute_ci(de_dpt_feat_before_recall)
    print("--------------------")
    print("-------dpt_ss-------")
    print("-now SS has w=h=384-")
    print("mean acc = ", mean(ss_dpt_feat_before_accuracies))
    print("mean auc = ", mean(ss_dpt_feat_before_auc))
    print("mean spe = ", mean(ss_dpt_feat_before_specificity))
    print("mean rec = ", mean(ss_dpt_feat_before_recall))
    compute_ci(ss_dpt_feat_before_accuracies)
    compute_ci(ss_dpt_feat_before_auc)
    compute_ci(ss_dpt_feat_before_specificity)
    compute_ci(ss_dpt_feat_before_recall)
    print("--------------------")

    print("/////////////////////////////////")

    print("--------rgb1--------")
    print("mean acc = ", mean(rgb_1_accuracies))
    print("mean auc = ", mean(rgb_1_auc))
    print("mean spe = ", mean(rgb_1_specificity))
    print("mean rec = ", mean(rgb_1_recall))
    compute_ci(rgb_1_accuracies)
    compute_ci(rgb_1_auc)
    compute_ci(rgb_1_specificity)
    compute_ci(rgb_1_recall)
    print("--------------------")
    print("--------rgb4--------")
    print("mean acc = ", mean(rgb_4_accuracies))
    print("mean auc = ", mean(rgb_4_auc))
    print("mean spe = ", mean(rgb_4_specificity))
    print("mean rec = ", mean(rgb_4_recall))
    compute_ci(rgb_4_accuracies)
    compute_ci(rgb_4_auc)
    compute_ci(rgb_4_specificity)
    compute_ci(rgb_4_recall)
    print("--------------------")
    print("-------rgb16--------")
    print("mean acc = ", mean(rgb_16_accuracies))
    print("mean auc = ", mean(rgb_16_auc))
    print("mean spe = ", mean(rgb_16_specificity))
    print("mean rec = ", mean(rgb_16_recall))
    compute_ci(rgb_16_accuracies)
    compute_ci(rgb_16_auc)
    compute_ci(rgb_16_specificity)
    compute_ci(rgb_16_recall)
    print("--------------------")

    print("/////////////////////////////////")

    print("--------------------")
    print("-------rgbd1--------")
    print("mean acc = ", mean(rgbd_1_accuracies))
    print("mean auc = ", mean(rgbd_1_auc))
    print("mean spe = ", mean(rgbd_1_specificity))
    print("mean rec = ", mean(rgbd_1_recall))
    compute_ci(rgbd_1_accuracies)
    compute_ci(rgbd_1_auc)
    compute_ci(rgbd_1_specificity)
    compute_ci(rgbd_1_recall)
    print("--------------------")
    print("-------rgbd4--------")
    print("mean acc = ", mean(rgbd_4_accuracies))
    print("mean auc = ", mean(rgbd_4_auc))
    print("mean spe = ", mean(rgbd_4_specificity))
    print("mean rec = ", mean(rgbd_4_recall))
    compute_ci(rgbd_4_accuracies)
    compute_ci(rgbd_4_auc)
    compute_ci(rgbd_4_specificity)
    compute_ci(rgbd_4_recall)
    print("--------------------")
    print("------rgbd16--------")
    print("mean acc = ", mean(rgbd_16_accuracies))
    print("mean auc = ", mean(rgbd_16_auc))
    print("mean spe = ", mean(rgbd_16_specificity))
    print("mean rec = ", mean(rgbd_16_recall))
    compute_ci(rgbd_16_accuracies)
    compute_ci(rgbd_16_auc)
    compute_ci(rgbd_16_specificity)
    compute_ci(rgbd_16_recall)
    print("--------------------")


if __name__ == '__main__':
    main()
