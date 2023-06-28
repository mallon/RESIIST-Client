package com.axellience.resiist.client.decisionmaking.amd3.struct;

import java.io.File;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.axellience.resiist.client.decisionmaking.amd3.storage.StorageProperties;
import com.axellience.resiist.client.decisionmaking.amd3.storage.StorageService;

@EnableConfigurationProperties(StorageProperties.class)
@ComponentScan({"com.axellience.resiist.client.decisionmaking.amd3.struct",
                "com.axellience.resiist.client.decisionmaking.amd3.storage"})
@ComponentScan(basePackages = {"com.axellience.resiist.client.decisionmaking.amd3.struct.controller"})
@SpringBootApplication
public class Amd3Application
{
    public static boolean        isCSVImported = false;
    public static StorageService storageService;

    public static void main(String[] args)
    {
        SpringApplication.run(Amd3Application.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService)
    {
        return (args) ->
        {
            Amd3Application.storageService = storageService;
            storageService.deleteAll();
            storageService.init();

            // if (args.length > 0)
            // storageService.init(args);
            //
            // if (storageService.isAlreadyImported())
            // isCSVImported = true;
        };
    }

    public static void setCsvFile(File csvDecisionFile)
    {
        String[] args = {csvDecisionFile.getAbsolutePath()};
        storageService.init(args);

        if (storageService.isAlreadyImported())
            isCSVImported = true;
    }

}
