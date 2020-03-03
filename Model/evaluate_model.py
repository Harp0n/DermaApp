import os
import numpy as np
import argparse
from keras.models import load_model
from keras.preprocessing.image import load_img
from keras.preprocessing.image import img_to_array
from keras.preprocessing.image import ImageDataGenerator
from keras.applications.mobilenet_v2 import preprocess_input
from sklearn.metrics import roc_auc_score, accuracy_score, confusion_matrix

SIZE = 224
batchsize = 32

parser = argparse.ArgumentParser()
parser.add_argument('--model', default='model.h5',
                    help="Filename of the model to evaluate")
parser.add_argument('--test_dir', default='data/Splitted/test',
                    help="Directory containing the test dataset")


def build_test_generator(test_dir):
    test_datagen = ImageDataGenerator(preprocessing_function=preprocess_input)

    test_generator = test_datagen.flow_from_directory(
            test_dir,
            target_size=(SIZE, SIZE),
            batch_size=batchsize,
            class_mode='binary')
    return test_generator

# test_generator = None
# model = None

'''function returning specifity given desired sensitivity 
    performs binary search for the sufficient threshold, specifity is returned for the threshold when
    sensitivity_threshold <= sensitivity <= sensitivity_threshold + eps 
'''
def specifity_at_sensitivity(Y_true, Y_pred, sensitivity_threshold=0.89, eps=0.001):
    low_threshold = 0.0
    high_threshold = 1.0
    for _ in range(100):
        threshold = (high_threshold+low_threshold)/2
        y_pred =  (Y_pred >= threshold)
        cm = confusion_matrix(Y_true, y_pred)
        tn, fp, fn, tp = cm.ravel()
        sensitivity = tp/(tp + fn)
        specifity = tn/(tn + fp)
        if(0 <= sensitivity-sensitivity_threshold <= eps):
            break
        if(sensitivity>=sensitivity_threshold):
            low_threshold = threshold
        else:
            high_threshold = threshold
    test_generator.shuffle = True
    return specifity

def get_model_prediction():
    test_generator.reset()
    test_generator.shuffle = False
    Y_pred = model.predict_generator(test_generator, len(test_generator.classes) // batchsize+1)
    Y_true = test_generator.classes
    test_generator.shuffle = True
    return [Y_true, Y_pred]

def evaluate():
    Y_true, Y_pred = get_model_prediction()
    accuracy = accuracy_score(Y_true, Y_pred>=0.5)
    roc_auc  = roc_auc_score(Y_true, Y_pred)
    specifity_at_89 = specifity_at_sensitivity(Y_true, Y_pred, sensitivity_threshold=0.89)
    print("""Evaluating model on the test set:
    Accuracy: %.3f,
    ROC AUC score: %.3f,
    Specifity at 89%% sensitivity: %.3f
    """ % (accuracy, roc_auc, specifity_at_89))

if __name__ == "__main__":
    args = parser.parse_args()
    assert os.path.isdir(args.test_dir), "Couldn't find the test set at {}".format(args.test_dir)
    global model
    global test_generator
    model = load_model(args.model)
    test_generator = build_test_generator(args.test_dir)
    evaluate()
