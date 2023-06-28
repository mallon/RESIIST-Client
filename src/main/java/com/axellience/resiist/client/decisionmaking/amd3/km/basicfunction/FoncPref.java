package com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction;

public class FoncPref
{

    // La fonction de preference P(a,b) est definie donnant le degre de
    // preference de a sur b pour un critere.

    // Fonction de preference Forme en U (avec seuil d'indifference)

    static double Fpr1(double a, double b, double indseuil)
    {
        double res = 0;
        double ecart = a - b;
        if (Math.abs(ecart) <= indseuil) {
            res = 0; // Pas de pr�f�rence entre a et b
        } else {
            res = 1;
        }
        return res;
    }

    // Fonction de preference Forme en V (avec seuil depreference)

    static double Fpr2(double a, double b, double prefseuil)
    {
        double res = 0;
        double ecart = a - b;
        if (Math.abs(ecart) <= prefseuil) {
            res = Math.abs(ecart) / prefseuil;
        } else {
            res = 1;
        }
        return res;
    }

    // Fonction de preference Lineaire (avec seuil d'indiff�rence et seuil de
    // preference)

    static double Frp3(double a, double b, double prefseuil, double indseuil)
    {
        double res = 0;
        double ecart = a - b;
        if (Math.abs(ecart) <= indseuil) {
            res = 0;
        } else if (Math.abs(ecart) > prefseuil) {
            res = 1;
        } else {
            res = (Math.abs(ecart) - indseuil) / (prefseuil - indseuil);
        }
        return res;
    }

}
