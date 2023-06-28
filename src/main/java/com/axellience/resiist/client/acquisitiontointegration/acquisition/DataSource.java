package com.axellience.resiist.client.acquisitiontointegration.acquisition;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.axellience.resiist.client.utils.CsvUtil;
import com.axellience.resiist.client.utils.ResiistConstants;
import com.fasterxml.jackson.databind.MappingIterator;

public class DataSource
{
    private ServerSocket                               serverSocket;
    private Socket                                     socket;
    private CsvUtil                                    csvUtil;
    private List<MappingIterator<Map<String, String>>> mappingIteratorList = new ArrayList<>();
    private MappingIterator<Map<String, String>>       mappingIterator;
    private MappingIterator<Map<String, String>>       secondMappingIterator;
    private MappingIterator<Map<String, String>>       thirdMappingIterator;

    private Integer previousRandomValue    = null;
    private long    currentTime            = 0;
    private long    lastTimeSimulatedValue = 0;
    private int     randomValuesNumber     = 200;
    private boolean lessZeroRandomValues;
    private boolean fromOneIntialValue;

    private static final Logger       LOGGER                      =
            Logger.getLogger(DataSource.class.getName());
    public String                     elementIdAndCriteriaAndValue;
    private String                    value;
    private String                    detectedEntity;
    private String                    elementId;
    private Map<String, String>       firstLineSimulationConf;
    private Map<String, Long>         elementAndLastTimeValue     = new HashMap<>();
    private List<Map<String, String>> firstLineSimulationConfList = new ArrayList<>();

    public DataSource()
    {
        csvUtil = new CsvUtil();
    }

