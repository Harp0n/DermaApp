# Flask server for model serving

Simple flask server which takes pictures, preprocesses them, and returns an output of the model.
To start it, run:
```
python run_flask_server.py
```

## Example connection to the server
```python
import requests

url = "http://localhost:5000/analyze/upload/"
img_path = "notnevus.jpg"
image = open(img_path, "rb").read()

payload = {"picture": image}
# submit the request
r = requests.post(KERAS_REST_API_URL, files=payload).json() 

# ensure the request was successful
if r["success"]:
    # loop over the predictions and display them
    print(r["score"])
# otherwise, the request failed
else:
    print("Request failed")
```
