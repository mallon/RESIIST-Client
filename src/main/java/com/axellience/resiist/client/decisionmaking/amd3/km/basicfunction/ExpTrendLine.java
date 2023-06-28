package com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction;

public class ExpTrendLine extends OLSTrendLine
{
    @Override
    protected double[] xVector(double x)
    {
        return new double[]{1, x};
    }

    @Override
    protected boolean logY()
    {
        return true;
    }
}