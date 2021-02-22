package com.axellience.resiist.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.axellience.resiist.client.acquisition.DataSource;
import com.axellience.resiist.client.analysis.evaluation.ResilienceRuleDefiner;
import com.axellience.resiist.client.integration.IntegrationPlatform;
import com.axellience.resiist.client.interpretation.InterpretationRuleOrchestrator;
import com.axellience.resiist.client.utils.CsvUtil;
import com.axellience.resiist.client.utils.ResiistConstants;
import com.axellience.resiist.client.utils.ihm.AccessApplication;

public class AcquisitionToIntegration extends Thread
{
    private IntegrationPlatform            integrationPlatform = IntegrationPlatform.getInstance();
    private DataSource                     dataSource          = new DataSource();
    private InterpretationRuleOrchestrator interpretationRuleOrchestrator;
    private ResilienceRuleDefiner          resilienceRuleDefiner;
    private boolean                        simulation          = false;
    private boolean                        random;
    private String                         projectId;

    public AcquisitionToIntegration(InterpretationRuleOrchestrator businessRuleDefiner,
                                    ResilienceRuleDefiner evaluationBusinessRuleDefiner)
    {
        this.interpretationRuleOrchestrator = businessRuleDefiner;
        this.resilienceRuleDefiner = evaluationBusinessRuleDefiner;
    }

    public void connexionToModelingPlatform()
    {
        AccessApplication accessApplication = AccessApplication.getInstance(integrationPlatform);
        while (true) {
            boolean loginDone = accessApplication.isLoginDone();
            try {
                Thread.sleep(2);
                if (loginDone) {
                    projectId = accessApplication.getProjectId();
                    break;
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // integrationPlatform.connexion("indicator", "password");
        // projectId = "_VosXMFzJEeuFdrwv9XdLnA";
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
    }

    public void getModelFromServerAsBinary()
    {
        integrationPlatform.getArchimateModel(projectId);
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
                interpretationRuleOrchestrator.addToHistoricInterpretation(elementId, "");
            }
        }
        List<Map<String, String>> interpretedIndicators =
                integrationPlatform.getInterpretationRuleOrchestrator().getInterpretedIndicators();
        List<Map<String, Object>> csvSimulatedIndicators =
                integrationPlatform.allSimulatedIndicators.stream()
                                                          .filter(indic -> indic.get(ResiistConstants.SIMULATION_FILE_PATH) != null
                                                                           && new CsvUtil().isACSVFile((String) indic.get(ResiistConstants.SIMULATION_FILE_PATH)))
                                                          .collect(Collectors.toList());

        while (true) {
            if (dataSource.canSendValue()) {
                dataSource.requestCriteriaAndValue();
                String elementId = dataSource.getElementIdFromCaptor();
                String value = dataSource.getValueFromCaptor();
                if (!value.isEmpty()) {
                    interpretationRuleOrchestrator.addToHistoricInterpretation(elementId, value);
                    Float resilience =
                            resilienceRuleDefiner.evaluateElementResilience(elementId, value);
                    Float modelResilience =
                            resilienceRuleDefiner.evaluateModelResilience(elementId, resilience);
                    String resilienceAlertColor =
                            resilienceRuleDefiner.getResilienceAlertColor(elementId, resilience);

                    sendValue(elementId, value, resilience, resilienceAlertColor, modelResilience);
                }
            }

            if (dataSource.canSendSimulatedValue()) {
                for (Map<String, Object> elementWidgetIDAndDTO : csvSimulatedIndicators) {
                    String elementId =
                            (String) elementWidgetIDAndDTO.get(ResiistConstants.ELEMENT_IDENTIFIER);
                    Integer frequency = interpretationRuleOrchestrator.getFrequency(elementId);
                    if (dataSource.isSendingTime(frequency, elementId)) {
                        Integer simulatedIndicatorIndex =
                                csvSimulatedIndicators.indexOf(elementWidgetIDAndDTO);
                        String simulatedValue =
                                dataSource.getSimulatedValue(simulatedIndicatorIndex, elementId);
                        interpretationRuleOrchestrator.addToHistoricInterpretation(elementId,
                                                                                   simulatedValue);
                        Float resilience =
                                resilienceRuleDefiner.evaluateElementResilience(elementId,
                                                                                simulatedValue);
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

            if (!interpretedIndicators.isEmpty()) {
                for (Map<String, String> interpretedIndicator : interpretedIndicators) {
                    String elementId =
                            interpretedIndicator.get(ResiistConstants.ELEMENT_IDENTIFIER);
                    Integer frequency = interpretationRuleOrchestrator.getFrequency(elementId);
                    if (dataSource.isSendingTime(frequency, elementId)) {
                        String interpretedValue =
                                interpretationRuleOrchestrator.getInterpretedValue(elementId,
                                                                                   interpretationRuleOrchestrator.getHistoricValue(elementId));
                        if (!interpretedValue.isEmpty()) {
                            interpretationRuleOrchestrator.addToHistoricInterpretation(elementId,
                                                                                       interpretedValue);
                            Float resilience =
                                    resilienceRuleDefiner.evaluateElementResilience(elementId,
                                                                                    interpretedValue);
                            Float modelResilience =
                                    resilienceRuleDefiner.evaluateModelResilience(elementId,
                                                                                  resilience);
                            String resilienceAlertColor =
                                    resilienceRuleDefiner.getResilienceAlertColor(elementId,
                                                                                  resilience);
                            sendValue(elementId,
                                      interpretedValue,
                                      resilience,
                                      resilienceAlertColor,
                                      modelResilience);
                        }
                    }
                }
            }

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
        integrationPlatform.setColorForIndicator(indicatorId, resilienceAlertColor);
        integrationPlatform.setResilienceForIndicator(indicatorId, resilience);
        integrationPlatform.setModelResilience(modelResilience);
    }
}