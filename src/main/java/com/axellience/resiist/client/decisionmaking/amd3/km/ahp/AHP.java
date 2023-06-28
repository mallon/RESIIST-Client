package com.axellience.resiist.client.decisionmaking.amd3.km.ahp;

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncAgregation;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncNormalisation;

public class AHP
{

    // Obtenir le vecteur normalise :

    public static double[] vecteurNorm(double[][] tableau)
    {
        double[][] tableaunorm = FoncNormalisation.sumNormalization(tableau);
        double[] vecteur = FoncNormalisation.sumCol(tableaunorm);
        for (int i = 0; i < vecteur.length; i++) {
            vecteur[i] = vecteur[i] / vecteur.length;
        }
        return vecteur;
    }

    // Prend en entre le tableau initial et le vecteur propre normalis.

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
        Tables IA = new Tables();
        double RC = IC / IA.getIA(n);
        if (RC <= 0.10 && RC >= 0) {
            resultat = "True";

        } else {
            resultat = "False";
        }
        return resultat;
    }

    public static double[][] tableauFinal(double[] criteria, double[]... decision)
    {
        // decision[0] = premier vecteur de decision entre
        int nbdec = decision[0].length;
        double[][] tableauFinal = new double[nbdec + 1][criteria.length]; // decision.legth
                                                                          // donne
                                                                          // le
                                                                          // nombre
                                                                          // de
                                                                          // decision
                                                                          // ,
                                                                          // +1
                                                                          // car
                                                                          // il
                                                                          // y
                                                                          // aura
                                                                          // la
                                                                          // ligne
                                                                          // des
                                                                          // poids

        for (int j = 0; j < criteria.length; j++) {
            for (int i = 0; i < (nbdec + 1); i++) {
                if (i == 0) {
                    tableauFinal[i][j] = criteria[j];
                } else {
                    tableauFinal[i][j] = decision[j][i - 1];
                }
            }
        }
        return tableauFinal;

    }

    public static double[] scores(double[][] tableaufinal)
    {
        double[] res = FoncAgregation.sumPond(tableaufinal);
        return res;

    }

}
