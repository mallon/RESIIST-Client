package com.axellience.resiist.client.acquisitiontointegration.integration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.cometd.common.JacksonJSONContextClient;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.opengroup.archimate.Grouping;
import org.opengroup.archimate.LangString;
import org.opengroup.archimate.Referenceable;
import org.opengroup.archimate.Value;
import org.opengroup.archimate.impl.ModelImpl;

import com.axellience.resiist.client.acquisitiontointegration.dtos.CustomPropertyDto;
import com.axellience.resiist.client.acquisitiontointegration.integration.authentication.Oauth2HttpClient;
import com.axellience.resiist.client.interpretation.InterpretationRuleGenerator;
import com.axellience.resiist.client.utils.ModelUtils;
import com.axellience.resiist.client.utils.ResiistConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genmymodel.api.dto.commands.CompoundCommandResource;
import com.genmymodel.api.dto.commands.ObjectIDResource;
import com.genmymodel.api.dto.commands.SetCommandResource;
import com.genmymodel.api.dto.indicator.IndicatorDto;
import com.genmymodel.archimatediag.TextWidget;
import com.genmymodel.ecoreonline.graphic.GMMUtil;

public class IntegrationPlatform
{
    private Oauth2HttpClient httpClient;

    private String evolutionFunction;

    private static final Logger      LOGGER = Logger.getLogger(IntegrationPlatform.class.getName());
    public List<Map<String, Object>> allSimulatedIndicators;

    private InterpretationRuleGenerator interpretationRuleOrchestrator;
    private ResourceSet                 resourceSet;
    private ResourceImpl                modelResource;
    private String                      projectId;

