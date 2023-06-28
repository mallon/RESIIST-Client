package com.axellience.resiist.client.decisionmaking.amd3.km.anp;

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncNormalisation;

public class ANP
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

    // V�rifier la coh�rence :

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
        ANPTables IA = new ANPTables();
        double RC = IC / IA.getIA(n);
        if (RC <= 0.10 && RC >= 0) {
            resultat = "True";

        } else {
            resultat = "False";
        }
        return resultat;
    }

    // Creation de la Super Matrix (TEST OK)
    // vectNorm de la forme : (nbcrit x vectNorm Criter->Decisions) + (nbdec x
    // vectNorm Decision-> Criter)

    public static double[][] superMatrix(double[] criteria, int nbcrit, int nbdec,
                                         double[]... vectNorm)
    {
        double[][] tableaufinal = new double[nbcrit + nbdec + 1][nbcrit + nbdec + 1];
        for (int i = 0; i < nbcrit; i++) {			// On rempli la premi�re
                                          			// colonne avec le vecteur
                                          			// ponderation des criteres
            tableaufinal[i + 1][0] = criteria[i];
        }
        for (int k = 0; k < nbcrit; k++) {						// On rempli les
                                          						// nbcrit
                                          						// colonnes
                                          						// suivantes
                                          						// avec le
                                          						// vecteur de
                                          						// pond�ration
                                          						// des d�cisions
                                          						// pour chaque
                                          						// crit�re
            for (int j = 0; j < nbdec; j++) {
                tableaufinal[nbcrit + 1 + j][k + 1] = vectNorm[k][j];

            }
        }
        for (int l = 0; l < nbdec; l++) {						//// On rempli
                                         						//// les nbdec
                                         						//// colonnes
                                         						//// suivantes
                                         						//// avec le
                                         						//// vecteur de
                                         						//// pond�ration
                                         						//// des
                                         						//// crit�res
                                         						//// pour chaque
                                         						//// d�cision
            for (int m = 0; m < nbcrit; m++) {
                tableaufinal[m + 1][nbcrit + 1 + l] = vectNorm[nbcrit + l][m];
            }
        }

        return tableaufinal;

    }

    // Passer une matrice � la puissance. (TEST OK)
    public static double[][] multipMatrix(double[][] A, double[][] B)
    {
        double[][] resultat = new double[A.length][B[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {

                for (int k = 0; k < B.length; k++) {
                    resultat[i][j] += A[i][k] * B[k][j];
                }

            }
        }
        return resultat;
    }

    // Matrice identite (TEST OK )
    public static double[][] identity(int size)
    {
        double[][] id = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    id[i][j] = 1;
                }
            }
        }
        return id;
    }

    // Matrice � la puissance. (TEST OK )
    public static double[][] powerMatrix(double[][] A, int powerRaised)
    {
        if (powerRaised != 0) {
            return multipMatrix(A, powerMatrix(A, powerRaised - 1));
        } else {
            return identity(A.length);
        }
    }

}
