package com.axellience.resiist.client.decisionmaking.amd3.km.topsis;

import com.axellience.resiist.client.decisionmaking.amd3.km.fuzzy.Fuzzification;

public class FuzzyAhpTopsis
{

    final static double[] IA =
            {0, 0, 0.58, 0.90, 1.12, 1.24, 1.32, 1.41, 1.45, 1.49, 1.51, 1.48, 1.56, 1.57, 1.59};

    public static double getIA(int N)
    {
        return IA[N - 1];
    }

    public static double[] FuzzCalcWeights(double[][][] tablefuzzy)
    {
        // TEST OK

        double[] resultat = new double[tablefuzzy.length];

        // on rempli la matrice par symtrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(tablefuzzy);
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);
        resultat = vecteurNormal;

        return resultat;
    }

    public static String verifCoherenc(double[][] tableau, double[] vecteurNorm)
    {
        int n = vecteurNorm.length;
        String resultat;
        double[] lambda = new double[vecteurNorm.length];
        double somme = 0;
        for (int i = 0; i < tableau.length; i++) { 			// On fait le calcul
                                                   			// ligne par ligne
                                                   			// du tableau
            for (int j = 0; j < n; j++) {
                lambda[i] += tableau[i][j] * vecteurNorm[j];
            }
            lambda[i] = lambda[i] / vecteurNorm[i];
            somme += lambda[i];
        }
        double lambdaMax = somme / (double) n;
        double IC = (lambdaMax - n) / (n - 1);
        double RC = IC / getIA(n);
        if (RC <= 0.10 && RC >= 0) {
            resultat = "True";

        } else {
            resultat = "False";
        }
        return resultat;
    }

    public static double getRC(double[][] tableau, double[] vecteurNorm)
    {
        int n = vecteurNorm.length;

        double[] lambda = new double[vecteurNorm.length];
        double somme = 0;
        for (int i = 0; i < tableau.length; i++) { 			// On fait le calcul
                                                   			// ligne par ligne
                                                   			// du tableau
            for (int j = 0; j < n; j++) {
                lambda[i] += tableau[i][j] * vecteurNorm[j];
            }
            lambda[i] = lambda[i] / vecteurNorm[i];
            somme += lambda[i];
        }
        double lambdaMax = somme / (double) n;
        double IC = (lambdaMax - n) / (n - 1);
        double RC = IC / getIA(n);

        return RC;

    }

    public static double[][] FuzAhptabUn(double[][][] tablefuzzy)
    {
        // Creation du tableau[][]
        // TEST OK
        double[][] tablesUn = new double[tablefuzzy.length][tablefuzzy[0].length];

        for (int i = 0; i < tablefuzzy[0].length; i++) {
            for (int j = 0; j < tablefuzzy[0].length; j++) {
                tablesUn[i][j] =
                        (tablefuzzy[i][j][0] + 2 * tablefuzzy[i][j][1] + tablefuzzy[i][j][2]) / 4;
            }
        }

        return tablesUn;

    }

    public static double[] ConsistentWeights(double[] listRC, double[][] listofWeights)
    {
        double[] resultat = new double[listofWeights[0].length];
        double[] listP = new double[listRC.length];
        double[] listWp = new double[listRC.length];
        double somme = 0;
        for (int i = 0; i < listRC.length; i++) {
            listP[i] = 1 / (1 + 10 * listRC[i]);
            somme += listP[i];
        }
        for (int i = 0; i < listRC.length; i++) {
            listWp[i] = listP[i] / somme;

        }

        for (int j = 0; j < listofWeights[0].length; j++) {
            for (int i = 0; i < listRC.length; i++) {
                resultat[j] += listWp[i] * listofWeights[i][j];
            }

        }
        return resultat;
    }
}
