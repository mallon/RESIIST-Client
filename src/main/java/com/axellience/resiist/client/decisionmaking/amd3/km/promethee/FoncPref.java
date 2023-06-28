package com.axellience.resiist.client.decisionmaking.amd3.km.promethee;

public class FoncPref
{

    // La fonction de preference P(a,b) est definie donnant le degre de
    // preference de a sur b pour un critere.

    // Fonction de preference Forme en U (avec seuil d'indiff�rence)

    public static double UShape(double ecart, double indseuil)
    {
        double res = 0;

        if ((ecart) <= indseuil) {
            res = 0;
        } else {
            res = 1;
        }
        return res;
    }

    // Fonction Level (avec seuil d'indifference et seuil de preference)

    public static double Level(double ecart, double prefseuil, double indseuil)
    {
        double res = 0;

        if ((ecart) <= indseuil) {
            res = 0;
        }
        if (((ecart) > indseuil) && ((ecart) <= prefseuil)) {
            res = 0.5;
        }
        if ((ecart) > prefseuil) {
            res = 1;
        }
        return res;

    }

    // Fonction Forme en V (avec seuil de preference)

    public static double VShape(double ecart, double prefseuil)
    {
        double res = 0;

        if (((ecart) <= prefseuil) && (((ecart) >= 0))) {
            res = (ecart) / prefseuil;
        }
        if (ecart <= 0) {
            res = 0;
        }
        if (ecart > prefseuil) {
            res = 1;
        }
        return res;
    }

    // Fonction Lineaire (avec seuil d'indifference et seuil de preference)

    public static double VShapeInd(double ecart, double prefseuil, double indseuil)
    {
        double res = 0;

        if ((ecart) <= indseuil) {
            res = 0;
        } else if ((ecart) > prefseuil) {
            res = 1;
        } else if (((ecart) <= prefseuil) && ((ecart) > indseuil)) {
            res = ((ecart) - indseuil) / (prefseuil - indseuil);
        }
        return res;
    }

    // Fonction Usuelle

    public static double Usual(double ecart)
    {
        double res = 0;

        if ((ecart) <= 0) {
            res = 0;
        } else {
            res = 1;
        }
        return res;
    }

    // Fonction Gaussian

    public static double Gaussian(double ecart, double s)
    {
        double res = 0;

        if ((ecart) <= 0) {
            res = 0;
        }

        else {
            res = 1 - Math.exp(-(Math.pow(ecart, 2) / (2 * Math.pow(s, 2))));
        }
        return res;
    }

}
