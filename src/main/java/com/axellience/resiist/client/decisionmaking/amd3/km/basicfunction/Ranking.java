package com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction;

import java.util.Arrays;

public class Ranking
{

    public static int[] rank(double[] tableau)
    {
        // TEST OK
        double[] copie = new double[tableau.length];
        for (int i = 0; i < tableau.length; i++) {
            copie[i] = tableau[i];
        }
        Arrays.sort(tableau);
        int[] bfind = new int[tableau.length];

        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau.length; j++) {
                if ((copie[j] == tableau[i])) {
                    bfind[j] = tableau.length - i;
                }
            }

        }

        return bfind;
    }

    /*public static String rank(double[] scores, String decisionList ) {
    	String resultat=""; 
    	int[] ind= indrank(scores); 
    	String[] liste = decisionList.split(",");
    	String[] interm = new String[scores.length]; 
    	for (int i = 0 ; i<scores.length; i++) {
    		interm[ind[i]] = liste[i]; 
    	}
    	for (int i=0; i<scores.length; i++){
    		if(i!=(scores.length-1)){
    			resultat+=interm[i]+","; 
    		}
    		else {
    			resultat+=interm[i]; 
    		}
    	}
    	return resultat; 
    	
    	
    }*/

}
