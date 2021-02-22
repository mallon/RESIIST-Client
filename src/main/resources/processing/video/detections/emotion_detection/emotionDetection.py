import cv2
import os
import socket
import time
import sys
import math
import threading
import argparse
import imutils
import numpy as np
from tensorflow.keras.models import load_model
import tensorflow.compat.v1 as tensorflow
tensorflow.disable_v2_behavior()
from keras.preprocessing.image import img_to_array

import logging;
logging.disable(logging.INFO);
logging.disable(logging.DEBUG);
#logging.disable(logging.CRITICAL);
logging.disable(logging.WARNING);
#logging.disable(logging.ERROR);

import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'  # or any {'0', '1', '2'}
import tensorflow as tf
  #Level | Level for Humans | Level Description                  
 #-------|------------------|------------------------------------ 
  #0     | DEBUG            | [Default] Print all messages       
  #1     | INFO             | Filter out INFO messages           
  #2     | WARNING          | Filter out INFO & WARNING messages 
  #3     | ERROR            | Filter out all messages  

label = ""

EMOTIONS= ["Angry", "Disgust", "Fear", "Happy", "Sad", "Surprise", "Neutral"]


CURRENT_PYTHON_PATH=os.getcwd() + "/src/main/resources"
EXTRACTION_TO_INTEGRATION_PATH = CURRENT_PYTHON_PATH + '/processing/video/detections/'
detection_model_path = EXTRACTION_TO_INTEGRATION_PATH + "/emotion_detection/haarcascade_frontalface_default.xml"

def startDetection(frame, objectNameToDetectList, allElementIds):
    tensorflow.keras.backend.clear_session()
    face_detection = cv2.CascadeClassifier(detection_model_path)
    model = load_model(EXTRACTION_TO_INTEGRATION_PATH + "emotion_detection/fer2013_big_XCEPTION.54-0.66.hdf5")
    
    frame = imutils.resize(frame,width=800)
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = face_detection.detectMultiScale(gray,scaleFactor=1.1,minNeighbors=5,minSize=(30,30),flags=cv2.CASCADE_SCALE_IMAGE)
    
    if len(faces) > 0:
        faces = sorted(faces, reverse=True,
        key=lambda x: (x[2] - x[0]) * (x[3] - x[1]))[0]
        (X, Y, W, H) = faces

        facial = gray[Y:Y + H, X:X + W]
        facial = cv2.resize(facial, (64, 64))                  
        facial = facial.astype("float") / 255.0
        facial = img_to_array(facial)
        facial = np.expand_dims(facial, axis=0)        
        
        preds = model.predict(facial)[0]
        label = EMOTIONS[preds.argmax()]
        
        if label in objectNameToDetectList:
            label_crit_value = [allElementIds[objectNameToDetectList.index(label)], label, "1"]
            label_crit_value_string = ','.join(label_crit_value)
             
            print("[INFO] Emotion recognized: " + label_crit_value_string)
             
            ourSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            ourSocket.connect( ('127.0.0.1', 8081) ) 
            msg = label_crit_value_string + "\r\n"
            our_socket.send(msg.encode('utf8'))
            ourSocket.close()
        else :
            for objectToDetect in objectNameToDetectList :
                higher_proba_label_crit_value = [allElementIds[objectNameToDetectList.index(objectToDetect)], objectToDetect, "0"]                
                higher_proba_label_crit_value_string = ','.join(higher_proba_label_crit_value)   
                             
                our_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                our_socket.connect( (INTEGRATION_NETWORK_ADRESS, INTEGRATION_NETWORK_PORT) )
                msg = higher_proba_label_crit_value_string + "\r\n"
                our_socket.send(msg.encode('utf8'))
                our_socket.close()