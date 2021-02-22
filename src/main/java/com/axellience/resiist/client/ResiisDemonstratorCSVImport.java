package com.axellience.resiist.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResiisDemonstratorCSVImport
{
    public static void main(String[] args)
    {
        SpringApplication.run(ResiisDemonstratorCSVImport.class, args);

        ExtractionToModel extractionToModeling = new ExtractionToModel();

        String objectAFilePath = args[0];
        String relationFilePath = args[1];
        String objectBFilePath = args[2];
        extractionToModeling.createModelWithCSV(objectAFilePath, relationFilePath, objectBFilePath);
    }
}