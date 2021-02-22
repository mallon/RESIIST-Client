#!/usr/bin/env python
# coding: utf-8
from tensorflow.keras.models import load_model
import tensorflow.compat.v1 as tensorflow
tensorflow.disable_v2_behavior()
from pyimagesearch import config
from collections import deque
import numpy as np
from multiprocessing import Process
import time
import threading
import cv2
import socket
import imutils
import sys
from tensorflow.python.framework.test_ops import none

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

INTEGRATION_NETWORK_ADRESS = '127.0.0.1'
INTEGRATION_NETWORK_PORT = 8081

def startDetection(frameToAnalyse, objectNameToDetectList, allElementIds):
    print("Starting event detection...")
    # load the trained model from disk
    tensorflow.keras.backend.clear_session()
    model = load_model(config.MODEL_PATH)

    # initialize the predictions queue
    Q = deque(maxlen=128)
        
    (W, H) = (None, None)
    
    #for frameToAnalyse in copiedFrames:            
    # if the frame dimensions are empty, grab them
    if W is None or H is None:
        (H, W) = frameToAnalyse.shape[:2]

    # clone the output frame, then convert it from BGR to RGB
    # ordering and resize the frame to a fixed 224x224
    frameToAnalyse = cv2.cvtColor(frameToAnalyse, cv2.COLOR_BGR2RGB)
    frameToAnalyse = cv2.resize(frameToAnalyse, (224, 224))
    frameToAnalyse = frameToAnalyse.astype("float32")
    
    # make predictions on the frame and then update the predictions
    # queue
    expandedFrame = np.expand_dims(frameToAnalyse, axis=0)
    preds = model.predict(expandedFrame)[0]
    Q.append(preds)  
               
    # perform prediction averaging over the current history of
    # previous predictions
    results = np.array(Q).mean(axis=0)
    i = np.argmax(results)
    label = config.CLASSES[i]
    if label in (objectNameToDetectList) :    
        
        print("Proba. : " + str(np.max(results)))
        
        # draw the activity on the output frame
        global text
        text = "{}".format(label)
                        
        label_crit_value = [allElementIds[objectNameToDetectList.index(text)], text, "1"]
        label_crit_value_string = ','.join(label_crit_value)
        
        print("[INFO] Event recognized: " + label)
        
        our_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        our_socket.connect( (INTEGRATION_NETWORK_ADRESS, INTEGRATION_NETWORK_PORT) ) 
        msg = label_crit_value_string + "\r\n"
        our_socket.send(msg.encode('utf8'))
        our_socket.close()
    else :
        for objectToDetect in objectNameToDetectList :
            higher_proba_label_crit_value = [allElementIds[objectNameToDetectList.index(objectToDetect)], objectToDetect, "0"]                
            higher_proba_label_crit_value_string = ','.join(higher_proba_label_crit_value)   
                         
            our_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            our_socket.connect( (INTEGRATION_NETWORK_ADRESS, INTEGRATION_NETWORK_PORT) ) 
            msg = higher_proba_label_crit_value_string + "\r\n"
            our_socket.send(msg.encode('utf8'))
            our_socket.close()