package com.axellience.resiist.client.acquisitiontointegration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

import com.axellience.resiist.client.acquisitiontointegration.acquisition.DataSource;
import com.axellience.resiist.client.acquisitiontointegration.integration.IntegrationPlatform;
import com.axellience.resiist.client.analysis.evaluation.ResilienceRuleDefiner;
import com.axellience.resiist.client.decisionmaking.DecisionMaking;
import com.axellience.resiist.client.interpretation.InterpretationRuleGenerator;
import com.axellience.resiist.client.utils.CsvUtil;
import com.axellience.resiist.client.utils.ResiistConstants;

public class AcquisitionToIntegration extends Thread
{
    private IntegrationPlatform         integrationPlatform   = IntegrationPlatform.getInstance();
    private DecisionMaking              decisionMaking;
    private DataSource                  dataSource            = new DataSource();
    private InterpretationRuleGenerator interpretationRuleOrchestrator;
    private ResilienceRuleDefiner       resilienceRuleDefiner;
    private boolean                     simulation            = false;
    private boolean                     random;
    private String                      projectId;
    private boolean                     hasSendIndicatorValue = false;
    private String                      clientId;
    private boolean                     gamaoutputs;

    public AcquisitionToIntegration()
    {
        decisionMaking = new DecisionMaking(integrationPlatform);
    }

    public AcquisitionToIntegration(InterpretationRuleGenerator businessRuleDefiner,
                                    ResilienceRuleDefiner evaluationBusinessRuleDefiner)
    {
        this.interpretationRuleOrchestrator = businessRuleDefiner;
        this.resilienceRuleDefiner = evaluationBusinessRuleDefiner;
        decisionMaking = new DecisionMaking(integrationPlatform);
    }

    public boolean connexionToModelingPlatform(String projectId, String login, String password)
    {
        this.projectId = projectId;
        this.clientId = login;
        return integrationPlatform.connexion(login, password);
    }

    public void connexionToDataSourceAsVideo()
    {
        dataSource.initConnexionWithVideoCaptor();
    }

    public void connexionToDataSourcesAsRealTypeSimulation()
    {
        String evolutionFunction = integrationPlatform.getEvolutionFunction();
        List<Map<String, String>> allSimulationFilePath =
                integrationPlatform.getAllElementAndObjectToDetectAndFrequenciesAndSimulationFilePaths();
        for (Map<String, String> elementIDAndSimulationFilePath : allSimulationFilePath) {

            String simulationFilePath =
                    elementIDAndSimulationFilePath.get(ResiistConstants.SIMULATION_FILE_PATH);
            if (evolutionFunction == null) {
                dataSource.initConnexionForSimulationWithRealTypeValue(simulationFilePath);
            } else if (evolutionFunction.equals("random")) {
                dataSource.initConnexionForSimulationWithRandomValue(simulationFilePath);
                dataSource.setLessZeroRandomValues(false);
                random = true;
            }
        }
        simulation = true;
        gamaoutputs = false;
    }

    public void connexionToDataSourcesAsGamaOutputs()
    {
        List<Map<String, String>> allSimulationFilePath =
                integrationPlatform.getAllElementAndObjectToDetectAndFrequenciesAndSimulationFilePaths();
        for (Map<String, String> elementIDAndSimulationFilePath : allSimulationFilePath) {
            String elementName = elementIDAndSimulationFilePath.get(ResiistConstants.ELEMENT_NAME);
            String gamaRoomSimulationFilePath =
                    elementIDAndSimulationFilePath.get(ResiistConstants.GAMA_SIMULATION_FILE_PATH);
            if (gamaRoomSimulationFilePath != null) {
                String indicatorSimulationFilePath =
                        toGamaIndicatorFilePath(elementName, gamaRoomSimulationFilePath);
                dataSource.initConnexionForSimulationWithRealTypeValue(indicatorSimulationFilePath);
            }
        }
        simulation = true;
        gamaoutputs = true;
    }

