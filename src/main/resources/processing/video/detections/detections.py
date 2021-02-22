#!/usr/bin/env python
# coding: utf-8

import cv2
import threading
from subprocess import call
from tensorflow.keras.models import load_model
from tensorflow.python.keras.backend import set_session
import tensorflow.compat.v1 as tensorflow
tensorflow.disable_v2_behavior()
import argparse
import json
import copy
import time
import ast
from multiprocessing import Process
import gc

import os
CURRENT_PYTHON_PATH=os.getcwd() + "/src/main/resources"
EXTRACTION_TO_INTEGRATION_PATH = CURRENT_PYTHON_PATH + '/processing/video/detections/'
print("Chemin courant :" + EXTRACTION_TO_INTEGRATION_PATH)

import sys,importlib
sys.path.append(EXTRACTION_TO_INTEGRATION_PATH + 'object_detection')
sys.path.append(EXTRACTION_TO_INTEGRATION_PATH + 'behavior_detection')
sys.path.append(EXTRACTION_TO_INTEGRATION_PATH + 'event_detection')
sys.path.append(EXTRACTION_TO_INTEGRATION_PATH + 'event_detection/pyimagesearch')
sys.path.append(EXTRACTION_TO_INTEGRATION_PATH + 'emotion_detection')

import logging;
logging.disable(logging.INFO);
logging.disable(logging.DEBUG);
logging.disable(logging.WARNING);

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'  # or any {'0', '1', '2'}
import tensorflow as tf
  #Level | Level for Humans | Level Description                  
 #-------|------------------|------------------------------------ 
  #0     | DEBUG            | [Default] Print all messages       
  #1     | INFO             | Filter out INFO messages           
  #2     | WARNING          | Filter out INFO & WARNING messages 
  #3     | ERROR            | Filter out all messages  

ap = argparse.ArgumentParser()
ap.add_argument("-i", "--input", type=str, default="",
                help="optional path to video file")
ap.add_argument("-fs", "--frequencies", type=str, required=True,
                help="frequencies of data capture ([f1, f2, ...]")
ap.add_argument("-aeo", "--algoElementsObjects", required=True, type=json.loads,
                help="model element identifiers and their objects to detect for each algo ({ algoOne: { elementIDs: [], objects: [] } })") 
args = vars(ap.parse_args())

processList=[]
    
def startBehaviorDetection(framesForActivityDetection, allObjToDetects, allElementIds):
    copiedFrames = copy.copy(framesForActivityDetection)        
    module = importlib.import_module("behaviorDetection")
    method_to_call = getattr(module, 'startDetection')
    method_to_call(copiedFrames, allObjToDetects, allElementIds)

def startDetection(frameToAnalyse, algoNameToExecute, allObjToDetects, allElementIds):
    print('Algo to execute :' + algoNameToExecute)
    print('Objects to detect :' + (', '.join(allObjToDetects)))
    print('Element IDs :' + (', '.join(allElementIds)))
    copiedFrame =  copy.copy(frameToAnalyse)    
    module = importlib.import_module(algoNameToExecute)
    method_to_call = getattr(module, 'startDetection')
    method_to_call(copiedFrame, allObjToDetects, allElementIds)
        
def sendFramesToBehaviorAlgorithm(framesForActivityDetection):
    limitProc = 4
    terminateProcesses(limitProc)
    elementObjectMap = args["algoElementsObjects"].get("behaviorDetection")
    allElementIds = elementObjectMap.get("ELEMENT_IDENTIFIERS")
    allObjToDetects = elementObjectMap.get("OBJECTS_TO_DETECT")
    process = Process(target=startBehaviorDetection, args=(framesForActivityDetection, allObjToDetects, allElementIds))
    process.start()
    processList.append(process)
    gc.collect()
            
def sendOneFrameToAllDetectionAlgorithms(frameToAnalyse):
    limitProc = 4
    terminateProcesses(limitProc)
    for algoNameToExecute in args["algoElementsObjects"].keys():
        #if (algoNameToExecute == "eventDetection"):
        if (algoNameToExecute != "behaviorDetection"):
            elementObjectMap = args["algoElementsObjects"].get(algoNameToExecute)
            allElementIds = elementObjectMap.get("ELEMENT_IDENTIFIERS")
            allObjToDetects = elementObjectMap.get("OBJECTS_TO_DETECT")
            process = Process(target=startDetection, args=(frameToAnalyse, algoNameToExecute, allObjToDetects, allElementIds))
            process.start()
            processList.append(process)
            gc.collect()

def terminateProcesses(limitProc):
    global processList
    if (len(processList) >= limitProc):
        for process in processList:
            process.terminate()
        processList = []
        gc.collect()

def isSendingTime(currentTime, lastRecordedTime):
    frequencies = ast.literal_eval(args["frequencies"])
    for frequency in frequencies:
        if (currenTime - lastRecordedTime >= int(frequency)):
            return True

cap = cv2.VideoCapture(args["input"] if args["input"] else 0)
delay = int(1000/cap.get(cv2.CAP_PROP_FPS))

print("FPS : " + str(cap.get(cv2.CAP_PROP_FPS)))

print("Delay : " + str(delay))

framesForActivityDetection=[]
lastRecordedTime = time.time()

while True:
  currenTime = time.time()
  limitProc = 4
  terminateProcesses(limitProc)
  
  ret, frameToAnalyse = cap.read()
  
  #Specific case for activity detc. needing a set of frames (16)
  framesForActivityDetection.append(frameToAnalyse)
  
  if (isSendingTime(time.time(), lastRecordedTime) and len(processList) < limitProc):
    print("frequency: " + args["frequencies"])  
    if len(framesForActivityDetection) >= 16:
        process = Process(target=sendFramesToBehaviorAlgorithm, args=(framesForActivityDetection,))
        process.start()
        processList.append(process)
        framesForActivityDetection=[]
    
    process = Process(target=sendOneFrameToAllDetectionAlgorithms, args=(frameToAnalyse,))
    process.start()
    processList.append(process)
    
    lastRecordedTime = currenTime
  
  cv2.imshow('Detection...', cv2.resize(frameToAnalyse, (400,300)))
  
  if cv2.waitKey(delay) & 0xFF == ord('q'):
    cv2.destroyAllWindows()
    cap.release()    
    for process in processList:
            process.terminate()
    break
