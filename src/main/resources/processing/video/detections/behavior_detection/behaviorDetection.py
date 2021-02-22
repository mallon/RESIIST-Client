#!/usr/bin/env python
# coding: utf-8

import numpy
import os
import six.moves.urllib as urllib
import sys
import tarfile
import tensorflow
import zipfile
import sys
import socket
import time
import math
from collections import deque
import argparse
import threading
import imutils
from multiprocessing import Process

# OpenCV library to capture images
import cv2

from collections import defaultdict
from io import StringIO
from PIL import Image

CURRENT_PYTHON_PATH=os.getcwd() + "/src/main/resources"
EXTRACTION_TO_INTEGRATION_PATH = CURRENT_PYTHON_PATH + '/processing/video/detections/'

sys.path.insert(0, EXTRACTION_TO_INTEGRATION_PATH + 'detection_utils')
from detection_utils.utils import ops as detectionUtils
 
from detection_utils.utils import label_map_util
 
from detection_utils.utils import visualization_utils as visualizationUtils

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

#For Java caller
CURRENT_PYTHON_PATH=os.getcwd() + "/src/main/resources"
EXTRACTION_TO_INTEGRATION_PATH = CURRENT_PYTHON_PATH + '/processing/video/detections/'
SPECIFIC_PATH=EXTRACTION_TO_INTEGRATION_PATH + 'behavior_detection/human-activity-recognition/'

#required model and the frozen inference graph
# Which model to download.
MODEL_PATH = SPECIFIC_PATH

# Path to frozen detection graph. This is the actual model that is used for the object detection.
PATH_TO_FROZEN_GRAPH = MODEL_PATH + 'resnet-34_kinetics.onnx'

# List of the strings that is used to add correct label for each box.
PATH_TO_LABELS = os.path.join('data', SPECIFIC_PATH + 'action_recognition_kinetics.txt')
CLASSES = open(PATH_TO_LABELS).read().strip().split("\n")

IMAGE_SIZE = 112
IMAGE_NUMBER = 16
INTEGRATION_NETWORK_ADRESS = '127.0.0.1'
INTEGRATION_NETWORK_PORT = 8081
higher_proba_label = ""


def startDetection(framesToAnalyse, objectNameToDetectList, allElementIds): 
    print("Starting activity detection...")
    detection_graph = cv2.dnn.readNet(PATH_TO_FROZEN_GRAPH)
    
    internalFrames = deque(maxlen=IMAGE_NUMBER)
    for frameToAnalyse in framesToAnalyse:
        internalFrames.append(frameToAnalyse)
        
    # now that our frames array is filled we can construct our blob   
    blob = cv2.dnn.blobFromImages(internalFrames, 1.0, (IMAGE_SIZE, IMAGE_SIZE), (114.7748, 107.7354, 99.4750), swapRB=True, crop=True)
    blob = numpy.transpose(blob, (1, 0, 2, 3))  
    blob = numpy.expand_dims(blob, axis=0)
   
    # pass the blob through the network to obtain our human activity
    # recognition predictions
    detection_graph.setInput(blob)
    outputs = detection_graph.forward()
    
    global higher_proba_label 
    higherDistance = numpy.max(outputs)
    higher_proba_label = CLASSES[numpy.argmax(outputs)]
    
    print("objectNameToDetectList:")
    print(*objectNameToDetectList)
    print("[INFO] Activity recognized : " + higher_proba_label)
    print("[INFO] higherDistance: " + str(higherDistance))
    
    if (higher_proba_label in objectNameToDetectList) and higherDistance >= 5:  
        higher_proba_label_crit_value = [allElementIds[objectNameToDetectList.index(higher_proba_label)], higher_proba_label, "1"]                
        higher_proba_label_crit_value_string = ','.join(higher_proba_label_crit_value)
        
        print("[INFO] Targeted activity recognized - HERE: " + higher_proba_label_crit_value_string)
            
        our_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        our_socket.connect( (INTEGRATION_NETWORK_ADRESS, INTEGRATION_NETWORK_PORT) ) 
        msg = higher_proba_label_crit_value_string + "\r\n"
        our_socket.send(msg.encode('utf8'))
        our_socket.close()
    else :
        for objectToDetect in objectNameToDetectList :
            higher_proba_label_crit_value = [allElementIds[objectNameToDetectList.index(objectToDetect)], objectToDetect, "0"]                
            higher_proba_label_crit_value_string = ','.join(higher_proba_label_crit_value)   
            
            print("[INFO] Targeted activity not recognized: " + higher_proba_label_crit_value_string)
                         
            our_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            our_socket.connect( (INTEGRATION_NETWORK_ADRESS, INTEGRATION_NETWORK_PORT) )
            msg = higher_proba_label_crit_value_string + "\r\n"
            our_socket.send(msg.encode('utf8'))
            our_socket.close()