    private String toGamaIndicatorFilePath(String elementName, String gamaRoomSimulationFilePath)
    {
        CsvUtil csvUtil = new CsvUtil();
        String gamaElementName = getGamaElementName(elementName);
        String gamaIndicatorFilePAth =
                csvUtil.createFileWithOneColumAndSomeLinesnOnly(gamaRoomSimulationFilePath,
                                                                gamaElementName,
                                                                10);
        return gamaIndicatorFilePAth;
    }

    private String getGamaElementName(String elementName)
    {
        switch (elementName) {
            case "Occupancy rate":
                return "#taux occupation";
            case "People number affected by a terrorist":
            case "Global People number affected by a terrorist":
                return " #nombre morts";
            case "Terrorist traveled distance":
                return "#distance terroriste";
            case "Global Event duration":
                return "#duree evenement";
            case "Global Terrorist traveled distance":
                return "#distance global terroriste";
            case "Train station occupancy rate":
                return "#taux global occupation";
        }
        return "";
    }

    public ResourceImpl getModelFromServerAsBinary()
    {
        ResourceImpl resourceImpl = integrationPlatform.getArchimateModel(projectId);
        decisionMaking.setModelResource(resourceImpl);
        return resourceImpl;
    }

    public void integrationConfiguration()
    {
        integrationPlatform.setInterpretationRuleOrchestrator(interpretationRuleOrchestrator);
        integrationPlatform.integrationConfiguration();
    }

    public void interpretationConfiguration()
    {
        integrationPlatform.interpretationConfiguration();
    }

