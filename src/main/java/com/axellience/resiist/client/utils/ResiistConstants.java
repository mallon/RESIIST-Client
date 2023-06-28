package com.axellience.resiist.client.utils;

import java.util.ArrayList;
import java.util.List;

public class ResiistConstants
{
    public static final String ELEMENT_IDENTIFIER = "Element identifier";
    public static final String WIDGET_IDENTIFIER  = "Widget identifier";

    public static final String FREQUENCY         = "frequency";
    public static final String OBJECTS_TO_DETECT = "objects_to_detect";

    public static final String SIMULATION_FILE_PATH             = "simulation_file_path";
    public static final String PSEUDO_CODE_INTERPRETATION_RULES =
            "pseudo_code_interpretation_rules";
    public static final String SIMULATION_VIDEO_FILE_NAME       = "simulated_video.mp4";

    // public static final String APIURL = "https://app.genmymodel.com/api";
    // public static final String USER_NAME = "matthieu.allon@axellience.com";
    // public static final String PASSWORD = "Easy1245?";

    public static final String APIURL    = "https://localhost:8443/api";
    public static final String USER_NAME = "indicator";
    public static final String PASSWORD  = "password";

    public static final String INDICATOR_DTO = "Indicator DTO";
    public static final String VALUE_HEADER  = "Values";

    public static final Integer EVOLUTION_STEP = 3;
    public static final Integer OUT_OF_LIMIT   = 3;

    public static final String FN         = "fn";
    public static final String FNMAX      = "fnmax";
    public static final String FMAX       = "fmax";
    public static final String FNMIN      = "fnmin";
    public static final String FMIN       = "fmin";
    public static final String RESILIENCE = "resilience";

    public static final String EVOLUTION_FUNCTION_HEADER = "Evolution function";
    public static final String AGG_FCT                   = "aggregation_function";

    public static final String COLOR_NORMAL       = "color_normal";
    public static final String COLOR_ACCEPTABLE   = "color_acceptable";
    public static final String COLOR_UNACCEPTABLE = "color_unacceptable";

    public static final String ELEMENT_NAME = "Element name";

    public static final String  DECISION_CRITERION            = "decision_criterion";
    public static final String  INFLUENCE_FUNCTION            = "influence_function";
    public static final String  INFLUENCE_PARAMETER           = "influence_parameter";
    public static final String  WEIGHT                        = "weight";
    public static final Object  PREFERENCE_FUNCTION           = "preference_function";
    public static final Object  THRESHOLDS                    = "thresholds";
    public static final String  V_SHAPE_PREFERENCE_FUNCTION   = "V-Shape";
    public static final String  U_SHAPE_PREFERENCE_FUNCTION   = "U-Shape";
    public static final String  MIN_OR_MAX                    = "min_or_max";
    public static final String  REPORT                        = "report";
    public static final String  DATE_REPORT                   = "date_report";
    public static final String  IS_TOTAL_FLOW                 = "isTotalFlow";
    public static final String  ALTERNATIVE                   = "alternative";
    public static final String  DECISION_REPORT               = "decisionReport";
    public static final String  DECISIONURL                   = "http://localhost:8081";
    public static final Integer DECISION_EVALUATION_FREQUENCY = 10;
    public static final String  GAMA_SIMULATION_FILE_PATH     = "gama_simulation_file_path";

    private ResiistConstants()
    {
    }

    public enum algoAndObjects {
        ObjectDetection(getObjectsForObjectDetection()),
        behaviorDetection(getObjectsForBehaviorDetection()),
        emotionDetection(getObjectsForEmotionDetection()),
        eventDetection(getObjectsForEventDetection());

        private List<String> objects = new ArrayList<>();

        private algoAndObjects(List<String> objects)
        {
            this.objects = objects;
        }

        public List<String> getObjects()
        {
            return this.objects;
        }

        private static List<String> getObjectsForObjectDetection()
        {
            List<String> objects = new ArrayList<>();
            // TODO (add others by seeing class labels in 'python' part)
            objects.add("person");
            objects.add("vehicules");
            return objects;
        }

        private static List<String> getObjectsForBehaviorDetection()
        {
            List<String> objects = new ArrayList<>();
            // TODO (add others by seeing class labels in 'python' part)
            objects.add("extinguishing fire");
            objects.add("washing hands");
            objects.add("shaking hands");
            objects.add("hugging");
            return objects;
        }

        private static List<String> getObjectsForEmotionDetection()
        {
            List<String> objects = new ArrayList<>();
            // TODO (see class labels in 'python' part)
            return objects;
        }

        private static List<String> getObjectsForEventDetection()
        {
            List<String> objects = new ArrayList<>();
            // TODO (add others by seeing class labels in 'python' part)
            objects.add("Wildfire");
            return objects;
        }
    }

}
