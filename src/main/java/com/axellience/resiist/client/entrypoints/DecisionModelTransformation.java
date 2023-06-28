package com.axellience.resiist.client.entrypoints;

import java.io.File;

import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

import com.axellience.resiist.client.acquisitiontointegration.AcquisitionToIntegration;
import com.axellience.resiist.client.decisionmaking.DecisionMaking;

public class DecisionModelTransformation
{
    public static void main(String[] args)
    {
        AcquisitionToIntegration acquisitionToIntegration = new AcquisitionToIntegration();
        acquisitionToIntegration.connexionToModelingPlatform("_u6R7EJHPEe2s_rSsl-4wyA",
                                                             "indicator",
                                                             "password");

        ResourceImpl modelResource = acquisitionToIntegration.getModelFromServerAsBinary();

        DecisionMaking decisionMaking = new DecisionMaking();
        decisionMaking.setModelResource(modelResource);
        File csvDecisionModelFile = decisionMaking.transformDecisionModelToCSV();
        System.exit(0);
        // decisionMaking.launchDecisionEvaluationServer(csvDecisionModelFile);
        // decisionMaking.openImportUrl();
    }
}