    @Override
    public void run()
    {
        for (Map<String, Object> elementWidgetIDAndDTO : integrationPlatform.allSimulatedIndicators) {
            String elementId =
                    (String) elementWidgetIDAndDTO.get(ResiistConstants.ELEMENT_IDENTIFIER);
            if (!elementId.equals(projectId)) {
                interpretationRuleOrchestrator.addValueToHistoricInterpretation(elementId, "");
                interpretationRuleOrchestrator.addResilienceToHistoricInterpretation(elementId, "");
            }
        }
        // List<Map<String, String>> interpretedIndicators =
        // integrationPlatform.getInterpretationRuleOrchestrator().getInterpretedIndicators();
        List<Map<String, Object>> csvSimulatedIndicators =
                integrationPlatform.allSimulatedIndicators.stream()
                                                          .filter(indic -> (indic.get(ResiistConstants.SIMULATION_FILE_PATH) != null
                                                                            && new CsvUtil().isACSVFile((String) indic.get(ResiistConstants.SIMULATION_FILE_PATH))))
                                                          .collect(Collectors.toList());

        List<Map<String, Object>> gamaSimulatedIndicators =
                integrationPlatform.allSimulatedIndicators.stream()
                                                          .filter(indic -> (indic.get(ResiistConstants.GAMA_SIMULATION_FILE_PATH) != null
                                                                            && new CsvUtil().isACSVFile((String) indic.get(ResiistConstants.GAMA_SIMULATION_FILE_PATH))))
                                                          .collect(Collectors.toList());

        while (true) {
            if (dataSource.canSendValue()) {
                hasSendIndicatorValue = true;
                dataSource.requestCriteriaAndValue();
                String elementId = dataSource.getElementIdFromCaptor();
                String value = dataSource.getValueFromCaptor();
                if (!value.isEmpty()) {
                    Float resilience =
                            resilienceRuleDefiner.evaluateElementResilience(elementId, value);
                    interpretationRuleOrchestrator.addValueToHistoricInterpretation(elementId,
                                                                                    value);
                    interpretationRuleOrchestrator.addResilienceToHistoricInterpretation(elementId,
                                                                                         resilience.toString());
                    // Sans aggregzation fonction
                    // Float modelResilience =
                    // resilienceRuleDefiner.evaluateModelResilience(elementId,
                    // resilience);

                    // Avec aggregzation fonction
                    BinaryOperator<Float> aggregationFunction =
                            resilienceRuleDefiner.getAggregationFunction(elementId);
                    Float modelResilience =
                            resilienceRuleDefiner.evaluateModelResilience(elementId,
                                                                          resilience,
                                                                          aggregationFunction);

                    String resilienceAlertColor =
                            resilienceRuleDefiner.getResilienceAlertColor(elementId, resilience);

                    sendValue(elementId, value, resilience, resilienceAlertColor, modelResilience);
                }
            } else {
                hasSendIndicatorValue = false;
            }

            if (dataSource.canSendSimulatedValue()) {
                hasSendIndicatorValue = true;
                if (!gamaoutputs) {
                    for (Map<String, Object> elementWidgetIDAndDTO : csvSimulatedIndicators) {
                        String elementId =
                                (String) elementWidgetIDAndDTO.get(ResiistConstants.ELEMENT_IDENTIFIER);
                        Integer frequency = interpretationRuleOrchestrator.getFrequency(elementId);
                        if (dataSource.isSendingTime(frequency, elementId)) {
                            Integer simulatedIndicatorIndex =
                                    csvSimulatedIndicators.indexOf(elementWidgetIDAndDTO);
                            String simulatedValue =
                                    dataSource.getSimulatedValue(simulatedIndicatorIndex,
                                                                 elementId);
                            Float resilience =
                                    resilienceRuleDefiner.evaluateElementResilience(elementId,
                                                                                    simulatedValue);
                            interpretationRuleOrchestrator.addValueToHistoricInterpretation(elementId,
                                                                                            simulatedValue);
                            interpretationRuleOrchestrator.addResilienceToHistoricInterpretation(elementId,
                                                                                                 resilience.toString());
                            Float modelResilience =
                                    resilienceRuleDefiner.evaluateModelResilience(elementId,
                                                                                  resilience);
                            String resilienceAlertColor =
                                    resilienceRuleDefiner.getResilienceAlertColor(elementId,
                                                                                  resilience);
                            sendValue(elementId,
                                      simulatedValue,
                                      resilience,
                                      resilienceAlertColor,
                                      modelResilience);
                        }
                    }
                } else {

                    for (Map<String, Object> elementWidgetIDAndDTO : gamaSimulatedIndicators) {
                        String elementId =
                                (String) elementWidgetIDAndDTO.get(ResiistConstants.ELEMENT_IDENTIFIER);
                        Integer frequency = interpretationRuleOrchestrator.getFrequency(elementId);
                        if (dataSource.isSendingTime(frequency, elementId)) {
                            Integer simulatedIndicatorIndex =
                                    gamaSimulatedIndicators.indexOf(elementWidgetIDAndDTO);
                            String simulatedValue =
                                    dataSource.getSimulatedValue(simulatedIndicatorIndex,
                                                                 elementId);
                            Float resilience =
                                    resilienceRuleDefiner.evaluateElementResilience(elementId,
                                                                                    simulatedValue);
                            interpretationRuleOrchestrator.addValueToHistoricInterpretation(elementId,
                                                                                            simulatedValue);
                            interpretationRuleOrchestrator.addResilienceToHistoricInterpretation(elementId,
                                                                                                 resilience.toString());
                            Float modelResilience =
                                    resilienceRuleDefiner.evaluateModelResilience(elementId,
                                                                                  resilience);
                            String resilienceAlertColor =
                                    resilienceRuleDefiner.getResilienceAlertColor(elementId,
                                                                                  resilience);
                            sendValue(elementId,
                                      simulatedValue,
                                      resilience,
                                      resilienceAlertColor,
                                      modelResilience);
                        }
                    }
                }

            } else {
                hasSendIndicatorValue = false;
            }

            // if (!interpretedIndicators.isEmpty()) {
            // for (Map<String, String> interpretedIndicator :
            // interpretedIndicators) {
            // String elementId =
            // interpretedIndicator.get(ResiistConstants.ELEMENT_IDENTIFIER);
            // Integer frequency =
            // interpretationRuleOrchestrator.getFrequency(elementId);
            // if (dataSource.isSendingTime(frequency, elementId)) {
            // String interpretedValue =
            // interpretationRuleOrchestrator.getInterpretedValue(elementId,
            // interpretationRuleOrchestrator.getHistoricValue(elementId));
            // if (!interpretedValue.isEmpty()) {
            // Float resilience =
            // resilienceRuleDefiner.evaluateElementResilience(elementId,
            // interpretedValue);
            // interpretationRuleOrchestrator.addValueToHistoricInterpretation(elementId,
            // interpretedValue);
            // interpretationRuleOrchestrator.addResilienceToHistoricInterpretation(elementId,
            // resilience.toString());
            // Float modelResilience =
            // resilienceRuleDefiner.evaluateModelResilience(elementId,
            // resilience);
            // String resilienceAlertColor =
            // resilienceRuleDefiner.getResilienceAlertColor(elementId,
            // resilience);
            // sendValue(elementId,
            // interpretedValue,
            // resilience,
            // resilienceAlertColor,
            // modelResilience);
            // }
            // }
            // }
            // }

            // if (dataSource.canSendRandomSimulatedValue() &&
            // dataSource.isSendingTime()) {
            // String value =
            //
            // String.valueOf(dataSource.getRandomSimulatedValue(interpreter.getMinLimitValueForIndicator(indicatorFromSimulationConf),
            // //
            // interpreter.getMaxLimitValueForIndicator(indicatorFromSimulationConf)));
            // sendValue(indicatorFromSimulationConf, value);
            // }
            // if (dataSource.isSendingTime()) {
            // integrationPlatform.estimateGlobalResilience();
            // }
        }

    }

