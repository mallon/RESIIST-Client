package com.axellience.resiist.client.entrypoints;

import com.axellience.resiist.client.acquisitiontointegration.AcquisitionToIntegration;
import com.axellience.resiist.client.analysis.evaluation.ResilienceRuleDefiner;
import com.axellience.resiist.client.decisionmaking.DecisionMaking;
import com.axellience.resiist.client.interpretation.InterpretationRuleGenerator;
import com.axellience.resiist.client.utils.gui.AccessApplication;

public class AcquisitionAndDecision
{
    public static void main(String[] args)
    {
        AcquisitionToIntegration acquisitionToIntegration =
                new AcquisitionToIntegration(InterpretationRuleGenerator.getInstance(),
                                             ResilienceRuleDefiner.getInstance());
        DecisionMaking decisionMaking = new DecisionMaking();
        AccessApplication accessApplication =
                AccessApplication.getInstance(acquisitionToIntegration, decisionMaking);
        while (true) {
            boolean loginDone = accessApplication.isLoginDone();
            try {
                Thread.sleep(2);
                if (loginDone) {
                    break;
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
