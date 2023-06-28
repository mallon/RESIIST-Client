package com.axellience.resiist.client.decisionmaking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.opengroup.archimate.CourseOfAction;
import org.opengroup.archimate.Element;
import org.opengroup.archimate.Resource;
import org.opengroup.archimate.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.axellience.resiist.client.acquisitiontointegration.integration.IntegrationPlatform;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncAgregation;
import com.axellience.resiist.client.decisionmaking.amd3.km.csvhandler.csvHandler;
import com.axellience.resiist.client.decisionmaking.amd3.km.promethee.PromMethod;
import com.axellience.resiist.client.decisionmaking.amd3.struct.Amd3Application;
import com.axellience.resiist.client.utils.CsvUtil;
import com.axellience.resiist.client.utils.ModelUtils;
import com.axellience.resiist.client.utils.ResiistConstants;
import com.axellience.resiist.client.utils.gui.BrowserUtil;
import com.genmymodel.ecoreonline.graphic.GMMUtil;

public class DecisionMaking
{
    private IntegrationPlatform integrationPlatform;

    private ResourceImpl modelResource;
    private CsvUtil      csvUtil = new CsvUtil();

    private FoncAgregation aggregaFunction = new FoncAgregation();

    private ConfigurableApplicationContext applicationContext;

    private List<CourseOfAction> alternatives;

    private Map<String, Map<String, String>> alternativeAndValues = new HashMap<>();

    private List<Value> performances       = new ArrayList<>();
    private List<Value> reportPerformances = new ArrayList<>();

    private int    nombrecrit = 0;
    private String decisionList;

    private double[]   listS;
    private double[]   listIndSeuil;
    private double[]   listPrefSeuil;
    private String     minOrMaxs;
    private String     weights;
    private String     prefFunctions;
    private String     prefThresholds;
    private String     indiffThresholds;
    private double[][] matrice;

    public DecisionMaking()
    {
    }

    public DecisionMaking(IntegrationPlatform integrationPlatform)
    {
        this.integrationPlatform = integrationPlatform;
    }

    public File transformDecisionModelToCSV()
    {
        List<Resource> criterions = getCriterions();
        alternativeAndValues = getAlternativeAndPerfs();
        Map<String, String> weightValuesAndCriterions = new HashMap<>();
        Map<String, String> minOrMaxValueAndCriterions = new HashMap<>();
        Map<String, String> preferenceFunctionValuesAndCriterions = new HashMap<>();
        Map<String, String> preferenceThresholdValuesAndCriterions = new HashMap<>();
        Map<String, String> indifferenceThresholdValuesAndCriterions = new HashMap<>();
        for (Resource criterion : criterions) {
            EMap<String, String> criterionDetails = ModelUtils.getElementDetails(criterion);

            String weight = toUSDecimal(criterionDetails.get(ResiistConstants.WEIGHT));
            String archimateName = ModelUtils.getArchimateName(criterion);
            weightValuesAndCriterions.put(archimateName, weight);

            String minOrMax = criterionDetails.get(ResiistConstants.MIN_OR_MAX);
            String convert = convertMinMaxOrPrefFunctionToValue(minOrMax);
            minOrMaxValueAndCriterions.put(archimateName, convert);

            String preferenceFunction = criterionDetails.get(ResiistConstants.PREFERENCE_FUNCTION);

            preferenceFunctionValuesAndCriterions.put(archimateName,
                                                      convertMinMaxOrPrefFunctionToValue(preferenceFunction));

            String threshold = criterionDetails.get(ResiistConstants.THRESHOLDS);
            setThresholds(criterion,
                          threshold,
                          preferenceFunction,
                          indifferenceThresholdValuesAndCriterions,
                          preferenceThresholdValuesAndCriterions);

        }
        alternativeAndValues.put("Min or max", minOrMaxValueAndCriterions);
        alternativeAndValues.put("Weight", weightValuesAndCriterions);
        alternativeAndValues.put("Preference function", preferenceFunctionValuesAndCriterions);
        alternativeAndValues.put("Preference threshold", preferenceThresholdValuesAndCriterions);
        alternativeAndValues.put("Indifference threshold",
                                 indifferenceThresholdValuesAndCriterions);

        return csvUtil.createCSVFile("decisionModel.csv", ";", alternativeAndValues);
    }

