import math
import os
import pickle
import random

import numpy as np
import xlsxwriter
from sklearn import tree, svm
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis
from sklearn.ensemble import AdaBoostClassifier, RandomForestClassifier
from sklearn.linear_model import LogisticRegression, SGDClassifier
from sklearn.metrics import confusion_matrix
from sklearn.model_selection import train_test_split
from sklearn.naive_bayes import GaussianNB, MultinomialNB
from sklearn.neighbors import KNeighborsClassifier
from sklearn.neural_network import MLPClassifier


def log(x):
    x = float(x)
    return math.log10(x + 1)


def asinh(x):
    x = float(x)
    return math.log(x + math.sqrt(x * x + 1))


def get_data_from_csv(data_file_name):
    # not forgetti to uncomentti your appendi ;P
    csv_f = open(data_file_name, "r")
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
                if "valid" not in header[indx]:
                # if "depth" not in header[indx] and "valid" not in header[indx]:
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

    # print(test_header_features_ok)
    features = np.array(features)
    # print(features)
    # print(type(features))

    np.array(labels)

    return features, np.array(labels)

def get_data_from_csv_d(data_file_name):
    # not forgetti to uncomentti your appendi ;P
    csv_f = open(data_file_name, "r")
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
                if "depth" in header[indx]:
                    filtered_data.append(dat)
                    test_header_features_ok.append(header[indx])
            data = filtered_data

            data = [transform(x) for x in data]  # TODO: transform / notransform
            features.append(data)

            # features.append([transform(avg_red), transform(avg_green), transform(avg_blue), transform(avg_depth)])
            # features.append([transform(avg_red), transform(avg_green), transform(avg_blue)])
            # features.append([avg_red, avg_green, avg_blue, avg_depth])
        else:
            continue

    # print(test_header_features_ok)
    features = np.array(features)
    # print(features)
    # print(type(features))

    np.array(labels)

    return features, np.array(labels)


# TODO: ConvergenceWarning: Stochastic Optimizer: Maximum iterations (200) reached and the optimization hasn't converged yet.


def read_data(data_file_name):
    return np.array([[]]), np.array([[]])


def print_categorical_measures(true_data, predicted_data):
    print("Number of points: ", true_data.shape[0])
    print("Number of nonzero points (anomalies) in true data", np.sum(true_data))
    print("Number of nonzero points (anomalies) in prediction", np.sum(predicted_data))
    tp, fn, fp, tn = confusion_matrix(true_data, predicted_data).ravel()
    print("True pozitives: ", tp)
    print("True negatives: ", tn)
    print("False pozitives: ", fp)
    print("False negatives: ", fn)
    print("")
    total = tp + tn + fp + fn
    print("Accuracy: ", (tp + tn) / total)
    recall = tp / (tp + fn)
    specificity = tn / (tn + fp)
    print("Recall: ", recall)
    print("Specificity: ", specificity)
    print("AUC: ", (recall + specificity) / 2)


def write_categorical_measures_Xlsx(worksheet, true_data, predicted_data, lineNumber):
    tp, fn, fp, tn = confusion_matrix(true_data, predicted_data).ravel()
    total = tp + tn + fp + fn
    accuracy = (tp + tn) / total
    recall = tp / (tp + fn)
    specificity = tn / (tn + fp)
    auc = (recall + specificity) / 2
    worksheet.write("A" + str(lineNumber + 2), accuracy)
    worksheet.write("B" + str(lineNumber + 2), auc)
    worksheet.write("C" + str(lineNumber + 2), specificity)
    worksheet.write("D" + str(lineNumber + 2), recall)
    worksheet.write("E" + str(lineNumber + 2), tp)
    worksheet.write("F" + str(lineNumber + 2), fp)
    worksheet.write("G" + str(lineNumber + 2), tn)
    worksheet.write("H" + str(lineNumber + 2), fn)


def compute(classifier, X_train, y_train, X_test, y_test, random_state, worksheet):
    classifier.fit(X_train, y_train)
    predicted = classifier.predict(X_test)
    # print_categorical_measures(y_test, predicted)
    write_categorical_measures_Xlsx(worksheet, y_test, predicted, random_state)