    private String login;

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
            this.login = login;
            return true;
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error: {0}", e);
            return false;
        }
    }

    public ResourceImpl getArchimateModel(String projectId)
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

        resourceSet = new ResourceSetImpl();
        resourceSet.getResources().add(modelResource);
        return modelResource;
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
            elementWidgetIDAndDTO.put(ResiistConstants.GAMA_SIMULATION_FILE_PATH,
                                      elementWidgetAndId.get(ResiistConstants.GAMA_SIMULATION_FILE_PATH));

            IndicatorDto indicatorDTO = toIndicatorDTO(elementIdentifierHeader);
            elementWidgetIDAndDTO.put(ResiistConstants.INDICATOR_DTO, indicatorDTO);
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

    public void setValueForPerf(Value perf, String oldValue, Double influencedIndicatorValue)
    {
        try {
            JacksonJSONContextClient jsonContext = new JacksonJSONContextClient();
            ObjectMapper objectMapper = jsonContext.getObjectMapper();

            String influencedIndicatorValueString = Double.toString(influencedIndicatorValue);

            CompoundCommandResource compoundCommandResource = new CompoundCommandResource();
            compoundCommandResource.setClientId(login);
            compoundCommandResource.setLabel("_UI_AbstractCommand_label");

            SetCommandResource setCommandResource =
                    buildSetNameCmdRes(perf.getName().get(0),
                                       oldValue,
                                       influencedIndicatorValueString);

            compoundCommandResource.getCommands().add(setCommandResource);

            String jsonCommands = objectMapper.writeValueAsString(compoundCommandResource);
            jsonCommands = "[" + jsonCommands + "]";
            Request request = httpClient.POST(ResiistConstants.APIURL
                                              + "/projects/"
                                              + projectId
                                              + "/commands");
            request.header(HttpHeader.CONTENT_TYPE, "application/json");
            request.content(new StringContentProvider(jsonCommands));

            Response serverResponse = request.send();
        }
        catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Write JSON failed: {0}", e);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error: {0}", e);
        }
    }

    public void setValueForDecisionInReport(Grouping decisionReport, String detailIndex,
                                            String oldValue, String value)
    {
        try {
            JacksonJSONContextClient jsonContext = new JacksonJSONContextClient();
            ObjectMapper objectMapper = jsonContext.getObjectMapper();

            CompoundCommandResource compoundCommandResource = new CompoundCommandResource();
            compoundCommandResource.setClientId(login);
            compoundCommandResource.setLabel("_UI_AbstractCommand_label");

            SetCommandResource commandResource =
                    buildSetNameCmdResForDetail(decisionReport, detailIndex, oldValue, value);

            compoundCommandResource.getCommands().add(commandResource);

            String jsonCommands = objectMapper.writeValueAsString(compoundCommandResource);
            jsonCommands = "[" + jsonCommands + "]";
            Request request = httpClient.POST(ResiistConstants.APIURL
                                              + "/projects/"
                                              + projectId
                                              + "/commands");
            request.header(HttpHeader.CONTENT_TYPE, "application/json");
            request.content(new StringContentProvider(jsonCommands));

            Response serverResponse = request.send();
        }
        catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Write JSON failed: {0}", e);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error: {0}", e);
        }
    }

    private SetCommandResource buildSetNameCmdResForDetail(EObject modelElement, String detailIndex,
                                                           String oldValue, String value)
    {
        SetCommandResource setNameCommandResource = new SetCommandResource();
        setNameCommandResource.setFeature("value");
        setNameCommandResource.setOldValue(oldValue);
        setNameCommandResource.setValue(value);
        setNameCommandResource.setHash(setNameCommandResource.hashCode());

        String elementId = GMMUtil.getUUID(modelElement);
        String elementUri =
                GMMUtil.createGmmUri(projectId)
                       .toString() + "#" + elementId + "/%genmymodel%/@details." + detailIndex;
        ObjectIDResource objectIDResource = new ObjectIDResource(elementUri, null);
        objectIDResource.setMetaClassURI("http://www.eclipse.org/emf/2002/Ecore#//EStringToStringMapEntry");
        setNameCommandResource.setObjectId(objectIDResource);

        return setNameCommandResource;
    }

    private SetCommandResource buildSetNameCmdRes(LangString valueForOneLanguage, String oldValue,
                                                  String influencedIndicatorValue)
    {
        SetCommandResource setNameCommandResource = new SetCommandResource();
        setNameCommandResource.setFeature("value");
        setNameCommandResource.setOldValue(oldValue);
        setNameCommandResource.setValue(influencedIndicatorValue);
        setNameCommandResource.setHash(setNameCommandResource.hashCode());

        String elementId = GMMUtil.getUUID(valueForOneLanguage);
        String elementUri = GMMUtil.createGmmUri(projectId).toString() + "#" + elementId;
        ObjectIDResource objectIDResource = new ObjectIDResource(elementUri, elementId);
        objectIDResource.setMetaClassURI("http://www.opengroup.org/xsd/archimate/3.0/#//LangString");
        setNameCommandResource.setObjectId(objectIDResource);

        return setNameCommandResource;
    }

    public void setValueForTextWidget(TextWidget textWidget, String oldValue, String newValue)
    {
        try {
            JacksonJSONContextClient jsonContext = new JacksonJSONContextClient();
            ObjectMapper objectMapper = jsonContext.getObjectMapper();

            CompoundCommandResource compoundCommandResource = new CompoundCommandResource();
            compoundCommandResource.setClientId(login);
            compoundCommandResource.setLabel("_UI_AbstractCommand_label");

            SetCommandResource setCommandResource =
                    buildSetNameCmdResForTW(textWidget, oldValue, newValue);

            compoundCommandResource.getCommands().add(setCommandResource);

            String jsonCommands = objectMapper.writeValueAsString(compoundCommandResource);
            jsonCommands = "[" + jsonCommands + "]";
            Request request = httpClient.POST(ResiistConstants.APIURL
                                              + "/projects/"
                                              + projectId
                                              + "/commands");
            request.header(HttpHeader.CONTENT_TYPE, "application/json");
            request.content(new StringContentProvider(jsonCommands));
            request.send();
        }
        catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Write JSON failed: {0}", e);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error: {0}", e);
        }
    }

    private SetCommandResource buildSetNameCmdResForTW(TextWidget textWidget, String oldValue,
                                                       String newValue)
    {
        SetCommandResource setNameCommandResource = new SetCommandResource();
        setNameCommandResource.setFeature("value");
        setNameCommandResource.setOldValue(oldValue);
        setNameCommandResource.setValue(newValue);
        setNameCommandResource.setHash(setNameCommandResource.hashCode());

        String elementId = GMMUtil.getUUID(textWidget);
        String elementUri = GMMUtil.createGmmUri(projectId).toString() + "#" + elementId;
        ObjectIDResource objectIDResource = new ObjectIDResource(elementUri, elementId);
        setNameCommandResource.setObjectId(objectIDResource);

        return setNameCommandResource;
    }

    public void setColorForIndicator(String indicatorId, String resilienceAlertColor)
    {
        try {
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

    public List<CustomPropertyDto> getCustomPropertyDto(String decisionModelReportId,
                                                        String decision, Date beginDate,
                                                        Date endDate)
    {
        JacksonJSONContextClient jsonContext = new JacksonJSONContextClient();
        ObjectMapper objectMapper = jsonContext.getObjectMapper();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        df.setTimeZone(tz);
        String beginDateF = df.format(beginDate);
        String endDateF = df.format(endDate);

        try {
            String url = ResiistConstants.APIURL
                         + "/resilience/history/"
                         + projectId
                         + "/"
                         + decisionModelReportId
                         + "/"
                         + decision
                         + "?beginDate="
                         + beginDateF
                         + "&endDate="
                         + endDateF;

            ContentResponse request = httpClient.GET(url);
            String customPropertyDtoAsString = request.getContentAsString();
            CustomPropertyDto[] customPropertyDtosArray =
                    objectMapper.readValue(customPropertyDtoAsString, CustomPropertyDto[].class);
            return Arrays.asList(customPropertyDtosArray);
        }
        catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
            e.printStackTrace();
        }

        return null;
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
                    } else if (ModelUtils.hasSimulationPath(modelElement)
                               || ModelUtils.hasGAMASimulationPath(modelElement))
                    {
                        elementAndWidgetId.put(ResiistConstants.SIMULATION_FILE_PATH,
                                               ModelUtils.getSimulationFilePath(modelElement));
                        elementAndWidgetId.put(ResiistConstants.GAMA_SIMULATION_FILE_PATH,
                                               ModelUtils.getGAMASimulationFilePath(modelElement));
                    }

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
                    && (ModelUtils.hasSimulationPath(modelElement)
                        && !ModelUtils.getSimulationFilePath(modelElement).isEmpty()
                        || ModelUtils.hasGAMASimulationPath(modelElement)
                           && !ModelUtils.getGAMASimulationFilePath(modelElement).isEmpty()))
                {
                    elementIdAndSimulationFilePath.put(ResiistConstants.ELEMENT_NAME,
                                                       ModelUtils.getArchimateName((Referenceable) modelElement));
                    elementIdAndSimulationFilePath.put(ResiistConstants.ELEMENT_IDENTIFIER,
                                                       GMMUtil.getUUID(modelElement));
                    elementIdAndSimulationFilePath.put(ResiistConstants.FREQUENCY,
                                                       ModelUtils.getFrequency(modelElement));
                    elementIdAndSimulationFilePath.put(ResiistConstants.SIMULATION_FILE_PATH,
                                                       ModelUtils.getSimulationFilePath(modelElement));
                    elementIdAndSimulationFilePath.put(ResiistConstants.GAMA_SIMULATION_FILE_PATH,
                                                       ModelUtils.getGAMASimulationFilePath(modelElement));
                    if (ModelUtils.getObjectsToDetect(modelElement) != null
                        && !ModelUtils.getObjectsToDetect(modelElement).isEmpty())
                    {
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

    public List<TextWidget> getDateReportTextWidget(Resource resourceImpl)
    {
        return getAllTextWidgets(resourceImpl).stream()
                                              .filter(this::isDateReportText)
                                              .collect(Collectors.toList());
    }

    public List<TextWidget> getAternativeTotalFlowTextWidget(Resource resourceImpl)
    {
        return getAllTextWidgets(resourceImpl).stream()
                                              .filter(this::isFlowText)
                                              .collect(Collectors.toList());
    }

    private boolean isFlowText(TextWidget textWidget)
    {
        EMap<String, String> valueDetails = ModelUtils.getElementDetails(textWidget);
        String totalFlow = valueDetails.get(ResiistConstants.IS_TOTAL_FLOW);
        return Boolean.valueOf(totalFlow);
    }

    private boolean isDateReportText(TextWidget textWidget)
    {
        EMap<String, String> valueDetails = ModelUtils.getElementDetails(textWidget);
        String totalFlow = valueDetails.get(ResiistConstants.DATE_REPORT);
        return Boolean.valueOf(totalFlow);
    }

    private List<TextWidget> getAllTextWidgets(Resource resourceImpl)
    {
        return GMMUtil.allContentsStream(resourceImpl.getContents().get(0))
                      .filter(e -> e instanceof TextWidget)
                      .map(e -> (TextWidget) e)
                      .collect(Collectors.toList());
    }

    public Resource getModelResource()
    {
        return modelResource;
    }

    public InterpretationRuleGenerator getInterpretationRuleOrchestrator()
    {
        return interpretationRuleOrchestrator;
    }

    public void setInterpretationRuleOrchestrator(InterpretationRuleGenerator interpretationRuleOrchestrator)
    {
        this.interpretationRuleOrchestrator = interpretationRuleOrchestrator;
    }
}