    private String convertMinMaxOrPrefFunctionToValue(String minMaxOrPrefFunctionName)
    {
        if ("Min".equals(minMaxOrPrefFunctionName) || "Usual".equals(minMaxOrPrefFunctionName)) {
            return "0";
        } else if ("Max".equals(minMaxOrPrefFunctionName)
                   || "Level".equals(minMaxOrPrefFunctionName))
        {
            return "1";
        }
        if ("V-Shape".equals(minMaxOrPrefFunctionName)) {
            return "2";
        } else if ("U-Shape".equals(minMaxOrPrefFunctionName)) {
            return "3";
        }

        return "-1";
    }

    private void setThresholds(Resource criterion, String threshold, String preferenceFunction,
                               Map<String, String> indifferenceThresholdValuesAndCriterions,
                               Map<String, String> prefereceThresholdValuesAndCriterions)
    {
        String archimateName = ModelUtils.getArchimateName(criterion);
        if (threshold != null && !threshold.isEmpty() && !threshold.isBlank()) {
            String[] thresholds = getPreferenceAndIndifferenceThresholds(threshold);
            if (ResiistConstants.V_SHAPE_PREFERENCE_FUNCTION.equals(preferenceFunction)) {
                prefereceThresholdValuesAndCriterions.put(archimateName, thresholds[0]);
                indifferenceThresholdValuesAndCriterions.put(archimateName, "0");
            } else if (ResiistConstants.U_SHAPE_PREFERENCE_FUNCTION.equals(preferenceFunction)) {
                indifferenceThresholdValuesAndCriterions.put(archimateName, thresholds[0]);
                prefereceThresholdValuesAndCriterions.put(archimateName, "0");
            } else if (thresholds.length == 2) {
                indifferenceThresholdValuesAndCriterions.put(archimateName, thresholds[0]);
                prefereceThresholdValuesAndCriterions.put(archimateName, thresholds[1]);
            }
        } else {
            indifferenceThresholdValuesAndCriterions.put(archimateName, "0");
            prefereceThresholdValuesAndCriterions.put(archimateName, "0");
        }
    }

    private String[] getPreferenceAndIndifferenceThresholds(String threshold)
    {
        Pattern pattern = Pattern.compile("\\([0-9],[0-9]*\\)*");
        Matcher matcher = pattern.matcher(threshold);
        String[] thresholds = new String[2];

        boolean found = matcher.find();
        if (!found) {
            thresholds = threshold.split(",");
        } else {
            thresholds[0] = removeParenthesis(toUSDecimal(matcher.group()));
            int i = 1;
            while (matcher.find()) {
                thresholds[i] = removeParenthesis(toUSDecimal(matcher.group()));
                i++;
            }
        }

        return thresholds;

    }

    private String toUSDecimal(String withComma)
    {
        return withComma.replace(",", ".");
    }

    private String removeParenthesis(String withParenthesis)
    {
        return withParenthesis.replace("(", "").replace(")", "");
    }

    private Map<String, Map<String, String>> getAlternativeAndPerfs()
    {
        alternatives = getAlternatives();

        for (CourseOfAction alternative : alternatives) {
            if (isNotInReport(alternative)) {
                String alternativeName = ModelUtils.getArchimateName(alternative);
                if (alternativeName != null) {
                    Map<String, String> perfoNamesAndCriterions =
                            getCriterionAndPerfoNames(alternative, performances);
                    alternativeAndValues.put(alternativeName, perfoNamesAndCriterions);
                }
            }
        }

        return alternativeAndValues;
    }

    private List<CourseOfAction> getAlternatives()
    {
        return getAllElements(CourseOfAction.class).stream()
                                                   .filter(this::isNotInReport)
                                                   .collect(Collectors.toList());
    }

    public List<CourseOfAction> getReportAlternatives()
    {
        return getAllElements(CourseOfAction.class).stream()
                                                   .filter(a -> !isNotInReport(a))
                                                   .collect(Collectors.toList());
    }

    private boolean isNotInReport(Element element)
    {
        EMap<String, String> criterionDetails = ModelUtils.getElementDetails(element);
        String report = criterionDetails.get(ResiistConstants.REPORT);
        return !"true".equals(report);
    }

