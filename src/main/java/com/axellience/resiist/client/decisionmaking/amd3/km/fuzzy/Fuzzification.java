package com.axellience.resiist.client.decisionmaking.amd3.km.fuzzy;

public class Fuzzification
{

    final static double[][] nb       = {{1, 1, 1}, {1, 2, 3}, {2, 3, 4}, {3, 4, 5}, {4, 5, 6},
                                        {5, 6, 7}, {6, 7, 8}, {7, 8, 9}, {9, 9, 9}};
    final static double[][] nbInvers =
            {{1, 1, 1}, {0.333, 0.5, 1}, {0.25, 0.333, 0.5}, {0.2, 0.25, 0.333}, {0.167, 0.2, 0.25},
             {0.143, 0.167, 0.2}, {0.125, 0.143, 0.167}, {0.111, 0.125, 0.143},
             {0.111, 0.111, 0.111}};

    public static double[] fuzzyNb(double nombre)
    {
        // TEST OK

        double[] resultat = null;
        int testnombre = (int) nombre;
        if (testnombre == nombre) {
            resultat = nb[testnombre - 1];
        }
        if (nombre == 0.5) {
            resultat = nbInvers[1];
        }
        if ((nombre <= 0.34) && (nombre >= 0.3)) {
            resultat = nbInvers[2];
        }
        if (nombre == 0.25) {
            resultat = nbInvers[3];
        }
        if (nombre == 0.2) {
            resultat = nbInvers[4];
        }
        if ((nombre <= 0.17) && (nombre >= 0.16)) {
            resultat = nbInvers[5];
        }
        if ((nombre <= 0.15) && (nombre >= 0.14)) {
            resultat = nbInvers[6];
        }
        if (nombre == 0.125) {
            resultat = nbInvers[7];
        }
        if ((nombre <= 0.119) && (nombre >= 0.1)) {
            resultat = nbInvers[8];
        }

        return resultat;
    }

    public static double[][][] fuzzyMatrix(double[][] tableau)
    {
        double[][][] matrice = new double[tableau.length][tableau[0].length][3];
        for (int i = 0; i < matrice.length; i++) {
            for (int j = 0; j < matrice[0].length; j++) {

                matrice[i][j] = fuzzyNb(tableau[i][j]);

            }
        }
        return matrice;
    }

    // Fuzzy geometric mean value Ri

    public static double[][] RICalcul(double[][][] fuzMatrix)
    {
        // TEST OK

        int n = fuzMatrix.length;
        double puissance = 1 / (double) n;
        double[][] resultat = new double[n][3];

        for (int i = 0; i < n; i++) {
            for (int k = 0; k < 3; k++) {
                double produit = 1;
                for (int j = 0; j < fuzMatrix[0].length; j++) {
                    produit = produit * fuzMatrix[i][j][k];
                    resultat[i][k] = Math.pow(produit, puissance);
                }
            }
        }
        return resultat;

    }

    public static double[] RISum(double[][] tablRICal)
    {

        // TEST OK

        double[] resultat = new double[3];

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < tablRICal.length; i++) {
                resultat[j] += tablRICal[i][j];
            }
        }
        return resultat;
    }

    public static double[] invRIsum(double[] risum)
    {
        // TEST OK

        double[] resultat = new double[3];
        for (int i = 0; i < 3; i++) {
            resultat[i] = 1 / risum[2 - i];
        }
        return resultat;
    }

    public static double[][] finalTableRI(double[][] RI, double[] invRI)
    {
        // TEST OK

        double[][] resultat = new double[RI.length][3];
        for (int i = 0; i < RI.length; i++) {
            for (int j = 0; j < 3; j++) {
                resultat[i][j] = RI[i][j] * invRI[j];
            }
        }
        return resultat;
    }

    public static double[] poidsFinal(double[][] tableFinal)
    {

        // TEST OK

        double[] resultat = new double[tableFinal.length];
        double div = (double) tableFinal.length;
        double[] resultatInter = new double[tableFinal.length];
        double somme = 0;
        for (int i = 0; i < tableFinal.length; i++) {
            resultatInter[i] = (tableFinal[i][0] + 2 * tableFinal[i][1] + tableFinal[i][2]) / div;
            somme += resultatInter[i];
        }
        for (int i = 0; i < tableFinal.length; i++) {
            resultat[i] = resultatInter[i] / somme;
        }
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
        FuzzyTable IA = new FuzzyTable();
        double RC = IC / IA.getIA(n);
        if (RC <= 0.10 && RC >= 0) {
            resultat = "True";

        } else {
            resultat = "False";
        }
        return resultat;
    }

    // Une Fonction qui étant donné un triplé de fuzzy (a,b,c) renvoie la
    // ponderation z correspondante ici fonctionfuzzy1([3,4,5]) -> 4
    public static double fonctionfuzzy1(double[] vecteurfuzzy)
    {
        double resultat = 0;
        resultat = vecteurfuzzy[1];
        return resultat;

    }

    // Fonction qui calcule l'inverse pour un triplé Fuzzy car (l,m,n)^(-1)=(1/n
    // , 1/m , 1/l)

    public static double[] TripleInv(double[] triple)
    {
        // TEST OK
        double[] resultat = new double[3];
        for (int i = 0; i < 3; i++) {
            if (1 / triple[2 - i] >= 1) {
                resultat[i] = Math.round(1 / triple[2 - i]);
            } else {
                resultat[i] = 1 / triple[2 - i];
            }

        }
        return resultat;
    }

    // Fonction qui fait le symétrique de la matricé triplé fuzzy :

    public static double[][][] remplirFuzzy(double[][][] tableau)
    {
        // TEST OK
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[0].length; j++) {
                if (j <= i) {
                    tableau[i][j] = TripleInv(tableau[j][i]);
                }
            }
        }
        return tableau;
    }

}
