package com.axellience.resiist.client.decisionmaking.amd3.km.topsis;

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncNormalisation;

public class Topsis
{

    public static double[] maxcol(double[][] tableau)
    {
        double[] resultat = new double[tableau[0].length];
        for (int j = 0; j < tableau[0].length; j++) {
            resultat[j] = tableau[0][j];
            for (int i = 0; i < tableau.length; i++) {
                if (tableau[i][j] > resultat[j]) {
                    resultat[j] = tableau[i][j];
                }
            }
        }
        return resultat;

    }

    public static double[] mincol(double[][] tableau)
    {
        double[] resultat = new double[tableau[0].length];
        for (int j = 0; j < tableau[0].length; j++) {
            resultat[j] = tableau[0][j];
            for (int i = 0; i < tableau.length; i++) {
                if (tableau[i][j] < resultat[j]) {
                    resultat[j] = tableau[i][j];
                }
            }
        }
        return resultat;

    }

    public static double[] topmethod(double[][] tableau, double[] poids, double[] infocriter)
    {
        // Le vecteur infocriter indique si on doit minimiser (0) ou maximiser
        // (1) le critere.
        // Etape 1 : matrice -> Matrice normalisee
        // TEST OK
        double[][] normalised = FoncNormalisation.vectorNormalization(tableau);

        // Etape 2 : matrice normalisee * poids
        // TEST OK
        double[][] mat2 = new double[tableau.length][tableau[0].length];
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[0].length; j++) {
                mat2[i][j] = normalised[i][j] * poids[j];
            }
        }
        // Etape 3 : 2 vecteurs best and worst value (chaque colonne)
        // TEST OK
        double[] bestvalue = new double[tableau[0].length];
        double[] worstvalue = new double[tableau[0].length];

        for (int j = 0; j < tableau[0].length; j++) {
            for (int i = 0; i < tableau.length; i++) {
                if (infocriter[j] == 0) {
                    bestvalue[j] = mincol(mat2)[j];
                    worstvalue[j] = maxcol(mat2)[j];
                } else {
                    bestvalue[j] = maxcol(mat2)[j];
                    worstvalue[j] = mincol(mat2)[j];
                }
            }
        }
        // Etape 4 : Calcul distance euclidienne entre best et worst value.
        // TEST OK
        double[][] eucli = new double[tableau.length][2];
        for (int i = 0; i < tableau.length; i++) {
            double sommeplus = 0;
            double sommemoins = 0;

            for (int j = 0; j < tableau[0].length; j++) {
                sommeplus += Math.pow((mat2[i][j] - bestvalue[j]), 2);
                sommemoins += Math.pow((mat2[i][j] - worstvalue[j]), 2);
            }
            eucli[i][0] = Math.pow(sommeplus, 0.5);
            eucli[i][1] = Math.pow(sommemoins, 0.5);
        }
        // Etape 5 : Calcul de Pi
        // TEST OK

        double[] pi = new double[tableau.length];
        for (int i = 0; i < tableau.length; i++) {
            pi[i] = eucli[i][1] / (eucli[i][0] + eucli[i][1]);
        }

        return pi;
    }

}