    private Map<String, String> getCriterionAndPerfoNames(CourseOfAction alternative,
                                                          List<Value> performances)
    {
        Map<String, String> perfoNamesAndCriterions = new HashMap<>();
        EMap<String, String> alternativeDetails = ModelUtils.getElementDetails(alternative);
        for (Entry<String, String> entry : alternativeDetails) {
            Value foundValue =
                    performances.stream()
                                .filter(perf -> GMMUtil.getUUID(perf).equals(entry.getValue()))
                                .findFirst()
                                .orElse(null);
            String valueName = ModelUtils.getArchimateName(foundValue);

            if (valueName != null) {
                EMap<String, String> valueDetails = ModelUtils.getElementDetails(foundValue);
                String criterionName = valueDetails.get(ResiistConstants.DECISION_CRITERION);
                perfoNamesAndCriterions.put(criterionName, valueName);
            }
        }

        if (getCriterions().size() > perfoNamesAndCriterions.size()) {
            Set<String> alterCriterions = perfoNamesAndCriterions.keySet();
            for (Resource criterion : getCriterions()) {
                String criterionName = ModelUtils.getArchimateName(criterion);
                if (!alterCriterions.contains(criterionName)) {
                    perfoNamesAndCriterions.put(criterionName, "0");
                }
            }
        }

        return perfoNamesAndCriterions;
    }

    public void setIndicatorValueInCorrespondingPerformance(String indicatorId, String value)
    {
        String indicatorName = getIndicatorName(indicatorId);

        for (Value perf : performances) {
            EMap<String, String> valueDetails = ModelUtils.getElementDetails(perf);
            String criterionPerf = valueDetails.get(ResiistConstants.DECISION_CRITERION);
            if (criterionPerf != null && criterionPerf.equals(indicatorName)) {
                String influenceFonction = valueDetails.get(ResiistConstants.INFLUENCE_FUNCTION);
                String influenceParameter = valueDetails.get(ResiistConstants.INFLUENCE_PARAMETER);
                String doubleAsUS = value.replace(",", ".");
                Double influencedIndicatorValue =
                        getInfluencedIndicatorValue(Double.valueOf(doubleAsUS),
                                                    influenceFonction,
                                                    influenceParameter);

                integrationPlatform.setValueForPerf(perf, value, influencedIndicatorValue);
                // Update local performances
                perf.getName().get(0).setValue(String.valueOf(influencedIndicatorValue));
            }
        }
    }

    private String getIndicatorName(String indicatorId)
    {
        for (Map<String, Object> elementWidgetIDAndDTO : IntegrationPlatform.getInstance().allSimulatedIndicators) {
            if (elementWidgetIDAndDTO.get(ResiistConstants.ELEMENT_IDENTIFIER)
                                     .equals(indicatorId))
            {
                return (String) elementWidgetIDAndDTO.get(ResiistConstants.ELEMENT_NAME);
            }
        }
        return null;
    }

    private double getInfluencedIndicatorValue(Double currentIndicatorValue,
                                               String influenceFonctionName,
                                               String influenceParameter)
    {
        Double infParamDouble = Double.valueOf(influenceParameter);
        Function<double[], Double> influenceFunction =
                getInfluenceFunctionImplementedName(influenceFonctionName);
        double[] indicatorValueAndFunctionParameter = {currentIndicatorValue, infParamDouble};

        return influenceFunction.apply(indicatorValueAndFunctionParameter);
    }

    private Function<double[], Double> getInfluenceFunctionImplementedName(String influenceFonction)
    {
        return aggregaFunction.getFunctionNameMapping().get(influenceFonction);
    }

    private <T> List<T> getAllElements(Class<T> targetedType)
    {
        if (modelResource != null)
            return GMMUtil.allContentOfType(modelResource.getContents().get(0), targetedType)
                          .collect(Collectors.toList());

        return Collections.emptyList();
    }

    private List<Resource> getCriterions()
    {
        return getAllElements(Resource.class).stream()
                                             .filter(r -> isNotInReport(r))
                                             .collect(Collectors.toList());
    }

    public void openImportUrl()
    {
        if (applicationContext.isRunning() && Amd3Application.isCSVImported) {
            BrowserUtil.openUrlInBrowser("http://localhost:8081/importation");
        }
    }

    public void launchDecisionEvaluationServer()
    {
        applicationContext = SpringApplication.run(Amd3Application.class);
    }

    public void setCsvFile(File csvDecisionFile)
    {
        if (isServerRunning())
            Amd3Application.setCsvFile(csvDecisionFile);
    }

