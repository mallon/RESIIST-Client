package com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction;

public class CalcInput
{

    public static double calc(String input)
    {
        // TEST OK
        String[] inter;
        double result = 0;
        double num = 0;
        double den = 0;
        if (input.length() > 2) {
            inter = input.split("/");
            num = Double.parseDouble(inter[0]);
            den = Double.parseDouble(inter[1]);
            result = num / den;

        } else {
            result = Double.parseDouble(input);
        }
        return result;
    }

    public static String fuzzycalc(String input)
    {
        // TEST

        String[] inter;
        String resultat = "";
        double result = 0;
        inter = input.split(",");
        for (int i = 0; i < 3; i++) {

            double num = 0;
            double den = 0;
            String[] intermed;
            if (inter[i].length() > 2) {
                intermed = inter[i].split("/");
                num = Double.parseDouble(intermed[0]);
                den = Double.parseDouble(intermed[1]);
                result = num / den;

                if (i != 2) {
                    resultat = resultat + result + ",";
                } else {
                    resultat = resultat + result;
                }
            } else {
                if (i != 2) {
                    resultat = resultat + inter[i] + ",";
                } else {
                    resultat = resultat + inter[i];
                }
            }

        }
        return resultat;

    }

}
