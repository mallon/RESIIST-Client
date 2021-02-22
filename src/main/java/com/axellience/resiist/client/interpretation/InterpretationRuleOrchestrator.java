package com.axellience.resiist.client.interpretation;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

import com.axellience.resiist.client.utils.ModelUtils;
import com.axellience.resiist.client.utils.ResiistConstants;
import com.genmymodel.api.dto.indicator.IndicatorDto;
import com.genmymodel.ecoreonline.graphic.GMMUtil;

public class InterpretationRuleOrchestrator
{
    private static InterpretationRuleOrchestrator instance               = null;
    private static String                         interpretationRulePath = null;
    private static final Logger                   LOGGER                 =
            Logger.getLogger(InterpretationRuleOrchestrator.class.getName());

    private Map<String, List<String>> criteriasAndIndicators = new HashMap<>();
    private List<Map<String, String>> interpretedIndicators  = new ArrayList<>();
    private Map<String, String>       historic;
    private List<Map<String, Object>> allElementWidgetIDAndDTOs;

    protected InterpretationRuleOrchestrator()
    {
        historic = new HashMap<>();

        ClassLoader classLoader = getClass().getClassLoader();

        URL interpretationResource = classLoader.getResource("generatedClasses");
        // /home/mallon/projects/resiist-client/target/classes/generatedClasses
        interpretationRulePath = interpretationResource.getPath();
    }

    public static InterpretationRuleOrchestrator getInstance()
    {
        if (instance == null)
            instance = new InterpretationRuleOrchestrator();

        return instance;
    }

    public void interpretationConfiguration(ResourceImpl modelResource)
    {
        TreeIterator<EObject> iterator = modelResource.getAllContents();
        while (iterator.hasNext()) {
            EObject nextEObject = iterator.next();

            if (nextEObject instanceof EModelElement) {
                EModelElement modelElement = (EModelElement) nextEObject;

                if (ModelUtils.hasFrequency(modelElement)) {
                    Map<String, String> elementIdAndFrequency = new HashMap<>();

                    String elementId = GMMUtil.getUUID(modelElement);
                    EObject eObject = modelResource.getEObject(elementId);
                    Optional<EAnnotation> gmmAnnotation = GMMUtil.getGmmAnnotation(eObject);
                    EMap<String, String> details = gmmAnnotation.get().getDetails();

                    elementIdAndFrequency.put(ResiistConstants.ELEMENT_IDENTIFIER, elementId);
                    ModelUtils.getName(modelElement)
                              .ifPresent(name -> elementIdAndFrequency.put(ResiistConstants.ELEMENT_NAME,
                                                                           name));
                    if (ModelUtils.hasInterpretationRules(modelElement)) {
                        elementIdAndFrequency.put(ResiistConstants.PSEUDO_CODE_INTERPRETATION_RULES,
                                                  details.get(ResiistConstants.PSEUDO_CODE_INTERPRETATION_RULES));
                    }
                    elementIdAndFrequency.put(ResiistConstants.FREQUENCY,
                                              details.get(ResiistConstants.FREQUENCY));

                    interpretedIndicators.add(elementIdAndFrequency);
                }
            }
        }
    }

    public List<Map<String, String>> getInterpretedIndicators()
    {
        return interpretedIndicators;
    }

    public Integer getFrequency(String elementId)
    {
        String freq =
                interpretedIndicators.stream()
                                     .filter(conf -> conf.get(ResiistConstants.ELEMENT_IDENTIFIER)
                                                         .equals(elementId))
                                     .map(conf -> conf.get(ResiistConstants.FREQUENCY))
                                     .findFirst()
                                     .orElse(null);
        if (freq != null) {
            return Integer.valueOf(freq);
        }
        return 0;
    }

    public String getInterpretedValue(String indicatorId, String value)
    {
        String rules = getInterpretationRules(indicatorId);
        if (rules != null) {
            generateAllRules();
            value = executeRule(indicatorId);
        }

        System.out.println("getInterpretedValue : " + value);

        return value;
    }

