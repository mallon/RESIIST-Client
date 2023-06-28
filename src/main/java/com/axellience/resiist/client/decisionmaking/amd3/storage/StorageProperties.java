package com.axellience.resiist.client.decisionmaking.amd3.storage;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties
{
    private String location;

    public StorageProperties() throws URISyntaxException
    {
        Path path = Paths.get(System.getProperty("user.dir"));
        location = path.toString() + File.separator + "upload-dir";
    }

    /**
     * Folder location for storing files
     */

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

}