package com.axellience.resiist.client.decisionmaking.amd3.km.electre;

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncAgregation;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncNormalisation;

public class Electre2
{

    public static int[] calculvectDi(int[] vectCi, int nbcrit)
    {
        // TEST OK

        int size = nbcrit - vectCi.length;
        int[] vint = new int[size];
        int compteur = 0;

        for (int i = 0; i < nbcrit; i++) {
            boolean test = true;
            for (int j = 0; j < vectCi.length; j++) {
                if (i + 1 == vectCi[j]) {
                    test = false;
                }

            }
            if (test) {

                vint[compteur] = i + 1;
                compteur += 1;
            }

        }
        return vint;
    }

    public static double[][] matrixC(double[] poids, int[][] vectCij, int nbdec)
    {
        // TEST OK
        double[][] resultat = new double[nbdec][nbdec];
        int compteur = 0;
        for (int i = 0; i < nbdec; i++) {
            for (int j = 0; j < nbdec; j++) {

                if (i != j) {

                    for (int k = 0; k < vectCij[compteur].length; k++) {
                        resultat[i][j] += poids[vectCij[compteur][k] - 1];

                    }

                    compteur += 1;
                }
            }
        }
        return resultat;
    }

    public static double[][] CreatBigM(double[][] normalised, int nbcrit, int nbdec)
    {
        // TEST OK
        double[][] bigM = new double[nbdec * (nbdec - 1)][nbcrit];

        for (int j = 0; j < bigM[0].length; j++) {
            int compteur = 0;

            for (int i = 0; i < nbdec; i++) {
                for (int k = 0; k < nbdec; k++) {
                    if (i != k) {

                        bigM[compteur][j] = Math.abs((normalised[i][j] - normalised[(k)][j]));
                        compteur += 1;
                    }

                }
            }
        }

        return bigM;
    }

    public static int[][] multipMatrix(int[][] A, int[][] B)
    {
        int[][] resultat = new int[A.length][B[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {

                for (int k = 0; k < B.length; k++) {
                    resultat[i][j] += A[i][k] * B[k][j];
                }

            }
        }
        return resultat;
    }

    public static int[][] electre(double[][] tableau, double[] poids)
    {
        int m = tableau.length;
        int nbcrit = tableau[0].length;
        // Etape 1 : matrice -> Matrice normalise
        // TEST OK
        double[][] normalised = FoncNormalisation.vectorNormalization(tableau);

        // Etape 2 : matrice normalise * poids
        // TEST OK
        double[][] mat2 = new double[tableau.length][tableau[0].length];
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[0].length; j++) {
                mat2[i][j] = normalised[i][j] * poids[j];
            }
        }

        // Etape 3 : Comparer les solutions entre elles sur chaque critere.
        // TEST OK

        int[][] vectCij = new int[m * (m - 1)][nbcrit];
        int[][] vectDij = new int[m * (m - 1)][nbcrit];
        int compteur = 0;
        for (int i = 0; i < m; i++) {
            for (int k = 0; k < m; k++) {
                String inter = "";
                if (i != k) {
                    compteur += 1;
                    for (int j = 0; j < nbcrit; j++) {
                        if (tableau[i][j] >= tableau[k][j]) {
                            inter += (j + 1) + ",";
                        }
                        String[] inter2 = inter.split(",");
                        if (inter.length() >= 1) {
                            int[] vectinter = new int[inter2.length];
                            for (int z = 0; z < inter2.length; z++) {

                                vectinter[z] = (int) Double.parseDouble(inter2[z]);
                                vectCij[compteur - 1] = vectinter;
                            }
                        } else {
                            int[] vectinter = {};
                            vectCij[compteur - 1] = vectinter;
                        }
                    }
                }
            }
        }

        // Etape 4 : Faire les vecteurs D inverse de VectCij tq si il y a 4 crit
        // et
        // C12 = [2,3] alors D12 = [1,4]
        // TEST OK
        for (int i = 0; i < vectDij.length; i++) {
            vectDij[i] = calculvectDi(vectCij[i], nbcrit);
        }

        // Etape 5 : Calcul de la matrice de concordance
        // TEST OK
        double[][] matrixC = matrixC(poids, vectCij, m);
        double[] colMatrixC = FoncAgregation.somme(matrixC, 0);
        double totalMatC = 0;
        for (int i = 0; i < colMatrixC.length; i++) {
            totalMatC += colMatrixC[i];
        }
        double cbar = totalMatC / (m * (m - 1));
        int[][] matrixE = new int[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if (matrixC[i][j] >= cbar) {
                    matrixE[i][j] = 1;
                }
            }
        }

        // Etape 6 : Calcul de la matrice de discordance
        // TEST OK
        double[][] bigM = CreatBigM(mat2, nbcrit, m);
        double[] dis = new double[m * (m - 1)];
        double[] maxrow = FoncAgregation.maxRow(bigM, 0);
        double[][] numerateur = new double[bigM.length][bigM[0].length];
        for (int i = 0; i < numerateur.length; i++) {
            for (int k = 0; k < vectDij[i].length; k++) {
                numerateur[i][vectDij[i][k] - 1] = bigM[i][vectDij[i][k] - 1];
            }
        }
        double[] maxNum = FoncAgregation.maxRow(numerateur, 0);
        for (int i = 0; i < dis.length; i++) {
            dis[i] = maxNum[i] / maxrow[i];
        }

        double[][] matDis = new double[m][m];
        int comptMatDis = 0;
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < m; i++) {
                if (i != j) {
                    matDis[j][i] = dis[comptMatDis];
                    comptMatDis += 1;
                }

            }
        }

        double[] colMatrixD = FoncAgregation.somme(matDis, 0);
        double totalMatD = 0;
        for (int i = 0; i < colMatrixD.length; i++) {
            totalMatD += colMatrixD[i];
        }
        double dbar = totalMatD / (m * (m - 1));
        int[][] matrixF = new int[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if (matDis[i][j] >= dbar) {
                    matrixF[i][j] = 1;
                }
            }
        }

        // Etape 7 : resultat

        int[][] resultat = new int[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                resultat[i][j] = matrixE[i][j] * matrixF[i][j];
            }
        }

        return resultat;

    }

}
