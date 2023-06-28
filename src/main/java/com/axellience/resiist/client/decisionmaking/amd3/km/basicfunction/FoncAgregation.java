package com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FoncAgregation
{
    private Map<String, Function<double[], Double>> functionNameMapping =
            new HashMap<String, Function<double[], Double>>();

    public FoncAgregation()
    {
        functionNameMapping.put("add", x -> sommeVect(x));
        functionNameMapping.put("sum", x -> sommeVect(x));
        functionNameMapping.put("average", x -> averageVect(x));
        functionNameMapping.put("product", x -> produitVect(x));
    }

    public Map<String, Function<double[], Double>> getFunctionNameMapping()
    {
        return functionNameMapping;
    }

    // Pour des tableaux de la forme premiere ligne = criteres , deuxieme ligne
    // = poids , lignes suivantes valeurs
    // toutes les fonctions sont appliquees par lignes le resultat est donc un
    // vecteur

    // Fonction somme : somme tous les elements de la ligne, si on doit omettre
    // la premiere ligne qui corresp. aux poids : poids = 1 sinon 0

    public static double[] somme(double[][] tableau, int poids)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[] vectorSum = new double[nbrow - poids]; 			 // -1 si on
                                                        			 // ne prend
                                                        			 // pas la
                                                        			 // ligne de
                                                        			 // poids

        for (int i = 0; i < nbrow - poids; i++) {
            for (int j = 0; j < nbcol; j++) {
                vectorSum[i] += tableau[i + poids][j];
            }
        }
        return vectorSum;
    }

    public static double sommeVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double vectorSum = 0; 			 // -1 si on ne prend pas la ligne de
                              			 // poids

        for (int j = 0; j < nbcol; j++) {
            vectorSum += tableau[j];
        }

        return vectorSum;
    }

    // Fonction moyenne : moyenne tous les elements de la ligne, on doit omettre
    // la premiere ligne qui corresp. aux poids

    public static double[] average(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[] vectorMoy = new double[nbrow - 1]; 			 // -1 car on ne
                                                    			 // prend pas la
                                                    			 // ligne de
                                                    			 // poids

        for (int i = 0; i < nbrow - 1; i++) {
            for (int j = 0; j < nbcol; j++) {
                vectorMoy[i] += tableau[i + 1][j]; 				 // On somme
                                                   				 // tous les
                                                   				 // elements
            }
            vectorMoy[i] = vectorMoy[i] / (nbcol);  			 // On fait la
                                                    			 // moyenne
        }
        return vectorMoy;
    }

    public static double averageVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double vectorMoy = 0;

        for (int j = 0; j < nbcol; j++) {
            vectorMoy += tableau[j]; 				 // On somme tous les
                                     				 // elements
        }
        vectorMoy = vectorMoy / (nbcol);  			 // On fait la moyenne

        return vectorMoy;
    }

    // Fonction mediane : mediane de la ligne, on doit omettre la premi�re ligne
    // qui corresp. aux poids
    public static double[] median(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;

        double[] vectorMedian = new double[nbrow - 1]; 		 // -1 car on ne
                                                       		 // prend pas la
                                                       		 // ligne de poids
                                                       		 // La mediane
                                                       		 // corresp. a
                                                       		 // l'element milieu
                                                       		 // de la ligne
                                                       		 // ordonnee.
                                                       		 // Si le nombre de
                                                       		 // colonne est
                                                       		 // impaire on fait
                                                       		 // la moyenne des
                                                       		 // deux termes
                                                       		 // milieu.
        double milieu = ((double) nbcol + 1) / 2; 				 // on met
                                                  				 // (double)
                                                  				 // pour que le
                                                  				 // resultat
                                                  				 // soit la
                                                  				 // valeur
                                                  				 // exacte
        int test = 0;
        test = (int) milieu; 								 // si milieu = 2.5
                             								 // par exemple
                             								 // (int) milieu
                             								 // renvoie 2

        for (int i = 0; i < nbrow - 1; i++) {
            Arrays.sort(tableau[i + 1]); 						 // on trie par
                                         						 // ordre
                                         						 // croissant
                                         						 // chaque ligne
                                         						 // du tableau
                                         						 // on demarre a
                                         						 // la 1
            if (test == milieu) {								 // si le milieu
                                 								 // est un
                                 								 // entier :
                vectorMedian[i] = tableau[i + 1][test - 1];  	 // -1 car
                                                             	 // l'indicage
                                                             	 // commence � 0
                                                             	 // pour les
                                                             	 // colonnes
            } else { 											 // sinon on
                     											 // fait la
                     											 // moyenne des
                     											 // valeurs en
                     											 // dessous et
                     											 // au dessus de
                     											 // la mediane
                vectorMedian[i] = (tableau[i + 1][test - 1] + tableau[i + 1][test]) / 2;
            }
        }
        return vectorMedian;

    }

    public static double medianVect(double[] tableau)
    {

        int nbcol = tableau.length;
        double vectorMedian = 0;
        // La mediane corresp. a l'element milieu de la ligne ordonnee.
        // Si le nombre de colonne est impaire on fait la moyenne des deux
        // termes milieu.
        double milieu = ((double) nbcol + 1) / 2; 				 // on met
                                                  				 // (double)
                                                  				 // pour que le
                                                  				 // resultat
                                                  				 // soit la
                                                  				 // valeur
                                                  				 // exacte
        int test = 0;
        test = (int) milieu; 								 // si milieu = 2.5
                             								 // par exemple
                             								 // (int) milieu
                             								 // renvoie 2

        Arrays.sort(tableau); 						 // on trie par ordre
                              						 // croissant chaque ligne
                              						 // du tableau on demarre a
                              						 // la 1
        if (test == milieu) {								 // si le milieu est
                             								 // un entier :
            vectorMedian = tableau[test - 1];  	 // -1 car l'indicage commence �
                                               	 // 0 pour les colonnes
        } else { 											 // sinon on fait la
                 											 // moyenne des
                 											 // valeurs en
                 											 // dessous et au
                 											 // dessus de la
                 											 // mediane
            vectorMedian = (tableau[test - 1] + tableau[test]) / 2;
        }
        return vectorMedian;

    }

    // Fonction min : renvoie un vecteur du min de chaque ligne

    public static double[] minRow(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;

        double[] vectorMin = new double[nbrow - 1];

        for (int i = 0; i < nbrow - 1; i++) {
            vectorMin[i] = tableau[i + 1][0];
            for (int j = 1; j < nbcol; j++) {
                if (tableau[i + 1][j] < vectorMin[i]) {
                    vectorMin[i] = tableau[i + 1][j];
                }
            }
        }
        return vectorMin;
    }

    public static double minRowVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double vectorMin = 0;

        vectorMin = tableau[0];
        for (int j = 1; j < nbcol; j++) {
            if (tableau[j] < vectorMin) {
                vectorMin = tableau[j];
            }
        }

        return vectorMin;
    }

    // Fonction max : renvoie un vecteur du max de chaque ligne

    public static double[] maxRow(double[][] tableau, int poids)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;

        double[] vectorMax = new double[nbrow - poids];

        for (int i = 0; i < nbrow - poids; i++) {
            vectorMax[i] = tableau[i + poids][0];
            for (int j = 1; j < nbcol; j++) {
                if (tableau[i + poids][j] > vectorMax[i]) {
                    vectorMax[i] = tableau[i + poids][j];
                }
            }
        }
        return vectorMax;
    }

    public static double maxRowVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double vectorMax = 0;

        vectorMax = tableau[0];
        for (int j = 1; j < nbcol; j++) {
            if (tableau[j] > vectorMax) {
                vectorMax = tableau[j];
            }
        }

        return vectorMax;
    }

    // Fonction produit : renvoie le produit de tous les elements de la ligne

    public static double[] produit(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[] vectorProd = new double[nbrow - 1]; 			 // -1 car on ne
                                                     			 // prend pas la
                                                     			 // ligne de
                                                     			 // poids

        for (int i = 0; i < nbrow - 1; i++) {
            vectorProd[i] = 1;
            for (int j = 0; j < nbcol; j++) {
                vectorProd[i] *= tableau[i + 1][j];
            }
        }
        return vectorProd;
    }

    public static double produitVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double vectorProd = 1;

        for (int j = 0; j < nbcol; j++) {
            vectorProd *= tableau[j];
        }

        return vectorProd;
    }

    // Fonction increased average

    public static double[] incAverage(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[] somme = new double[nbrow - 1];
        double[] vectorIncAv = average(tableau);
        for (int i = 0; i < nbrow - 1; i++) {
            for (int j = 0; j < nbcol; j++) {
                somme[i] += tableau[i + 1][j] / average(tableau)[i];
            }
            vectorIncAv[i] += somme[i];
        }
        return vectorIncAv;
    }

    public static double incAverageVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double somme = 0;
        double vectorIncAv = averageVect(tableau);

        for (int j = 0; j < nbcol; j++) {
            somme += tableau[j] / averageVect(tableau);
        }
        vectorIncAv += somme;

        return vectorIncAv;
    }

    // Fonction decreased average

    public static double[] decAverage(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[] somme = new double[nbrow - 1];
        double[] vectorDecAv = average(tableau);
        for (int i = 0; i < nbrow - 1; i++) {
            for (int j = 0; j < nbcol; j++) {
                somme[i] += tableau[i + 1][j] / average(tableau)[i];
            }
            vectorDecAv[i] -= somme[i];
        }
        return vectorDecAv;
    }

    public static double decAverageVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double somme = 0;
        double vectorDecAv = averageVect(tableau);

        for (int j = 0; j < nbcol; j++) {
            somme += tableau[j] / averageVect(tableau);
        }
        vectorDecAv -= somme;

        return vectorDecAv;
    }

    // Fonction increased Maximum

    public static double[] incMax(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[] somme = new double[nbrow - 1];
        double[] vectorIncMax = maxRow(tableau, 1);
        for (int i = 0; i < nbrow - 1; i++) {
            for (int j = 0; j < nbcol; j++) {
                somme[i] += tableau[i + 1][j] / maxRow(tableau, 1)[i];
            }
            vectorIncMax[i] += somme[i];
        }
        return vectorIncMax;
    }

    public static double incMaxVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double somme = 0;
        double vectorIncMax = maxRowVect(tableau);

        for (int j = 0; j < nbcol; j++) {
            somme += tableau[j] / maxRowVect(tableau);
        }
        vectorIncMax += somme;

        return vectorIncMax;
    }

    // Fonction decreased Maximum

    public static double[] decMax(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[] somme = new double[nbrow - 1];
        double[] vectorDecMax = maxRow(tableau, 1);
        for (int i = 0; i < nbrow - 1; i++) {
            for (int j = 0; j < nbcol; j++) {
                somme[i] += tableau[i + 1][j] / maxRow(tableau, 1)[i];
            }
            vectorDecMax[i] -= somme[i];
        }
        return vectorDecMax;
    }

    public static double decMaxVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double somme = 0;
        double vectorDecMax = maxRowVect(tableau);

        for (int j = 0; j < nbcol; j++) {
            somme += tableau[j] / maxRowVect(tableau);
        }
        vectorDecMax -= somme;

        return vectorDecMax;
    }

    // Fonction increased Minimum

    public static double[] incMin(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[] somme = new double[nbrow - 1];
        double[] vectorIncMin = minRow(tableau);
        for (int i = 0; i < nbrow - 1; i++) {
            for (int j = 0; j < nbcol; j++) {
                somme[i] += tableau[i + 1][j] / minRow(tableau)[i];
            }
            vectorIncMin[i] += somme[i];
        }
        return vectorIncMin;
    }

    public static double incMinVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double somme = 0;
        double vectorIncMin = minRowVect(tableau);

        for (int j = 0; j < nbcol; j++) {
            somme += tableau[j] / minRowVect(tableau);
        }
        vectorIncMin += somme;

        return vectorIncMin;
    }

    // Fonction decreased Minimum

    public static double[] decMin(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;
        double[] somme = new double[nbrow - 1];
        double[] vectorDecMin = minRow(tableau);
        for (int i = 0; i < nbrow - 1; i++) {
            for (int j = 0; j < nbcol; j++) {
                somme[i] += tableau[i + 1][j] / minRow(tableau)[i];
            }
            vectorDecMin[i] -= somme[i];
        }
        return vectorDecMin;
    }

    public static double decMinVect(double[] tableau)
    {

        int nbcol = tableau.length;

        double somme = 0;
        double vectorDecMin = minRowVect(tableau);

        for (int j = 0; j < nbcol; j++) {
            somme += tableau[j] / minRowVect(tableau);
        }

        vectorDecMin -= somme;

        return vectorDecMin;
    }

    // Somme ponderee

    public static double[] sumPond(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;

        double[] vectorSumPond = new double[nbrow - 1];
        for (int i = 0; i < nbrow - 1; i++) {
            for (int j = 0; j < nbcol; j++) {
                vectorSumPond[i] += tableau[i + 1][j] * tableau[0][j];
            }
        }
        return vectorSumPond;
    }

    // Produit ponderee version 1 : (produit (des valeurs puissance le poids))

    public static double[] prodPond1(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;

        double[] vectorProdPond = new double[nbrow - 1];
        for (int i = 0; i < nbrow - 1; i++) {
            vectorProdPond[i] = 1;
            for (int j = 0; j < nbcol; j++) {
                vectorProdPond[i] *= Math.pow(tableau[i + 1][j], tableau[0][j]);
            }
        }
        return vectorProdPond;
    }

    // Produit pondere version 2 : R(Ai/Aj) = prod (pour k = 1 a nbrow-1) de
    // (aik/ajk)^wk

    public static double[][] prodPond2(double[][] tableau)
    {

        int nbrow = tableau.length;
        int nbcol = tableau[0].length;

        double[][] matProdPond = new double[nbrow - 1][nbrow - 1]; // On cree
                                                                   // une
                                                                   // matrice
                                                                   // carree
                                                                   // dont
                                                                   // l'element
                                                                   // xij sera
                                                                   // R(Ai/Aj)
                                                                   // i,j =
                                                                   // 0,...,nbrow-1-1
        for (int i = 0; i < nbrow - 1; i++) {
            for (int j = 0; j < nbrow - 1; j++) {
                matProdPond[i][j] = 1;
                for (int k = 0; k < nbcol; k++) {
                    matProdPond[i][j] *=
                            Math.pow((tableau[i + 1][k] / tableau[j + 1][k]), tableau[0][k]);
                }

            }
        }
        return matProdPond;
    }

}