    public List<String[]> sortDecisions()
    {
        Map<String, String> decisionsAndTotalFlow = getDecisionAndTotalFlows();
        List<String[]> orderedDecisions = new ArrayList<>();
        List<Double> valuesAsDouble = new ArrayList<>();
        for (String valuesAsString : decisionsAndTotalFlow.values()) {
            valuesAsDouble.add(Double.valueOf(valuesAsString));
        }
        Collections.sort(valuesAsDouble);

        for (Double valueAsDouble : valuesAsDouble) {
            for (Entry<String, String> entry : decisionsAndTotalFlow.entrySet()) {
                if (valueAsDouble.equals(Double.valueOf(entry.getValue()))
                    && decisionNotAlreadySelected(entry.getKey(), orderedDecisions))
                {
                    String[] dec = {entry.getKey(), entry.getValue()};
                    orderedDecisions.add(dec);
                }
            }
        }

        return orderedDecisions;
    }

    private boolean decisionNotAlreadySelected(String key, List<String[]> orderedDecisions)
    {
        for (String[] dec : orderedDecisions) {
            if (dec[0].equals(key)) {
                return false;
            }
        }
        return true;
    }

    private Map<String, String> getDecisionAndTotalFlows()
    {
        String cheminAbsolu = Amd3Application.storageService.getFileNamespace();
        importCriteriaTable(cheminAbsolu);
        int[] infoCriterProm = minOrMaxImport();
        double[] poidsPromethee = poidsImport();
        int[] numFctPref = prefImport();
        valeursPrefImport();

        double[][][] tabecar = PromMethod.calcTabEcart(matrice);
        double[][][] tabpref = PromMethod.ApplyPref(tabecar,
                                                    infoCriterProm,
                                                    numFctPref,
                                                    listPrefSeuil,
                                                    listIndSeuil,
                                                    listS);
        double[][] tabPi = PromMethod.MatPi(tabpref);
        double[][] tabFlux = PromMethod.CalcFlux(tabPi, poidsPromethee);
        double[] fluxPositif = PromMethod.PosFlux(tabFlux);
        double[] fluxNeg = PromMethod.NegFlux(tabFlux);
        double[] fluxTotal = PromMethod.FluxTot(fluxNeg, fluxPositif);

        Map<String, String> decisionsAndTotalFlow = new HashMap<>();
        String[] decisions = decisionList.split(",");
        for (int i = 0; i < decisions.length; i++) {
            String name = decisions[i];
            decisionsAndTotalFlow.put(name, Double.toString(fluxTotal[i]));
        }

        return decisionsAndTotalFlow;
    }