    private String executeRule(String indicatorId)
    {
        String executionValue = "";
        String indicatorName =
                interpretedIndicators.stream()
                                     .filter(interIndic -> indicatorId.equals(interIndic.get(ResiistConstants.ELEMENT_IDENTIFIER)))
                                     .map(interIndic -> interIndic.get(ResiistConstants.ELEMENT_NAME))
                                     .findFirst()
                                     .orElse(null);
        if (indicatorName != null) {
            try {
                ProcessBuilder compilationProcessBuilder = new ProcessBuilder();
                // /home/mallon/projects/resiist-client/target/classes/generatedClasses
                compilationProcessBuilder.command("javac",
                                                  "-source",
                                                  "1.8",
                                                  "-target",
                                                  "1.8",
                                                  interpretationRulePath
                                                         + "/InterpretationRules.java");
                Process compilationProcess = compilationProcessBuilder.start();

                BufferedReader readerError =
                        new BufferedReader(new InputStreamReader(compilationProcess.getErrorStream()));
                String lineError;
                while ((lineError = readerError.readLine()) != null) {
                    LOGGER.log(Level.SEVERE, lineError);
                }

                ProcessBuilder executionProcessBuilder = new ProcessBuilder();
                // /home/mallon/projects/resiist-client/target/classes/generatedClasses
                executionProcessBuilder.command("java",
                                                "-cp",
                                                interpretationRulePath,
                                                "InterpretationRules",
                                                "executeRule_" + indicatorName);
                Process executionProcess = executionProcessBuilder.start();

                BufferedReader executionreader =
                        new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
                String executionLine;
                while ((executionLine = executionreader.readLine()) != null) {
                    executionValue += executionLine;
                }

                BufferedReader executionReaderError =
                        new BufferedReader(new InputStreamReader(executionProcess.getErrorStream()));
                String executionlineError;
                while ((executionlineError = executionReaderError.readLine()) != null) {
                    LOGGER.log(Level.SEVERE, executionlineError);
                }
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return executionValue;
    }

    private void generateAllRules()
    {
        try (FileWriter fileWriter =
                new FileWriter(interpretationRulePath + "/InterpretationRules.java"))
        {
            fileWriter.write("import java.lang.reflect.InvocationTargetException;\n"
                             + "public class InterpretationRules\n"
                             + "{\n");

            historic.entrySet()
                    .stream()
                    .forEach(entry -> appendIndicatorOldValue(fileWriter, entry));

            fileWriter.append("public static void main(String[] args)\n"
                              + "    {\n"
                              + "        InterpretationRules interpretationRules = new InterpretationRules();\n"
                              + "        interpretationRules.launchMethod(args[0]);\n"
                              + "    }\n"
                              + "\n"
                              + "    private void launchMethod(String methodName)\n"
                              + "    {\n"
                              + "        try {\n"
                              + "            String value = String.valueOf(this.getClass().getMethod(methodName).invoke(this));\n"
                              + "            System.out.println(value);\n"
                              + "        }\n"
                              + "        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException\n"
                              + "                | NoSuchMethodException | SecurityException e)\n"
                              + "        {\n"
                              + "            e.printStackTrace();\n"
                              + "        }\n"
                              + "    }\n\n");

            interpretedIndicators.stream()
                                 .forEach(stringMap -> appendMethod(fileWriter, stringMap));

            fileWriter.append("}");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendIndicatorOldValue(FileWriter fileWriter, Entry<String, String> entry)
    {
        String cleanedValue = entry.getValue().replace("\r", "").replace("\n", "");
        String floatableValue = cleanedValue.isEmpty() ? "0f" : cleanedValue;
        try {
            fileWriter.append("Float "
                              + entry.getKey()
                              + "="
                              + Float.valueOf(floatableValue)
                              + "f"
                              + ";\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendMethod(FileWriter fileWriter, Map<String, String> stringMap)
    {
        try {
            String indicatorName = stringMap.get(ResiistConstants.ELEMENT_NAME);
            fileWriter.append("public Object executeRule_" + indicatorName + "()\n");
            fileWriter.append("{\n");
            fileWriter.append(stringMap.get(ResiistConstants.PSEUDO_CODE_INTERPRETATION_RULES)
                              + "\n");
            fileWriter.append("}\n\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getInterpretationRules(String indicatorId)
    {
        return interpretedIndicators.stream()
                                    .filter(conf -> conf.get(ResiistConstants.ELEMENT_IDENTIFIER)
                                                        .equals(indicatorId))
                                    .filter(conf -> conf.get(ResiistConstants.PSEUDO_CODE_INTERPRETATION_RULES) != null)
                                    .map(conf -> conf.get(ResiistConstants.PSEUDO_CODE_INTERPRETATION_RULES))
                                    .findFirst()
                                    .orElse(null);
    }

    public IndicatorDto toIndicatorDTO(String elementId, String indicatorName)
    {
        IndicatorDto indicatorDto = new IndicatorDto();
        indicatorDto.setElementId(elementId);
        indicatorDto.setKey(indicatorName);

        return indicatorDto;
    }

    public void setIndicatorForCriteria(String criteriaHeader, String indicatorHeader)
    {
        if (criteriasAndIndicators.get(criteriaHeader) == null) {
            criteriasAndIndicators.put(criteriaHeader, new ArrayList<String>());
        }
        criteriasAndIndicators.get(criteriaHeader).add(indicatorHeader);
    }

    public Map<String, List<String>> getCriteriasAndIndicators()
    {
        return criteriasAndIndicators;
    }

    public String getHistoricValue(String elementId)
    {
        String name =
                allElementWidgetIDAndDTOs.stream()
                                         .filter(conf -> conf.get(ResiistConstants.ELEMENT_IDENTIFIER)
                                                             .equals(elementId))
                                         .map(conf -> (String) conf.get(ResiistConstants.ELEMENT_NAME))
                                         .findFirst()
                                         .orElse("");
        return historic.get(name);
    }

    public void addToHistoricInterpretation(String elementId, String simulatedValue)
    {
        allElementWidgetIDAndDTOs.stream()
                                 .filter(conf -> conf.get(ResiistConstants.ELEMENT_IDENTIFIER)
                                                     .equals(elementId))
                                 .map(conf -> conf.get(ResiistConstants.ELEMENT_NAME))
                                 .findFirst()
                                 .ifPresent(name -> historic.put((String) name, simulatedValue));
    }

    public void setNotInterpretedIndicators(List<Map<String, Object>> allElementWidgetIDAndDTOs)
    {
        this.allElementWidgetIDAndDTOs = allElementWidgetIDAndDTOs;
    }
}