package com.axellience.resiist.client.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.cometd.common.JacksonJSONContextClient;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.opengroup.archimate.impl.ModelImpl;

import com.axellience.resiist.client.integration.authentication.Oauth2HttpClient;
import com.axellience.resiist.client.interpretation.InterpretationRuleOrchestrator;
import com.axellience.resiist.client.utils.ModelUtils;
import com.axellience.resiist.client.utils.ResiistConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genmymodel.api.dto.indicator.IndicatorDto;
import com.genmymodel.ecoreonline.graphic.GMMUtil;

public class IntegrationPlatform
{
    private Oauth2HttpClient httpClient;

    private String evolutionFunction;

    private static final Logger            LOGGER =
            Logger.getLogger(IntegrationPlatform.class.getName());
    public List<Map<String, Object>>       allSimulatedIndicators;
    private InterpretationRuleOrchestrator interpretationRuleOrchestrator;
    private ResourceImpl                   modelResource;
    private String                         projectId;

    private static IntegrationPlatform instance = null;

    private IntegrationPlatform()
    {
        SslContextFactory sslContextFactory = new SslContextFactory(true);
        httpClient = new Oauth2HttpClient(sslContextFactory);
    }

    public static IntegrationPlatform getInstance()
    {
        if (instance == null)
            instance = new IntegrationPlatform();

        return instance;
    }

    public boolean connexion(String login, String password)
    {
        try {
            httpClient.start();
            httpClient.login(login, password, ResiistConstants.APIURL);
            return true;
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error: {0}", e);
            return false;
        }
    }

