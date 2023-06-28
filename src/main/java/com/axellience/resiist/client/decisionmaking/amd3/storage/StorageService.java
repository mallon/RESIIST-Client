package com.axellience.resiist.client.decisionmaking.amd3.storage;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService
{

    void init();

    Path store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    void init(String[] args);
    
    boolean isAlreadyImported();

    String getFileNamespace();

    void setFileNamespace(String string);

    void setIsAlreadyImported(boolean b);

}