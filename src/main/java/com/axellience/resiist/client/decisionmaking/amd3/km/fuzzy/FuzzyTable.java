package com.axellience.resiist.client.decisionmaking.amd3.km.fuzzy;

public class FuzzyTable
{

    final double[] IA =
            {0, 0, 0.58, 0.90, 1.12, 1.24, 1.32, 1.41, 1.45, 1.49, 1.51, 1.48, 1.56, 1.57, 1.59};

    public double getIA(int N)
    {
        return IA[N - 1];
    }

}
