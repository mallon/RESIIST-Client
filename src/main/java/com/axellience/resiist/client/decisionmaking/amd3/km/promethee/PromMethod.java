package com.axellience.resiist.client.decisionmaking.amd3.km.promethee;

public class PromMethod
{

    public static double[][][] calcTabEcart(double[][] tab)
    {
        // TEST OK
        int nbcrit = tab[0].length;
        int nbdec = tab.length;

        double[][][] resultat = new double[nbcrit][nbdec][nbdec];
        for (int k = 0; k < nbcrit; k++) {
            double[][] inter = new double[nbdec][nbdec];
            for (int i = 0; i < nbdec; i++) {
                for (int j = 0; j < nbdec; j++) {
                    inter[i][j] = tab[i][k] - tab[j][k];
                }
            }
            resultat[k] = inter;

        }

        return resultat;

    }

    public static double[][] Pref(double[][] tab, int MinOrMax, int numberPref, double prefseuil,
                                  double indseuil, double s)
    {
        double[][] resultat = new double[tab.length][tab[0].length];
        switch (numberPref) {
            case 1:
                if (MinOrMax == 0) {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.Usual(-tab[i][j]);
                        }
                    }
                } else {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.Usual(tab[i][j]);
                        }
                    }

                }
                break;
            case 2:
                if (MinOrMax == 0) {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.UShape(-tab[i][j], indseuil);
                        }
                    }
                } else {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.UShape(tab[i][j], indseuil);
                        }
                    }

                }
                break;
            case 3:
                if (MinOrMax == 0) {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.VShape(-tab[i][j], prefseuil);
                        }
                    }
                } else {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.VShape(tab[i][j], prefseuil);
                        }
                    }
                }
                break;
            case 4:
                if (MinOrMax == 0) {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.VShapeInd(-tab[i][j], prefseuil, indseuil);
                        }
                    }
                } else {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.VShapeInd(tab[i][j], prefseuil, indseuil);
                        }
                    }

                }
                break;
            case 5:
                if (MinOrMax == 0) {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.Level(-tab[i][j], prefseuil, indseuil);
                        }
                    }
                } else {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.Level(tab[i][j], prefseuil, indseuil);
                        }
                    }
                }
                break;

            case 6:
                if (MinOrMax == 0) {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.Gaussian(-tab[i][j], s);
                        }
                    }
                } else {
                    for (int i = 0; i < tab.length; i++) {
                        for (int j = 0; j < tab[0].length; j++) {
                            resultat[i][j] = FoncPref.Gaussian(tab[i][j], s);
                        }
                    }
                }
                break;

        }
        return resultat;
    }

    public static double[][][] ApplyPref(double[][][] tab, int[] listMinorMax, int[] listNbPref,
                                         double[] listPrefSeuil, double[] listIndSeuil,
                                         double[] listS)
    {
        // TEST OK
        double[][][] resultat = new double[tab.length][tab[0].length][tab[0].length];
        for (int k = 0; k < tab.length; k++) {
            resultat[k] = Pref(tab[k],
                               listMinorMax[k],
                               listNbPref[k],
                               listPrefSeuil[k],
                               listIndSeuil[k],
                               listS[k]);
        }
        return resultat;
    }

    public static double[][] MatPi(double[][][] tabAP)
    {
        // TEST OK
        double[][] resultat = new double[tabAP.length][tabAP[0].length * (tabAP[0].length - 1)];
        for (int l = 0; l < resultat.length; l++) {
            int compteur = 0;
            for (int i = 0; i < tabAP[0].length; i++) {
                for (int j = 0; j < tabAP[0].length; j++) {
                    if (i != j) {

                        resultat[l][compteur] = tabAP[l][i][j];
                        compteur += 1;
                    }
                }
            }

        }
        return resultat;

    }

    public static double[][] CalcFlux(double[][] piTab, double[] poids)
    {
        double[] inter = new double[piTab[0].length];

        for (int j = 0; j < piTab[0].length; j++) {
            for (int i = 0; i < piTab.length; i++) {
                inter[j] += piTab[i][j] * poids[i];
            }
        }

        double[][] resultat = new double[piTab.length][piTab.length];
        int compteur = 0;
        for (int i = 0; i < resultat.length; i++) {
            for (int j = 0; j < resultat[0].length; j++) {
                if (i != j) {
                    resultat[i][j] = inter[compteur];
                    compteur += 1;
                }
            }
        }
        return resultat;
    }

    public static double[] PosFlux(double[][] res)
    {
        // TEST OK
        int n = res.length;
        double[] resultat = new double[n];
        for (int i = 0; i < n; i++) {
            double somme = 0;
            for (int j = 0; j < n; j++) {
                somme += res[i][j];
            }
            resultat[i] = somme / (double) (n - 1);

        }
        return resultat;
    }

    public static double[] NegFlux(double[][] res)
    {
        // TEST OK
        int n = res.length;
        double[] resultat = new double[n];
        for (int i = 0; i < n; i++) {
            double somme = 0;
            for (int j = 0; j < n; j++) {
                somme += res[j][i];
            }
            resultat[i] = somme / (double) (n - 1);

        }
        return resultat;
    }

    public static double[] FluxTot(double[] FluxNeg, double[] FluxPositif)
    {
        int n = FluxNeg.length;
        double[] resultat = new double[n];

        for (int i = 0; i < n; i++) {
            resultat[i] = FluxPositif[i] - FluxNeg[i];

        }
        return resultat;
    }

}
