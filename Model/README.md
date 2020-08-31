# Skin Lesion classification with Tensorflow and Keras

_Author: Michał Janik_

A deep model built with Tensorflow and Keras classifying types of skin lesions.
Images are passed through [MobileNetV2][mobilenetv2] to obtain good feature representation. Features are then globally pooled and passed to a fully connected layer.
The model outputs a single label that predicts whether a given lesion is a benign nevus(**0**) or any other type(**1**).
Model is trained on the HAM10000 dataset containing 10015 dermatoscopic images of skin lesions.

### Results
On unseen data the model achieves:
| Metric | Value |
| --- | --- |
| Accuracy: | *0.877* |
| ROC AUC score: |  *0.949* |
| Specifity at 89% sensitivity: | *0.857* |

## HAM10000 download

The dataset may be downloaded from [here][ham].

After downloading the metadata file, and two images zipped parts extract zipped files and put everything in `data\Original`.

Your directory should look like this:
```
data/
    Original/
      HAM10000_metadata.tab
      ISIC_0024306.jpg
      ISIC_0024307.jpg
      ...
```

Run the script `build_dataset.py` which will resize the images to size `(224, 224)` and split them to training/validation/test directories. The new resized dataset will be located by default in `data/Splitted`:

```bash
python build_dataset.py --data_dir data/Original --output_dir data/Splitted
```

## Train Model
In order to **train** the model, run:

```
python train_model.py --data_dir data/Splitted
```

## Evaluate Model
To see how the model performs on the test data, run:
```
python evaluate_model.py
```

[ham]: https://dataverse.harvard.edu/dataset.xhtml?persistentId=doi:10.7910/DVN/DBW86T
[mobilenetv2]: https://arxiv.org/abs/1801.04381
