package com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction;

public interface TrendLine
{
    public void setValues(double[] y, double[] x); // y ~ f(x)

    public double predict(double x); // get a predicted y for a given x
}
