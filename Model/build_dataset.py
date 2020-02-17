"""Split the HAM10000 dataset into train/dev/test and resize images to 224x224.
Directory `data_dir` should contain all images and a metadata file
Creates `train`, `dev`, `test` directories in `output_dir`, and in each of them creates directories `nevus` and `others` containing adequate images
"""

import os
import numpy as np
import argparse
from PIL import Image


parser = argparse.ArgumentParser()
parser.add_argument('--data_dir', default='data/Original', help="Directory with all the images from dataset and metadata file")
parser.add_argument('--output_dir', default='data/Splitted', help="Where to write the new data")
parser.add_argument('--size', type=int, default=224, help="Target image size")

#dictionary assigning each lession an abstract type
#here we classify nevus vs all other lession types
name_to_class = {'akiec':'others',
               'bcc':'others',
               'bkl':'others',
               'df':'others',
               'mel':'others',
               'nv':'nevus',
               'vasc':'others'}

def resize_and_save(filepath, target_filename, output_dir, size):
    """Resize the image contained in `filepath` and save it as `target_filename` to the `output_dir`"""
    image = Image.open(filepath)
    image = image.resize((size, size), Image.BILINEAR)
    image.save(os.path.join(output_dir, target_filename+'.jpg'))

def prepareDirectories(output_dir):
    for name in set(name_to_class.values()):
        for dir_type in ['train', 'validation', 'test']:
            directory = os.path.join(output_dir, dir_type, name)
            if not os.path.exists(directory):
                os.makedirs(directory)

def generateData(dataDir, outputDir, size, validation_split = 0.1, test_split = 0.1):
    assert (validation_split+test_split<1.0), 'Validation and test split should be below 100% of the dataset'

    #dictionary with classname as key, and an array with paths as value
    imagePathsArray = {classname:[] for classname in set(name_to_class.values())}
    metadataFilepath = os.path.join(dataDir,"HAM10000_metadata.tab")
    with open(metadataFilepath) as f:
        next(f)
        for line in f:
            columnsArray = line.split('	')
            name = columnsArray[1].replace('"','')
            path = os.path.join(dataDir,name+'.jpg')
            class_name = columnsArray[2].replace('"','')
            class_name = name_to_class[class_name]
            imagePathsArray[class_name].append(path)
    biggest_class_count = max([len(imagePathsArray[x]) for x in imagePathsArray])
    biggest_validation_count = int(biggest_class_count*validation_split)
    biggest_test_count = int(biggest_class_count*test_split)
    biggest_training_count = biggest_class_count - (biggest_validation_count + biggest_test_count)

    for className in imagePathsArray:
        classPaths = imagePathsArray[className]
        countClass = len(classPaths)
        validation_count = int(countClass*validation_split)
        test_count = int(countClass*test_split)
        training_count = countClass-(validation_count+test_count)
        
        
        validation_paths = np.random.choice(classPaths, validation_count, replace=False)
        remaining_paths  = np.setdiff1d(classPaths, validation_paths)
        test_paths = np.random.choice(remaining_paths, test_count, replace=False)
        remaining_paths  = np.setdiff1d(remaining_paths, test_paths)
        training_paths = remaining_paths
        
  
        balancing_validation_paths = np.random.choice(validation_paths,biggest_validation_count-validation_count)
        balancing_test_paths = np.random.choice(test_paths,biggest_test_count-test_count)
        balancing_training_paths = np.random.choice(training_paths,biggest_training_count-training_count)
        
        
        validation_paths = np.concatenate([validation_paths,balancing_validation_paths])
        test_paths = np.concatenate([test_paths,balancing_test_paths])
        training_paths   = np.concatenate([training_paths,balancing_training_paths])

        i = 0
        for path in validation_paths:
            resize_and_save(filepath=path, target_filename=str(i), output_dir=os.path.join(outputDir,"validation",className), size=size)
            i+=1
        i = 0
        for path in test_paths:
            resize_and_save(filepath=path, target_filename=str(i), output_dir=os.path.join(outputDir,"test",className), size=size)
            i+=1
        i = 0
        for path in training_paths:
            resize_and_save(filepath=path, target_filename=str(i), output_dir=os.path.join(outputDir,"train",className), size=size)
            i+=1

if __name__ == "__main__":
    args = parser.parse_args()
    assert os.path.isdir(args.data_dir), "Couldn't find the dataset at {}".format(args.data_dir)
    prepareDirectories(args.output_dir)
    print("Generating data...")
    generateData(dataDir=args.data_dir, outputDir=args.output_dir, size=args.size, validation_split=0.1, test_split=0.1)
    print("Done building dataset")
    
