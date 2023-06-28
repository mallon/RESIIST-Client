package com.axellience.resiist.client.decisionmaking.amd3.km.topsis;

public class FuzzyTopsis
{

    public static double[][][] tableToFuzzy(double[][] vecteurLevel, double[][] table)
    {
        // TEST OK
        double[][][] resultat = new double[table.length][table[0].length][3];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                int valeur = (int) table[i][j];
                resultat[i][j] = vecteurLevel[valeur - 1];

            }
        }

        return resultat;
    }

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

    public static double[][][] reduceToOne(double[][][] fuzzytable, int nbdecideur)
    {
        // TEST OK
        double[][][] resultat = new double[fuzzytable.length / nbdecideur][fuzzytable[0].length][3];

        for (int j = 0; j < fuzzytable[0].length; j++) {
            for (int i = 0; i < (fuzzytable.length / nbdecideur); i++) {
                double un = fuzzytable[i * nbdecideur][j][0];
                double deux = 0;
                double trois = fuzzytable[i * nbdecideur][j][2];

                for (int nbd = 0; nbd < (nbdecideur - 1); nbd++) {

                    if (fuzzytable[i * nbdecideur + nbd + 1][j][0] < un) {
                        un = fuzzytable[i * nbdecideur + nbd + 1][j][0];
                    }

                    if (fuzzytable[i * nbdecideur + nbd + 1][j][2] > trois) {
                        trois = fuzzytable[i * nbdecideur + nbd + 1][j][2];
                    }
                }
                deux = 1
                       / (double) nbdecideur
                       * (fuzzytable[i * nbdecideur][j][1]
                          + fuzzytable[i * nbdecideur + 1][j][1]
                          + fuzzytable[i * nbdecideur + 2][j][1]);
                resultat[i][j][0] = un;
                resultat[i][j][1] = deux;
                resultat[i][j][2] = trois;

            }

        }

        return resultat;

    }

    public static double[] FuzzyTopsisMethod(double[][] table, double[][] vecteurLevel,
                                             int nbdecideur, double[] poids, double[] infocriter)
    {
        //
        double[][][] res = FuzzyTopsis.tableToFuzzy(vecteurLevel, table);
        double[][][] resultat = FuzzyTopsis.reduceToOne(res, nbdecideur);
        // Normaliser resultat
        double[][][] norm = new double[resultat.length][resultat[0].length][3];
        for (int i = 0; i < resultat.length; i++) {
            for (int j = 0; j < resultat[0].length; j++) {
                double valeur = resultat[i][j][2];
                norm[i][j][0] = resultat[i][j][0] / valeur;
                norm[i][j][1] = resultat[i][j][1] / valeur;
                norm[i][j][2] = 1;
            }
        }
        // Calculer (b1+2b2+b3)/4
        // TEST OK

        double[][] tableau = new double[norm.length][norm[0].length];
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[0].length; j++) {
                tableau[i][j] = (norm[i][j][0] + 2 * norm[i][j][1] + norm[i][j][2]) / 4;
            }
        }

        // Le vecteur infocriter indique si on doit minimiser (0) ou maximiser
        // (1) le critere.
        // Etape 1 : matrice -> Matrice normalisee
        // TEST OK
        // double[][] normalised =
        // FoncNormalisation.vectorNormalization(tableau);

        // Etape 2 : matrice normalisee * poids
        // TEST OK
        double[][] mat2 = new double[tableau.length][tableau[0].length];
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[0].length; j++) {
                mat2[i][j] = tableau[i][j] * poids[j];
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