    private void valeursPrefImport()
    {
        // Notice that we do not use gaussian functions for the moment
        String unusedGaussianThresholds = "0,0,0,0";
        String indiffAndPreffFunctionThresholds =
                indiffThresholds + "," + prefThresholds + "," + unusedGaussianThresholds;
        Map<String, String> indiffAndPreffFunctionThresholdsMap =
                convertStringCommaToMap(indiffAndPreffFunctionThresholds, true);

        listIndSeuil = new double[nombrecrit];
        listPrefSeuil = new double[nombrecrit];
        listS = new double[nombrecrit];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                String value = indiffAndPreffFunctionThresholdsMap.get(cell);
                double valeur = Double.parseDouble(value);
                if (i == 0) {
                    listIndSeuil[j] = valeur;
                }
                if (i == 1) {
                    listPrefSeuil[j] = valeur;
                }
                if (i == 2) {
                    listS[j] = valeur;
                }
            }
        }
    }

    private Map<String, String> convertStringCommaToMap(String stringComma, boolean fromZero)
    {
        String[] array = stringComma.split(",");
        Map<String, String> oneMap = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            String value = array[i];
            String key = fromZero ? String.valueOf(i) : String.valueOf(i + 1);
            oneMap.put(key, value);
        }
        return oneMap;
    }

    private int[] prefImport()
    {
        String[] prefFunctionArray = prefFunctions.split(",");
        Map<String, String> prefFunctionMap = new HashMap<>();
        for (int i = 0; i < prefFunctionArray.length; i++) {
            String prefFunctionValue = prefFunctionArray[i];
            String minOrMaxName = convertValueToPrefFunction(prefFunctionValue);
            prefFunctionMap.put(String.valueOf(i + 1), minOrMaxName);
        }

        int[] numFctPref = new int[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = prefFunctionMap.get(cell);
            if (value.equals("Usual")) {
                numFctPref[i] = 1;
            }
            if (value.equals("U-Shape")) {
                numFctPref[i] = 2;
            }
            if (value.equals("V-Shape")) {
                numFctPref[i] = 3;
            }
            if (value.equals("V-Shape-Ind")) {
                numFctPref[i] = 4;
            }
            if (value.equals("Level")) {
                numFctPref[i] = 5;
            }
            if (value.equals("Gaussian")) {
                numFctPref[i] = 6;
            }
        }

        return numFctPref;
    }

    private String convertValueToPrefFunction(String prefFunctionValue)
    {
        if ("0".equals(prefFunctionValue)) {
            return "Usual";
        } else if ("1".equals(prefFunctionValue)) {
            return "Level";
        }
        if ("2".equals(prefFunctionValue)) {
            return "V-Shape";
        } else if ("3".equals(prefFunctionValue)) {
            return "U-Shape";
        }
        return "";
    }

    private double[] poidsImport()
    {
        Map<String, String> weightsMap = convertStringCommaToMap(weights, false);

        double[] poidsPromethee = new double[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = weightsMap.get(cell);

            poidsPromethee[i] = Double.parseDouble(value);
        }

        return poidsPromethee;
    }

    private int[] minOrMaxImport()
    {
        String[] minOrMaxArray = minOrMaxs.split(",");
        Map<String, String> minOrMaxMap = new HashMap<>();
        for (int i = 0; i < minOrMaxArray.length; i++) {
            String minOrMaxValue = minOrMaxArray[i];
            String minOrMaxName = "0".equals(minOrMaxValue) ? "Min" : "Max";
            minOrMaxMap.put(String.valueOf(i + 1), minOrMaxName);
        }

        int[] infoCriterProm = new int[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = minOrMaxMap.get(cell);
            if (value.equals("Min")) {
                infoCriterProm[i] = 0;
            } else {
                infoCriterProm[i] = 1;
            }
        }

        return infoCriterProm;
    }

    private void importCriteriaTable(String cheminAbsolu)
    {
        try {
            matrice = csvHandler.csvToArray(cheminAbsolu, 1, 1);
            nombrecrit = matrice[0].length;
            decisionList = csvHandler.csvFirstCol(cheminAbsolu);
            minOrMaxs = csvHandler.csvNRow(cheminAbsolu, "Min or max");
            weights = csvHandler.csvNRow(cheminAbsolu, "Weight");
            prefFunctions = csvHandler.csvNRow(cheminAbsolu, "Preference function");
            indiffThresholds = csvHandler.csvNRow(cheminAbsolu, "Indifference threshold");
            prefThresholds = csvHandler.csvNRow(cheminAbsolu, "Preference threshold");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFlowValue(String alterName, List<String[]> orderedDecisions)
    {
        return orderedDecisions.stream()
                               .filter(dec -> alterName.equals(dec[0]))
                               .map(dec -> dec[1])
                               .findFirst()
                               .orElse(null);
    }

    public Map<String, Map<String, String>> getAlternativeAndValues()
    {
        return alternativeAndValues;
    }

    public List<Value> getReportPerformances()
    {
        return reportPerformances;
    }

    public String getDecisionList()
    {
        return decisionList;
    }

    public boolean isServerRunning()
    {
        return applicationContext != null && applicationContext.isRunning();
    }

    public CourseOfAction getBestAlternativeInReport(String alternativeName)
    {
        List<CourseOfAction> reportAlternatives = getReportAlternatives();
        return reportAlternatives.stream()
                                 .filter(ra -> alternativeName.equals(ra.getName()
                                                                        .get(0)
                                                                        .getValue()))
                                 .findFirst()
                                 .orElse(null);
    }

    public void setModelResource(ResourceImpl modelResource)
    {
        this.modelResource = modelResource;
        for (Value value : getAllElements(Value.class)) {
            EMap<String, String> valueDetails = ModelUtils.getElementDetails(value);
            if ((valueDetails.get(ResiistConstants.DECISION_CRITERION) != null
                 && !valueDetails.get(ResiistConstants.DECISION_CRITERION).isEmpty())
                && (valueDetails.get(ResiistConstants.REPORT) == null
                    || valueDetails.get(ResiistConstants.REPORT).isEmpty()))
            {
                performances.add(value);
            } else if (valueDetails.get(ResiistConstants.REPORT) != null
                       && !valueDetails.get(ResiistConstants.REPORT).isEmpty())
                reportPerformances.add(value);
        }

    }

}