    public void initConnexionWithVideoCaptor()
    {
        try {
            this.serverSocket = new ServerSocket(8081);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public boolean canSendValue()
    {
        if (serverSocket != null) {
            try {
                socket = serverSocket.accept();
                return true;
            }
            catch (Exception e) {
                LOGGER.log(Level.INFO, "-- Connexion lost --", e);
            }
        }

        return false;
    }

    public String requestCriteriaAndValue()
    {
        byte[] byteArray = new byte[10000];
        try {
            int bytesRead = socket.getInputStream().read(byteArray);
            if (bytesRead != -1) {
                elementIdAndCriteriaAndValue = new String(byteArray, 0, bytesRead);
                elementIdAndCriteriaAndValue =
                        elementIdAndCriteriaAndValue.replace("[", "").replace("]", "");
                value = elementIdAndCriteriaAndValue.split(",")[2];
                detectedEntity = elementIdAndCriteriaAndValue.split(",")[1];
                elementId = elementIdAndCriteriaAndValue.split(",")[0];
                LOGGER.log(Level.INFO,
                           "ElementId, criteria and value: {0}",
                           elementIdAndCriteriaAndValue);
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "-- Impossible to receive data from open socket --", e);
        }

        return elementIdAndCriteriaAndValue;
    }

    public String getValueFromCaptor()
    {
        return value;
    }

    public String getElementIdFromCaptor()
    {
        return elementId;
    }

    public String getDetectedEntityFromCaptor()
    {
        return detectedEntity;
    }

    public void initConnexionForSimulationWithRealTypeValue(String simulationFilePath)
    {
        mappingIterator = csvUtil.readCSVFile(simulationFilePath);
        if (mappingIterator != null) {
            mappingIteratorList.add(mappingIterator);
        }
    }

    public void initConnexionForSimulationWithRandomValue(String simulationFilePath)
    {
        mappingIterator = csvUtil.readCSVFile(simulationFilePath);
        fromOneIntialValue = true;
    }

    public String getInitialValue()
    {
        lastTimeSimulatedValue = currentTime;
        if (firstLineSimulationConf == null) {
            firstLineSimulationConf = mappingIterator.next();
        }

        String value = firstLineSimulationConf.get(ResiistConstants.VALUE_HEADER);
        LOGGER.log(Level.INFO, "Initial value to send: {0}", value);
        return value;
    }

    public String getSimulatedValue(Integer mappingIteratorIndex, String elementId)
    {
        elementAndLastTimeValue.put(elementId, currentTime);
        System.out.println("Element ID " + elementId);
        String value = mappingIteratorList.get(mappingIteratorIndex)
                                          .next()
                                          .get(ResiistConstants.VALUE_HEADER);
        LOGGER.log(Level.INFO, "Simulated value to send: {0}", value);
        return value;
    }

    public int getRandomSimulatedValue(Integer minimalLimitValue, Integer maximalLimitValue,
                                       String elementId)
    {
        elementAndLastTimeValue.put(elementId, currentTime);
        randomValuesNumber--;
        int outOfMinimalLimit = 0;
        if (minimalLimitValue != null)
            outOfMinimalLimit = minimalLimitValue - ResiistConstants.OUT_OF_LIMIT;

        int outOfMaximalLimit = maximalLimitValue + ResiistConstants.OUT_OF_LIMIT;
        if (previousRandomValue == null) {
            previousRandomValue = getRandomValue(outOfMinimalLimit, outOfMaximalLimit);
        } else {
            int minimalStepValue = getMinimalStepValue(outOfMinimalLimit);
            int maximalStepValue = getMaximalStepValue(outOfMaximalLimit);
            previousRandomValue = getRandomValue(minimalStepValue, maximalStepValue);
        }

        return previousRandomValue;
    }

    public boolean isSendingTime(Integer frequency, String elementId)
    {
        if (frequency > 0) {
            currentTime = System.currentTimeMillis();
            long elapsedTime = (currentTime
                                - (elementAndLastTimeValue.get(elementId) == null
                                        ? 0
                                        : elementAndLastTimeValue.get(elementId)));
            return (elapsedTime >= (frequency * 1000));
        }

        return false;
    }

    private int getMinimalStepValue(int minimalLimitValue)
    {
        int minimalStepValue = previousRandomValue - ResiistConstants.EVOLUTION_STEP;

        if (!lessZeroRandomValues && minimalStepValue < 0) {
            minimalStepValue = 0;
        } else if (minimalStepValue < minimalLimitValue) {
            minimalStepValue = minimalLimitValue;
        }

        return getRandomValue(minimalStepValue, previousRandomValue);
    }

    public void setPreviousRandomValue(Integer previousRandomValue)
    {
        this.previousRandomValue = previousRandomValue;
    }

    private int getMaximalStepValue(int maximalLimitValue)
    {
        int maximalStepValue = previousRandomValue + ResiistConstants.EVOLUTION_STEP;
        if (maximalStepValue > maximalLimitValue) {
            maximalStepValue = maximalLimitValue;
        }
        return getRandomValue(previousRandomValue, maximalStepValue);
    }

    public boolean isFromOneIntialValue()
    {
        return fromOneIntialValue;
    }

    public boolean canSendSimulatedValue()
    {
        for (MappingIterator<Map<String, String>> mappingIterator : mappingIteratorList) {
            if (mappingIterator != null && mappingIterator.hasNext()) {
                return true;
            }
        }
        return false;
    }

    public boolean canSendRandomSimulatedValue()
    {
        return randomValuesNumber >= 0;
    }

    public void readCSVFiles(String objectAFilePath, String relationFilePath,
                             String objectBFilePath)
    {
        mappingIterator = csvUtil.readCSVFile(objectAFilePath);
        secondMappingIterator = csvUtil.readCSVFile(relationFilePath);
        thirdMappingIterator = csvUtil.readCSVFile(objectBFilePath);
    }

    public MappingIterator<Map<String, String>> getMappingIterator()
    {
        return mappingIterator;
    }

    public MappingIterator<Map<String, String>> getSecondMappingIterator()
    {
        return secondMappingIterator;
    }

    public MappingIterator<Map<String, String>> getThirdMappingIterator()
    {
        return thirdMappingIterator;
    }

    public void setLessZeroRandomValues(boolean lessZeroRandomValues)
    {
        this.lessZeroRandomValues = lessZeroRandomValues;
    }

    private static int getRandomValue(int min, int max)
    {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }

    public long getCurrentTime()
    {
        return currentTime;
    }
}