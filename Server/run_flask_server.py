import flask
from gevent.pywsgi import WSGIServer
import io
import numpy as np
from keras.preprocessing.image import img_to_array
from PIL import Image
from keras.applications.mobilenet_v2 import preprocess_input
from keras.models import load_model
import tensorflow as tf

app = flask.Flask(__name__)
model = None


#create wrapper for keras model
class ModelWrapper:
    def __init__(self):
        config = tf.ConfigProto()
        config.gpu_options.allow_growth = True
        self.session = tf.Session(config=config)
        self.graph = tf.get_default_graph()
        self.model = None

    def load(self, filename='model.h5'):
        with self.graph.as_default():
            with self.session.as_default():
                self.model = load_model(filename)

    def predict(self, x):
        with self.graph.as_default():
            with self.session.as_default():
                return self.model.predict(x)

def prepare_image(image, target):
    # if the image mode is not RGB, convert it
    if image.mode != "RGB":
        image = image.convert("RGB")

    # resize the input image and preprocess it
    image = image.resize(target)
    image = img_to_array(image)
    image = np.expand_dims(image, axis=0)
    image = preprocess_input(image)

    # return the processed image
    return image

@app.route("/analyze/upload/", methods=["POST"])
def predict():
    # initialize the returned data dictionary
    data = {"success": False}

    # ensure an image was properly uploaded to our endpoint
    if flask.request.method == "POST":
        if flask.request.files.get("picture"):
            # read the image in PIL format
            image = flask.request.files["picture"].read()
            image = Image.open(io.BytesIO(image))

            # preprocess the image and prepare it for classification
            image = prepare_image(image, target=(224, 224))

            # predict score for the image
            try:
                preds = model.predict(image)
                data["score"] = str(preds[0][0])
                # indicate that the request was a success
                data["success"] = True
            except:
                _
    # return the data dictionary as a JSON response
    return flask.jsonify(data)

if __name__ == "__main__":
    print("Loading model and starting server...")
    model = ModelWrapper()
    model.load('model.h5')
    print("\n\n\Server is ready")
    http_server = WSGIServer(('', 5000), app)
    http_server.serve_forever()