    // @Override
    // public void run()
    // {
    //// if (!simulation) {
    // while (dataSource.canSendValue()) {
    // dataSource.requestCriteriaAndValue();
    // sendValue(dataSource.getIndicatorFromCaptor(),
    // dataSource.getValueFromCaptor());
    // }
    //// } else
    // runSimulation();
    // }
    //
    // private void runSimulation()
    // {
    // String indicatorFromSimulationConf =
    // dataSource.getIndicatorFromSimulationConf();
    // if (!random) {
    // while (dataSource.canSendSimulatedValue()) {
    // if (dataSource.isSendingTime()) {
    // sendValue(indicatorFromSimulationConf, dataSource.getSimulatedValue());
    // }
    // }
    // } else {
    // if (dataSource.isFromOneIntialValue()) {
    // String value = dataSource.getInitialValue();
    // dataSource.setPreviousRandomValue(Integer.valueOf(value));
    // sendValue(indicatorFromSimulationConf, value);
    // }
    // while (dataSource.canSendRandomSimulatedValue()) {
    // if (dataSource.isSendingTime()) {
    // String value =
    // String.valueOf(dataSource.getRandomSimulatedValue(interpreter.getMinLimitValueForIndicator(indicatorFromSimulationConf),
    // interpreter.getMaxLimitValueForIndicator(indicatorFromSimulationConf)));
    // sendValue(indicatorFromSimulationConf, value);
    // }
    // }
    // }
    // }

    private void sendValue(String indicatorId, String value, Float resilience,
                           String resilienceAlertColor, Float modelResilience)
    {
        integrationPlatform.setFnForIndicator(indicatorId, value);
        decisionMaking.setIndicatorValueInCorrespondingPerformance(indicatorId, value);
        integrationPlatform.setColorForIndicator(indicatorId, resilienceAlertColor);
        integrationPlatform.setResilienceForIndicator(indicatorId, resilience);
        integrationPlatform.setModelResilience(modelResilience);
    }

    public List<String> getIndicatorIds()
    {
        List<String> indicatorIds = new ArrayList<>();
        for (Map<String, Object> elementWidgetIDAndDTO : integrationPlatform.allSimulatedIndicators) {
            indicatorIds.add((String) elementWidgetIDAndDTO.get(ResiistConstants.ELEMENT_IDENTIFIER));
        }

        return indicatorIds;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public String getClientId()
    {
        return clientId;
    }

    public boolean hasSendIndicatorValue()
    {
        return hasSendIndicatorValue;
    }

}