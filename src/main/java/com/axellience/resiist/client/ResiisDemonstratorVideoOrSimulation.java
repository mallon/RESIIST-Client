package com.axellience.resiist.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.axellience.resiist.client.acquisition.VideoSource;
import com.axellience.resiist.client.analysis.evaluation.ResilienceRuleDefiner;
import com.axellience.resiist.client.interpretation.InterpretationRuleOrchestrator;

@SpringBootApplication
public class ResiisDemonstratorVideoOrSimulation
{
    public static void main(String[] args)
    {
        /**
         * By default, Spring boot set a property - headless - to false,
         * blocking swing components (more precisely rising an headless
         * exception). One need to set it to 'true' to be able to use swing
         **/
        SpringApplicationBuilder builder =
                new SpringApplicationBuilder(ResiisDemonstratorVideoOrSimulation.class);
        builder.headless(false);
        builder.run(args);

        /** Video and simulation data source **/
        AcquisitionToIntegration extractionToIntegrationAsVideo =
                new AcquisitionToIntegration(InterpretationRuleOrchestrator.getInstance(),
                                             ResilienceRuleDefiner.getInstance());

        extractionToIntegrationAsVideo.connexionToModelingPlatform();

        extractionToIntegrationAsVideo.getModelFromServerAsBinary();

        extractionToIntegrationAsVideo.integrationConfiguration();
        extractionToIntegrationAsVideo.interpretationConfiguration();

        extractionToIntegrationAsVideo.connexionToDataSourcesAsRealTypeSimulation();

        // extractionToIntegrationAsVideo.connexionToDataSourceAsVideo();

        extractionToIntegrationAsVideo.start();

        new VideoSource().start();
    }
}