def fix_random_seeds():
    seed = 10
    random.seed(seed)
    np.random.seed(seed)


def cross_validate_classifier(classifier, data, classes, test_size, result_file):
    workbook = xlsxwriter.Workbook(result_file)
    worksheet = workbook.add_worksheet()
    worksheet.write("A1", "accuracy")
    worksheet.write("B1", "auc")
    worksheet.write("C1", "specificity")
    worksheet.write("D1", "recall")
    worksheet.write("E1", "TP")
    worksheet.write("F1", "FP")
    worksheet.write("G1", "TN")
    worksheet.write("H1", "FN")
    for i in range(0, 10):
        print("---------------------------------" + str(i) + "---------------------------------------")
        X_train, X_test = train_test_split(data, test_size=test_size, random_state=i)
        y_train, y_test = train_test_split(classes, test_size=test_size, random_state=i)
        compute(classifier, X_train, y_train, X_test, y_test, i, worksheet)
    workbook.close()


def get_data_from_pkl(filename):
    a_file = open(filename, "rb")
    our_data = pickle.load(a_file)
    features, labels = zip(*our_data)
    a_file.close()

    features = np.array(features)
    labels = np.array(labels)
    return features, labels
    pass


if __name__ == "__main__":
    fix_random_seeds()  # TODO: uncomment this for consistent results

    # classifier_lda = LinearDiscriminantAnalysis()
    # classifier_bayes = GaussianNB()
    # classifier_dectree = tree.DecisionTreeClassifier()
    # classifier_mlp = MLPClassifier(hidden_layer_sizes=(150, 150, 100), max_iter=1000, learning_rate="adaptive")
    # classifier_mlp = MLPClassifier(hidden_layer_sizes=(150, 100), max_iter=1000)
    classifier_mlp = MLPClassifier()
    # classifier_svc = svm.SVC()
    # classifier_mnb = MultinomialNB()
    # classifier_logreg = LogisticRegression()
    # classifier_knn = KNeighborsClassifier()
    # classifier_ada = AdaBoostClassifier()
    # classifier_sgd = SGDClassifier()
    # classifier_rndfor = RandomForestClassifier()

    # classifiers = {'lda': classifier_lda, 'bayes': classifier_bayes, 'dectree': classifier_dectree,
    #                'mlp': classifier_mlp, 'svc': classifier_svc, 'mnb': classifier_mnb,
    #                'logreg': classifier_logreg, 'knn': classifier_knn, 'ada': classifier_ada,
    #                'sgd': classifier_sgd, 'rndfor': classifier_rndfor}
    classifiers = {'mlp': classifier_mlp}

    # TODO: this is for RGB
    result_paths = {'lda': "results/lda/", 'bayes': "results/bayes/", 'dectree': "results/dectree/",
                    'mlp': "results/mlp/", 'svc': "results/svc/", 'mnb': "results/mnb/",
                    'logreg': "results/logreg/", 'knn': "results/knn/", 'ada': "results/ada/",
                    'sgd': "results/sgd/", 'rndfor': "results/rndfor/"}

    # TODO: this is for RGB-D
    # result_paths = {'lda': "resultsRGBD/lda/", 'bayes': "resultsRGBD/bayes/", 'dectree': "resultsRGBD/dectree/",
    #                 'mlp': "resultsRGBD/mlp/", 'svc': "resultsRGBD/svc/", 'mnb': "resultsRGBD/mnb/",
    #                 'logreg': "resultsRGBD/logreg/", 'knn': "resultsRGBD/knn/", 'ada': "resultsRGBD/ada/",
    #                 'sgd': "resultsRGBD/sgd/", 'rndfor': "resultsRGBD/rndfor/"}

    # dataset_input_files = {"DIODE": "C:/Users/George/bsc/Licenta/Processor/photo_data.csv"}  # le completezi tu in functie de seturile de date folosite
    dataset_input_files = {"DIODE": "C:/Users/George/bsc/Licenta/Processor/photo_data_split4.csv"}  # le completezi tu in functie de seturile de date folosite
    # dataset_input_files = {"DIODE": "C:/Users/George/bsc/Licenta/Processor/photo_data_split16.csv"}  # le completezi tu in functie de seturile de date folosite

    # dataset_input_files = {"DIODE": "C:/Users/George/Downloads/DPT-main/DPT-main/DPT_hybrid_DE_after.pkl"}  # lol 100% cpu, ram si disk
    # dataset_input_files = {"DIODE": "C:/Users/George/Downloads/DPT-main/DPT-main/DPT_hybrid_SS_before_384.pkl"}
    # dataset_input_files = {"DIODE": "C:/Users/George/Downloads/DPT-main/DPT-main/DPT_hybrid_SS_before_384_RGBA-D.pkl"}
    # dataset_input_files = {"DIODE": "C:/Users/George/Downloads/DPT-main/DPT-main/DPT_hybrid_DE_before.pkl"}
    # dataset_input_files = {"DIODE": "C:/Users/George/Downloads/DPT-main/DPT-main/DPT_hybrid_SS_after.pkl"}
    # dataset_input_files = {"DIODE": "C:/Users/George/Downloads/DPT-main/DPT-main/DPT_hybrid_SS_before.pkl"}

    # dataset_result_files = {"DIODE": "C:/Users/George/bsc/Licenta/Processor/output.xlsx"}  # le completezi tu in functie de seturile de date folosite
    dataset_result_files = {"DIODE": "output.xlsx"}  # le completezi tu in functie de seturile de date folosite

    # aici ar trebui sa ti se creeze directoarele in care vor fi salvate datele de la crossvalidation
    for result_path in result_paths:
        if not os.path.exists(result_paths[result_path]):
            os.makedirs(result_paths[result_path])

    test_size = 0.1  # aici pui cat vrei intre 0 si 1 (fractia din date care da dimensiunea setului de testare)

    for dataset in dataset_input_files:
        # pentru fiecare set de date citesti datele+clasele aferente
        # data, classes = read_data(dataset_input_files[dataset]) #aici modifici cu o citire valida de date

        data, classes = get_data_from_csv(dataset_input_files[dataset])
        # data_d, classes_d = get_data_from_csv_d("C:/Users/George/bsc/Licenta/Processor/photo_data.csv")
        # data_d, classes_d = get_data_from_csv_d("C:/Users/George/bsc/Licenta/Processor/photo_data_split4.csv")
        # data_d, classes_d = get_data_from_csv_d("C:/Users/George/bsc/Licenta/Processor/photo_data_split16.csv")
        # data_d, classes_d = get_data_from_pkl("C:/Users/George/Downloads/DPT-main/DPT-main/DPT_hybrid_DE_before.pkl")  # TODO: this is special
        # data, classes = get_data_from_pkl(dataset_input_files[dataset])  # TODO: this is special

        print(data.shape)

        # data = np.append(data, data_d, axis=1)
        # features = []
        # for ss_arr, de_arr in zip(data, data_d):
        #     features_line = []
        #     for ss, de in zip(ss_arr, de_arr):
        #         features_line.append(ss)
        #         features_line.append(de)
        #     features.append(features_line)
        # data = np.array(features)

        # features = []
        # for ss_arr, de_val in zip(data, data_d):
        #     features_line = []
        #     for ss in ss_arr:
        #         features_line.append(ss)
        #         features_line.append(de_val.astype(float).astype(int)[0])
        #     features.append(features_line)
        # data = np.array(features)


        print(data.shape)

        # print(data[0])
        # print(data_d[0])
        # print("\n")
        # print(data[-1])
        # print(data_d[-1])

        print(classes.shape)
        # data, classes = read_data(dataset_input_files[dataset]) #aici modifici cu o citire valida de date
        for classifier in classifiers:
            # faci crossvalidation cu fiecare clasificator disponibil pe setul de date curent
            # cross_validate_classifier(classifiers[classifier], data, classes, test_size, dataset_result_files[dataset])
            cross_validate_classifier(classifiers[classifier], data, classes, test_size, result_paths[classifier] + dataset_result_files[dataset])


#     raise ValueError("Negative values in data passed to %s" % whom)
# ValueError: Negative values in data passed to MultinomialNB (input X)
# TODO: this happens because non-valid pixels have depth == -1