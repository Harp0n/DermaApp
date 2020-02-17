test_datagen = ImageDataGenerator(preprocessing_function=preprocess_input)

test_generator = test_datagen.flow_from_directory(
         main_dir+'validation',
        target_size=(size, size),
        batch_size=batchsize,
        class_mode='binary')


def validate(sensitivity_threshold=0.89  , eps=0.005):
    validation_generator.reset()
    validation_generator.shuffle = False
    Y_pred = model.predict_generator(validation_generator, len(validation_generator.classes) // batchsize+1)
    low_threshold = 0.0
    high_threshold = 1.0
    while(True):
        threshold = (high_threshold+low_threshold)/2
        y_pred =  np.where(Y_pred >= threshold, 1, 0) 
        cm = confusion_matrix(validation_generator.classes, y_pred)
        tn, fp, fn, tp = cm.ravel()
        sensitivity = tp/(tp + fn)
        specifity = tn/(tn + fp)
        if(0 <= sensitivity-sensitivity_threshold <= eps):
            break
        if(sensitivity>=sensitivity_threshold):
            low_threshold = threshold
        else:
            high_threshold = threshold
    target_names = ['nevus', 'others']
    print(cm)
    print("required threshold:", threshold)
    validation_generator.shuffle = True
    return specifity

from sklearn.metrics import roc_auc_score
def test_roc_auc():
    validation_generator.reset()
    validation_generator.shuffle = False
    Y_pred = model.predict_generator(validation_generator, len(validation_generator.classes) // batchsize+1)
    Y_true = validation_generator.classes
    roc = roc_auc_score(Y_true, Y_pred)
    validation_generator.shuffle = True
    return roc
print(test_roc_auc())

def loadAndPreprocess(path):
    image = load_img(path, target_size=(size, size))
    image = img_to_array(image)
    image = preprocess_input(image)
    return image

def deprocessImage(img):
    x = np.array(img)
    x = np.mean(x, axis=-1)
##    x -= np.min(x)
    x = np.clip(x, 0.0, np.max(x))
    x /= np.max(x)
    return x

def get_layer_output_grad(inputs):
    """ Gets gradient a layer output for given inputs and outputs"""
    grads = K.gradients(model.output, model.input)[0]
    grads /= (K.sqrt(K.mean(K.square(grads))) + 1e-5)
    iterate = K.function([model.input], [grads])
    grads_value = iterate([inputs])
    return grads_value[0]

def test_grad(index=8):
    path = 'dataNevus/train/others/'+str(index)+'.jpg'
    img = loadAndPreprocess(path)
    imgModel = img.reshape(1,img.shape[0],img.shape[1],img.shape[2])
    grads = get_layer_output_grad(imgModel)[0]
    deprocessedImage = deprocessImage(grads)
    fig, axs = plt.subplots(1, 2)
    axs0 = axs[0].imshow(img)
    axs1 = axs[1].imshow(deprocessedImage)
    axs1.set_cmap('jet')
    fig.colorbar(axs1)
    plt.axis('off')
    plt.show()