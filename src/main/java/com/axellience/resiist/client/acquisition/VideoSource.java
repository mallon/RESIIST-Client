package com.axellience.resiist.client.acquisition;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.axellience.resiist.client.integration.IntegrationPlatform;
import com.axellience.resiist.client.utils.ResiistConstants;

public class VideoSource extends Thread
{
    private static final Logger LOGGER = Logger.getLogger(VideoSource.class.getName());

    @Override
    public void run()
    {
        List<Map<String, String>> allElementIdAndFrequencyAndSimulationFilePath =
                IntegrationPlatform.getInstance()
                                   .getAllElementAndObjectToDetectAndFrequenciesAndSimulationFilePaths();

        List<List<Map<String, String>>> elementsRegroupedBySameSimulationFilePath =
                getElementsRegroupedBySameFilePath(allElementIdAndFrequencyAndSimulationFilePath);

        for (List<Map<String, String>> elementsWithSameFilePath : elementsRegroupedBySameSimulationFilePath) {
            Thread videoSimulation = new Thread()
            {
                @Override
                public void run()
                {
                    launchVideoSimulation(elementsWithSameFilePath);
                }
            };
            videoSimulation.start();
        }

    }

    private List<List<Map<String, String>>> getElementsRegroupedBySameFilePath(List<Map<String, String>> allElementIdAndFrequencyAndSimulationFilePath)
    {
        List<List<Map<String, String>>> elementsRegroupedBySameSimulationFilePath =
                new ArrayList<>();
        for (Map<String, String> elementIdAndFrequencyAndSimulationFilePath : allElementIdAndFrequencyAndSimulationFilePath) {
            String elementFilePath =
                    elementIdAndFrequencyAndSimulationFilePath.get(ResiistConstants.SIMULATION_FILE_PATH);
            if (elementFilePath.endsWith(ResiistConstants.SIMULATION_VIDEO_FILE_NAME)) {
                List<Map<String, String>> elementAndFilePath =
                        getListOfElementWithSameFilePath(elementsRegroupedBySameSimulationFilePath,
                                                         elementFilePath);
                if (elementAndFilePath != null) {
                    elementAndFilePath.add(elementIdAndFrequencyAndSimulationFilePath);
                } else {
                    elementAndFilePath = new ArrayList<>();
                    elementAndFilePath.add(elementIdAndFrequencyAndSimulationFilePath);
                    elementsRegroupedBySameSimulationFilePath.add(elementAndFilePath);
                }
            }
        }
        return elementsRegroupedBySameSimulationFilePath;
    }

    private List<Map<String, String>> getListOfElementWithSameFilePath(List<List<Map<String, String>>> elementsRegroupedBySameSimulationFilePath,
                                                                       String elementFilePath)
    {
        return elementsRegroupedBySameSimulationFilePath.stream()
                                                        .filter(list -> listHasSameFilePath(list,
                                                                                            elementFilePath))
                                                        .findFirst()
                                                        .orElse(null);
    }

    private boolean listHasSameFilePath(List<Map<String, String>> list, String elementFilePath)
    {
        return list.stream()
                   .filter(map -> map.get(ResiistConstants.SIMULATION_FILE_PATH)
                                     .equals(elementFilePath))
                   .count() > 0;
    }

    private void launchVideoSimulation(List<Map<String, String>> elementIdsAndFrequenciesWithSameSimulationFilePath)
    {
        String detectionFilePath = "";
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            URL detectionResource =
                    classLoader.getResource("processing/video/detections/detections.py");
            detectionFilePath = detectionResource.getPath();

            URL envirResource =
                    classLoader.getResource("processing/video/detections/environmentActivation");
            String envirFilePath = envirResource.getPath();

            List<String> frequencies =
                    getFrequencies(elementIdsAndFrequenciesWithSameSimulationFilePath);
            String algoAndElementIdsAndObjects =
                    getAllAlgoAndElementIdsAndObjects(elementIdsAndFrequenciesWithSameSimulationFilePath);
            String fileArgument =
                    elementIdsAndFrequenciesWithSameSimulationFilePath.get(0)
                                                                      .get(ResiistConstants.SIMULATION_FILE_PATH);

            ProcessBuilder detectionProcessBuilder = new ProcessBuilder();
            detectionProcessBuilder.command("bash", envirFilePath);
            detectionProcessBuilder.command("python3",
                                            detectionFilePath,
                                            "-fs",
                                            "[" + StringUtils.join(frequencies, ",") + "]",
                                            "-i",
                                            fileArgument,
                                            "-aeo",
                                            algoAndElementIdsAndObjects);
            Process detectionProcess = detectionProcessBuilder.start();

            // BufferedReader reader2 =
            // new BufferedReader(new
            // InputStreamReader(detectionProcess.getInputStream()));
            // String line2;
            // while ((line2 = reader2.readLine()) != null) {
            // LOGGER.log(Level.SEVERE, line2);
            // }
            //
            // BufferedReader reader =
            // new BufferedReader(new
            // InputStreamReader(detectionProcess.getErrorStream()));
            // String line;
            // while ((line = reader.readLine()) != null) {
            // LOGGER.log(Level.SEVERE, line);
            // }

        }
        catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,
                       "Following command path(s) to the process does not exist: "
                                     + detectionFilePath);
        }
    }

    private String getAllAlgoAndElementIdsAndObjects(List<Map<String, String>> elementIdsAndFrequenciesWithSameSimulationFilePath)
    {
        String allElmeentAndObjects = "";

        int index = 0;
        for (ResiistConstants.algoAndObjects algo : ResiistConstants.algoAndObjects.values()) {
            index++;
            List<String> allElementIds = new ArrayList<>();
            List<String> allObjectsToDetect = new ArrayList<>();
            for (Map<String, String> elementIdAndFrequency : elementIdsAndFrequenciesWithSameSimulationFilePath) {
                String objectToDetect =
                        elementIdAndFrequency.get(ResiistConstants.OBJECTS_TO_DETECT);
                String elementId = elementIdAndFrequency.get(ResiistConstants.ELEMENT_IDENTIFIER);
                if (algo.getObjects().contains(objectToDetect)) {
                    allElementIds.add("\"" + elementId + "\"");
                    allObjectsToDetect.add("\"" + objectToDetect + "\"");
                }
            }
            String elementIds = "\"ELEMENT_IDENTIFIERS\":" + StringUtils.join("", allElementIds);
            String objects = "\"OBJECTS_TO_DETECT\":" + StringUtils.join("", allObjectsToDetect);
            allElmeentAndObjects += "\"" + algo + "\":{" + elementIds + "," + objects + "}";
            if (index < ResiistConstants.algoAndObjects.values().length) {
                allElmeentAndObjects += ",";
            }
        }

        return "{" + allElmeentAndObjects + "}";
    }

    private List<String> getFrequencies(List<Map<String, String>> elementIdsAndFrequenciesWithSameSimulationFilePath)
    {
        List<String> frequencies = new ArrayList<>();
        for (Map<String, String> elementIdAndFrequency : elementIdsAndFrequenciesWithSameSimulationFilePath) {
            String objectsToDetect = elementIdAndFrequency.get(ResiistConstants.OBJECTS_TO_DETECT);
            if (objectsToDetect != null) {
                frequencies.add(elementIdAndFrequency.get(ResiistConstants.FREQUENCY));
            }
        }
        return frequencies;
    }
}