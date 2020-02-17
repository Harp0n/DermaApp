import numpy as np
from keras.preprocessing.image import img_to_array
from keras.applications.mobilenet_v2 import MobileNetV2, preprocess_input
from keras.preprocessing.image import load_img
from keras.models import Model
from keras.layers import Dense, Flatten, Input, MaxPooling2D, GlobalAveragePooling2D, SeparableConv2D, Conv2D, BatchNormalization, Dropout, Activation
from keras.optimizers import Adam
from keras.preprocessing.image import ImageDataGenerator
from keras.utils import to_categorical
from sklearn.utils import class_weight
from keras.models import load_model
import keras
import keras.backend as K
import tensorflow as tf
from keras.callbacks import TensorBoard, EarlyStopping, ModelCheckpoint, ReduceLROnPlateau
from sklearn.metrics import balanced_accuracy_score
import tempfile
import os
from sklearn.metrics import confusion_matrix, classification_report
from time import time
import matplotlib.pyplot as plt
import argparse

#hyperparameters
size = 224
batchsize = 32 
dropout = 0.3
lr = 0.0002


parser = argparse.ArgumentParser()
parser.add_argument('--data_dir', default='data/Splitted',
                    help="Directory containing the (splitted) dataset")
parser.add_argument('--restore_from', default=None,
                    help="Optional, directory or file containing saved model to load before training")


"""Build the model using MobileNetV2 as base model, followed by a global average pooling, a fully connected layer and one sigmoid unit as final output"""
def build_model():
    input_layer = Input(shape=(size,size,3))
    features_model = MobileNetV2(include_top = False, input_tensor = input_layer)
    x = features_model.output

    x = GlobalAveragePooling2D()(x)
    x = BatchNormalization()(x)
    x = Dropout(dropout)(x)

    x = Dense(1024)(x)
    x = BatchNormalization()(x)
    x = Activation('relu')(x)
    x = Dropout(dropout)(x)
    output = Dense(1, activation='sigmoid')(x)

    model = Model(inputs=input_layer, outputs=output)
    model.compile( optimizer=Adam(lr),
                loss='binary_crossentropy',
                metrics=['accuracy'])
    return model


"""Build generators for creating augmented data"""
def build_generators(data_dir):
    train_datagen = ImageDataGenerator(
        zoom_range=0.2,
        rotation_range=180,
        width_shift_range=0.1,
        height_shift_range=0.1,
        brightness_range=[0.9, 1.1],
        preprocessing_function=preprocess_input,
        horizontal_flip=True,
        vertical_flip=True)

    validation_datagen = ImageDataGenerator(preprocessing_function=preprocess_input)

    train_generator = train_datagen.flow_from_directory(
            os.path.join(data_dir,'train'),
            target_size=(size, size),
            batch_size=batchsize,
            class_mode='binary')

    validation_generator = validation_datagen.flow_from_directory(
            os.path.join(data_dir,'validation'),
            target_size=(size, size),
            batch_size=batchsize,
            class_mode='binary')

    return [train_generator, validation_generator]


def train(model, data_dir):
    train_generator, validation_generator = build_generators(data_dir)

    tensorboard = TensorBoard(log_dir="logs/{}".format(time()))
    bestModelCheckpoint = ModelCheckpoint( filepath="model.h5", monitor='val_loss', save_weights_only=False, save_best_only=True, mode='min', verbose=1)
    earlystop = EarlyStopping(monitor = 'val_loss', min_delta = 0, patience = 20, verbose = 1, restore_best_weights = True)
    lrReduce = ReduceLROnPlateau(monitor='val_loss', factor=0.1, patience=6, verbose=1)

    return model.fit_generator(
        train_generator,
        steps_per_epoch=50,
        validation_data=validation_generator,
        validation_steps=len(validation_generator.classes)//batchsize + 1,
        epochs=200,
        verbose=2,
        callbacks=[tensorboard,bestModelCheckpoint, earlystop, lrReduce])

if __name__ == "__main__":
    args = parser.parse_args()
    assert os.path.isdir(args.data_dir), "Couldn't find the dataset at {}".format(args.data_dir)
    if(args.restore_from!=None):
        model = load_model(args.restore_from)
    else:
        model = build_model()
    train(model, args.data_dir)