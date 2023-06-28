package com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction;

public class MatrixSym
{

    // Pour AHP rempli la matrice tq Mij=x et Mji=1/x

    public static double[][] remplir(double[][] tableau)
    {
        int test = 0;

        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[0].length; j++) {

                if (j <= i) {
                    test = (int) tableau[j][i];
                    if (test == tableau[j][i]) {
                        tableau[i][j] = 1 / tableau[j][i];
                    } else {
                        tableau[i][j] = Math.round(1 / tableau[j][i]);
                    }
                }
            }
        }
        return tableau;
    }
}
