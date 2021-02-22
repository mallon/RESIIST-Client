# import the necessary packages
import os

CURRENT_PYTHON_PATH=os.getcwd() + "/src/main/resources"
EXTRACTION_TO_INTEGRATION_PATH = CURRENT_PYTHON_PATH + '/processing/video/detections/'

# initialize the path to the input directory containing our dataset
# of images
DATASET_PATH = "Cyclone_Wildfire_Flood_Earthquake_Database"

# initialize the class labels in the dataset
CLASSES = ["Cyclone", "Earthquake", "Flood", "Wildfire"]

# define the size of the training, validation (which comes from the
# train split), and testing splits, respectively
TRAIN_SPLIT = 0.75
VAL_SPLIT = 0.1
TEST_SPLIT = 0.25

# define the minimum learning rate, maximum learning rate, batch size,
# step size, CLR method, and number of epochs
MIN_LR = 1e-6
MAX_LR = 1e-4
BATCH_SIZE = 32
STEP_SIZE = 8
CLR_METHOD = "triangular"
NUM_EPOCHS = 48

# set the path to the serialized model after training
MODEL_PATH = os.path.sep.join([EXTRACTION_TO_INTEGRATION_PATH+"event_detection/output", "natural_disaster.model"])
MODEL_PATH_PB = os.path.sep.join([EXTRACTION_TO_INTEGRATION_PATH+"event_detection/output/natural_disaster.model", "saved_model.pb"])

# define the path to the output learning rate finder plot, training
# history plot and cyclical learning rate plot
LRFIND_PLOT_PATH = os.path.sep.join([EXTRACTION_TO_INTEGRATION_PATH+"event_detection/output", "lrfind_plot.png"])
TRAINING_PLOT_PATH = os.path.sep.join([EXTRACTION_TO_INTEGRATION_PATH+"event_detection/output", "training_plot.png"])
CLR_PLOT_PATH = os.path.sep.join([EXTRACTION_TO_INTEGRATION_PATH+"event_detection/output", "clr_plot.png"])