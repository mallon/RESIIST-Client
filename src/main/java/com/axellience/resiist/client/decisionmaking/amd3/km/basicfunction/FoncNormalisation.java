package com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction;

public class FoncNormalisation
{

    // static double[][] tableau;

    // function min

    public static double min(double[][] tableau)
    {
        double min = tableau[0][0];

        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[i].length; j++) {
                if (tableau[i][j] < min) {
                    min = tableau[i][j];
                }
            }
        }
        return min;
    }

    // function max

    public static double max(double[][] tableau)
    {
        double max = tableau[0][0];

        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[i].length; j++) {
                if (tableau[i][j] > max) {
                    max = tableau[i][j];
                }
            }
        }
        return max;
    }

    // function sum on the row pour une colonne fixe

    public static double[] sumRow(double[][] tableau)
    {
        int nbcol = tableau[0].length;
        double[] sumrow = new double[nbcol];

        for (int j = 0; j < nbcol; j++) {
            for (int i = 0; i < tableau.length; i++) {
                sumrow[j] += tableau[i][j];
            }
        }
        return sumrow;
    }

    // function sum on the col pour une ligne fixe

    public static double[] sumCol(double[][] tableau)
    {
        int nbcol = tableau[0].length;
        double[] sumcol = new double[nbcol];

        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < nbcol; j++) {
                sumcol[i] += tableau[i][j];
            }
        }
        return sumcol;
    }

    // function sum square on the row

    public static double[] sumSquareRow(double[][] tableau)
    {
        int nbcol = tableau[0].length;
        double[] sumrow = new double[nbcol];

        for (int j = 0; j < nbcol; j++) {
            for (int i = 0; i < tableau.length; i++) {
                sumrow[j] += tableau[i][j] * tableau[i][j];
            }
        }
        return sumrow;
    }

    // product on the row

    public static double[] productRow(double[][] tableau)
    {
        int nbcol = tableau[0].length;
        double[] sumrow = new double[nbcol];

        for (int j = 0; j < nbcol; j++) {
            sumrow[j] = 1;
            for (int i = 0; i < tableau.length; i++) {
                sumrow[j] *= tableau[i][j];
            }
        }
        return sumrow;
    }

    // Les fonctions de normalisation:

    // Max Normalization

    public static double[][] maxNormalization(double[][] tableau)
    {

        double tableaumax = max(tableau);
        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[][] tableaunorm = new double[nbrow][nbcol];

        for (int i = 0; i < nbrow; i++) {
            for (int j = 0; j < nbcol; j++) {
                tableaunorm[i][j] = tableau[i][j] / tableaumax;
            }
        }

        return tableaunorm;
    }

    // MinMax Normalization

    public static double[][] minmaxNormalization(double[][] tableau)
    {

        double tableaumax = max(tableau);
        double tableaumin = min(tableau);
        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[][] tableaunorm = new double[nbrow][nbcol];

        // Pour un critre bnfit
        // Pour un critre de cot au numrateur mettre : tableaumax-tableauij
        for (int i = 0; i < nbrow; i++) {
            for (int j = 0; j < nbcol; j++) {
                tableaunorm[i][j] = (tableau[i][j] - tableaumin) / (tableaumax - tableaumin);
            }
        }

        return tableaunorm;
    }

    // Sum Normalization

    public static double[][] sumNormalization(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[][] tableaunorm = new double[nbrow][nbcol];

        for (int i = 0; i < nbrow; i++) {
            for (int j = 0; j < nbcol; j++) {
                tableaunorm[i][j] = tableau[i][j] / sumRow(tableau)[j];
            }
        }

        return tableaunorm;
    }

    // Vector Normalization

    public static double[][] vectorNormalization(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[][] tableaunorm = new double[nbrow][nbcol];

        for (int i = 0; i < nbrow; i++) {
            for (int j = 0; j < nbcol; j++) {
                tableaunorm[i][j] = tableau[i][j] / Math.sqrt(sumSquareRow(tableau)[j]);
            }
        }

        return tableaunorm;
    }

    // Target-based Normalization

    public static double[][] targetBasedNormalization(double[][] tableau, double[] target)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[][] tableaunorm = new double[nbrow][nbcol];

        for (int i = 0; i < nbrow; i++) {
            for (int j = 0; j < nbcol; j++) {
                tableaunorm[i][j] = 1
                                    - (Math.abs((tableau[i][j] - target[j]))
                                       / (Math.max(max(tableau), target[j])
                                          - Math.min(min(tableau), target[j])));
            }
        }

        return tableaunorm;
    }

    // Logarithmic normalization

    public static double[][] logNormalization(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[][] tableaunorm = new double[nbrow][nbcol];

        for (int i = 0; i < nbrow; i++) {
            for (int j = 0; j < nbcol; j++) {
                tableaunorm[i][j] = Math.log(tableau[i][j]) / Math.log(productRow(tableau)[j]);
            }
        }
        return tableaunorm;
    }

}
