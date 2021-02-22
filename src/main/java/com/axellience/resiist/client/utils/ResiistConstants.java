package com.axellience.resiist.client.utils;

import java.util.ArrayList;
import java.util.List;

public class ResiistConstants
{
    public static final String  ELEMENT_IDENTIFIER               = "Element identifier";
    public static final String  WIDGET_IDENTIFIER                = "Widget identifier";
    public static final String  FREQUENCY                        = "frequency";
    public static final String  OBJECTS_TO_DETECT                = "objects_to_detect";
    public static final String  SIMULATION_FILE_PATH             = "simulation_file_path";
    public static final String  PSEUDO_CODE_INTERPRETATION_RULES =
            "pseudo_code_interpretation_rules";
    public static final String  SIMULATION_VIDEO_FILE_NAME       = "simulated_video.mp4";
    public static final String  APIURL                           = "https://localhost:8443/api";
    public static final String  USER_NAME                        = "indicator";
    public static final String  PASSWORD                         = "password";
    public static final String  EVOLUTION_FUNCTION_HEADER        = "Evolution function";
    public static final String  INDICATOR_DTO                    = "Indicator DTO";
    public static final String  VALUE_HEADER                     = "Values";
    public static final Integer EVOLUTION_STEP                   = 3;
    public static final Integer OUT_OF_LIMIT                     = 3;
    public static final String  FN                               = "fn";
    public static final String  FNMAX                            = "fnmax";
    public static final String  FMAX                             = "fmax";
    public static final String  FNMIN                            = "fnmin";
    public static final String  FMIN                             = "fmin";
    public static final String  RESILIENCE                       = "resilience";
    public static final String  COLOR_NORMAL                     = "color_normal";
    public static final String  COLOR_ACCEPTABLE                 = "color_acceptable";
    public static final String  COLOR_UNACCEPTABLE               = "color_unacceptable";
    public static final String  ELEMENT_NAME                     = "Element name";

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