    public void getArchimateModel(String projectId)
    {
        try {
            this.projectId = projectId;
            ContentResponse contentResponse =
                    httpClient.GET(ResiistConstants.APIURL + "/projects/" + projectId + "/data");
            byte[] binaryModel = contentResponse.getContent();
            modelResource = (ResourceImpl) ModelUtils.importArchimateModel(projectId, binaryModel);
        }
        catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.log(Level.INFO, "Problem to get model from server - ", e);
        }
    }

    public void integrationConfiguration()
    {
        List<Map<String, String>> elementWidgetAndIds = getElementWidgetAndId();
        allSimulatedIndicators = new ArrayList<>();

        for (Map<String, String> elementWidgetAndId : elementWidgetAndIds) {
            Map<String, Object> elementWidgetIDAndDTO = new HashMap<>();

            String elementIdentifierHeader =
                    elementWidgetAndId.get(ResiistConstants.ELEMENT_IDENTIFIER);
            elementWidgetIDAndDTO.put(ResiistConstants.ELEMENT_IDENTIFIER, elementIdentifierHeader);
            elementWidgetIDAndDTO.put(ResiistConstants.ELEMENT_NAME,
                                      elementWidgetAndId.get(ResiistConstants.ELEMENT_NAME));
            elementWidgetIDAndDTO.put(ResiistConstants.SIMULATION_FILE_PATH,
                                      elementWidgetAndId.get(ResiistConstants.SIMULATION_FILE_PATH));

            elementWidgetIDAndDTO.put(ResiistConstants.INDICATOR_DTO,
                                      toIndicatorDTO(elementIdentifierHeader));
            allSimulatedIndicators.add(elementWidgetIDAndDTO);
        }
        interpretationRuleOrchestrator.setNotInterpretedIndicators(allSimulatedIndicators);
    }

    public void interpretationConfiguration()
    {
        interpretationRuleOrchestrator.interpretationConfiguration(modelResource);
    }

    public void setFnForIndicator(String indicatorId, String value)
    {
        setValueForIndicator(indicatorId, "fn", value);
    }

    public void setResilienceForIndicator(String indicatorId, Float resilience)
    {
        setValueForIndicator(indicatorId, "resilience", resilience);
    }

    public void setModelResilience(Float modelResilience)
    {
        setValueForIndicator(projectId, "resilience", modelResilience);
    }

    private void setValueForIndicator(String indicatorId, String key, Object value)
    {
        try {
            JacksonJSONContextClient jsonContext = new JacksonJSONContextClient();
            ObjectMapper objectMapper = jsonContext.getObjectMapper();

            List<IndicatorDto> indicatorDtos = getTargetedDtos(indicatorId);
            if (!indicatorDtos.isEmpty()) {
                for (IndicatorDto indicatorDto : indicatorDtos) {
                    indicatorDto.setKey(key);
                    indicatorDto.setValue(String.valueOf(value));
                    // LOGGER.log(Level.INFO, "Send value {0}", value);

                    String jsonIndicator = objectMapper.writeValueAsString(indicatorDto);

                    Request request =
                            httpClient.POST(ResiistConstants.APIURL + "/indicators/" + projectId);
                    request.header(HttpHeader.CONTENT_TYPE, "application/json");
                    request.content(new StringContentProvider(jsonIndicator));

                    Response serverResponse = request.send();
                    // LOGGER.log(Level.INFO, "{0}", serverResponse);
                }
            }
        }
        catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Write JSON failed: {0}", e);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error: {0}", e);
        }
    }

    public void setColorForIndicator(String indicatorId, String resilienceAlertColor)
    {
        try {
            // LOGGER.log(Level.INFO, "Send value {0}", resilienceAlertColor);

            Request request = httpClient.POST(ResiistConstants.APIURL
                                              + "/indicators/"
                                              + projectId
                                              + "/"
                                              + indicatorId);
            request.header(HttpHeader.CONTENT_TYPE, "application/json");
            request.content(new StringContentProvider(resilienceAlertColor));

            Response serverResponse = request.send();
            // LOGGER.log(Level.INFO, "{0}", serverResponse);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error: {0}", e);
        }
    }

    private List<IndicatorDto> getTargetedDtos(String indicatorId)
    {
        List<IndicatorDto> indicatorDtos = new ArrayList<>();
        List<IndicatorDto> dupplicateIndicatorDtos =
                allSimulatedIndicators.stream()
                                      .filter(confParam -> ((IndicatorDto) confParam.get(ResiistConstants.INDICATOR_DTO)).getElementId()
                                                                                                                         .equals(indicatorId))
                                      .map(confParam -> ((IndicatorDto) confParam.get(ResiistConstants.INDICATOR_DTO)))
                                      .collect(Collectors.toList());

        for (IndicatorDto indicatorDto : dupplicateIndicatorDtos) {
            if (!isAlreadyPresent(indicatorDto.getElementId(), indicatorDtos)) {
                indicatorDtos.add(indicatorDto);
            }
        }

        if (indicatorDtos.isEmpty()) {
            List<String> correspondingCriterias = getCorrespondingCriterias(indicatorId);
            indicatorDtos = correspondingCriterias.stream()
                                                  .map(this::getTargetedDtos)
                                                  .flatMap(List::stream)
                                                  .collect(Collectors.toList());

        }

        return indicatorDtos;
    }

    private boolean isAlreadyPresent(String elementId, List<IndicatorDto> indicatorDtos)
    {
        for (IndicatorDto indicatorDto : indicatorDtos) {
            if (elementId.equals(indicatorDto.getElementId())) {
                return true;
            }
        }
        return false;
    }

    public List<String> getCorrespondingCriterias(String indicatorName)
    {
        Map<String, List<String>> criteriasAndIndicators =
                interpretationRuleOrchestrator.getCriteriasAndIndicators();
        return criteriasAndIndicators.entrySet()
                                     .stream()
                                     .filter(entrySet -> entrySet.getValue()
                                                                 .contains(indicatorName))
                                     .map(Entry::getKey)
                                     .distinct()
                                     .collect(Collectors.toList());
    }

    public String getEvolutionFunction()
    {
        return (String) allSimulatedIndicators.get(0)
                                              .get(ResiistConstants.EVOLUTION_FUNCTION_HEADER);
    }

    private List<Map<String, String>> getElementWidgetAndId()
    {
        List<Map<String, String>> elementAndWidgetIds = new ArrayList<>();
        TreeIterator<EObject> iterator = modelResource.getAllContents();
        while (iterator.hasNext()) {
            EObject nextEObject = iterator.next();

            if (nextEObject instanceof EModelElement) {
                EModelElement modelElement = (EModelElement) nextEObject;
                if (ModelUtils.hasFrequency(modelElement) || modelElement instanceof ModelImpl) {
                    String elementId = GMMUtil.getUUID(modelElement);
                    Map<String, String> elementAndWidgetId = new HashMap<>();
                    elementAndWidgetId.put(ResiistConstants.ELEMENT_IDENTIFIER, elementId);
                    ModelUtils.getName(modelElement)
                              .ifPresent(name -> elementAndWidgetId.put(ResiistConstants.ELEMENT_NAME,
                                                                        name));
                    if (modelElement instanceof ModelImpl) {
                        elementAndWidgetId.put(ResiistConstants.SIMULATION_FILE_PATH, "");
                    } else if (ModelUtils.hasSimulationPath(modelElement))
                        elementAndWidgetId.put(ResiistConstants.SIMULATION_FILE_PATH,
                                               ModelUtils.getSimulationFilePath(modelElement));

                    elementAndWidgetIds.add(elementAndWidgetId);
                }
            }
        }
        return elementAndWidgetIds;
    }

    public List<Map<String, String>> getAllElementAndObjectToDetectAndFrequenciesAndSimulationFilePaths()
    {
        List<Map<String, String>> allElementIdAndFrequencyAndSimulationFilePath = new ArrayList<>();
        TreeIterator<EObject> iterator = modelResource.getAllContents();
        while (iterator.hasNext()) {
            EObject nextEObject = iterator.next();

            if (nextEObject instanceof EModelElement) {
                EModelElement modelElement = (EModelElement) nextEObject;
                Map<String, String> elementIdAndSimulationFilePath = new HashMap<>();
                if (ModelUtils.hasFrequency(modelElement)
                    && ModelUtils.hasSimulationPath(modelElement)
                    && !ModelUtils.getSimulationFilePath(modelElement).isEmpty())
                {

                    elementIdAndSimulationFilePath.put(ResiistConstants.ELEMENT_IDENTIFIER,
                                                       GMMUtil.getUUID(modelElement));
                    elementIdAndSimulationFilePath.put(ResiistConstants.FREQUENCY,
                                                       ModelUtils.getFrequency(modelElement));
                    elementIdAndSimulationFilePath.put(ResiistConstants.SIMULATION_FILE_PATH,
                                                       ModelUtils.getSimulationFilePath(modelElement));
                    if (!ModelUtils.getObjectsToDetect(modelElement).isEmpty()) {
                        elementIdAndSimulationFilePath.put(ResiistConstants.OBJECTS_TO_DETECT,
                                                           ModelUtils.getObjectsToDetect(modelElement));
                    }
                    allElementIdAndFrequencyAndSimulationFilePath.add(elementIdAndSimulationFilePath);
                } else if (ModelUtils.hasVideoSimulationPath(modelElement)) {
                    elementIdAndSimulationFilePath.put(ResiistConstants.SIMULATION_FILE_PATH,
                                                       ModelUtils.getSimulationFilePath(modelElement));
                    allElementIdAndFrequencyAndSimulationFilePath.add(elementIdAndSimulationFilePath);
                }
            }
        }
        return allElementIdAndFrequencyAndSimulationFilePath;
    }

    public IndicatorDto toIndicatorDTO(String elementId)
    {
        IndicatorDto indicatorDto = new IndicatorDto();
        indicatorDto.setElementId(elementId);
        return indicatorDto;
    }

    public Resource getModelResource()
    {
        return modelResource;
    }

    public InterpretationRuleOrchestrator getInterpretationRuleOrchestrator()
    {
        return interpretationRuleOrchestrator;
    }

    public void setInterpretationRuleOrchestrator(InterpretationRuleOrchestrator interpretationRuleOrchestrator)
    {
        this.interpretationRuleOrchestrator = interpretationRuleOrchestrator;
    }
}