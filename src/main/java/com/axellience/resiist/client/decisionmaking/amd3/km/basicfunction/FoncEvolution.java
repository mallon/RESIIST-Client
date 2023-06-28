package com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;

public class FoncEvolution
{
    // Voir comment borner

    // Loi normale de parametre l'esperence et la variance

    public static double FE1(double mean, double sd)
    {
        double variable = 0;

        NormalDistribution normal = new NormalDistribution(mean, sd);
        variable = normal.sample(); 								 // prend
                                    								 // une
                                    								 // valeur
                                    								 // de notre
                                    								 // distribution.

        return variable;
    }

    // Loi de weibull de parametre lambda=echelle > 0 et k=forme > 0

    public static double FE2(double lambda, double k)
    {
        double variable = 0;

        WeibullDistribution weibull = new WeibullDistribution(k, lambda);
        variable = weibull.sample();

        return variable;

    }

    // Loi de poisson de parametre lambda

    public static double FE3(double param)
    {
        double variable = 0;

        PoissonDistribution poisson = new PoissonDistribution(param);
        variable = poisson.sample(); // prend une valeur de notre distribution.

        return variable;
    }

    // Identite

    public static double FE4(double valeur)
    {
        return valeur;
    }

    // Variation de la valeur aleatoirement entre deux limites FE2min et FE2max
    public static double FE5(boolean isValeurEntre0et1, int borneinf, int bornesup)
    {
        Random r = new Random();
        double result = 0;
        if (isValeurEntre0et1 == true) {
            result = r.nextDouble();
        } else {
            result = r.nextInt(bornesup - borneinf) + borneinf;
        }
        return result;
    }

    // Fonction lineraire (croissant et decroissant) de la forme f(x)=ax+b si
    // a<0 decroissante si a>0 croissante.

    public static double FE7(double a, double b, double x)
    {
        double variable = 0;

        variable = a * x + b;

        return variable;
    }
    // Fonction exponentielle (croissant et decroissant) de la forme f(x)=a(c)^x
    // si 0 < c < 1 decroissante,
    // si c > 1 la fonction est croissante.

    public static double FE8(double a, double c, double x)
    {
        double variable = 0;

        variable = a * Math.pow(a, x);

        return variable;
    }

    // Fonction logarithmique ( croissant decroissant) de la forme f(x)=
    // a*logc(b) si 0 < c < 1 decroissante,
    // si c > 1 la fonction est croissante. S'ecrit aussi f(x) = a*(ln(x)/ln(c))

    public static double FE9(double a, double c, double x)
    {
        double variable = 0;

        variable = a * (Math.log(x) / Math.log(c));

        return variable;
    }

}
