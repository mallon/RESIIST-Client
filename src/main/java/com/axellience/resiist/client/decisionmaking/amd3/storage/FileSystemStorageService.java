package com.axellience.resiist.client.decisionmaking.amd3.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService
{
    private final Path rootLocation;
    private boolean    alreadyImported;
    private String     fileNamespace;

    @Autowired
    public FileSystemStorageService(StorageProperties properties)
    {
        this.rootLocation = Paths.get(properties.getLocation());
        alreadyImported = false;
    }

    @Override
    public Path store(MultipartFile file)
    {
        File destFile;
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            destFile = new File(this.rootLocation.toString(), "decisionModel_generatedCSV.csv");
            destFile.createNewFile();
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }

        return destFile.toPath();
    }

    @Override
    public Stream<Path> loadAll()
    {
        try {
            return Files.walk(this.rootLocation, 1)
                        .filter(path -> !path.equals(this.rootLocation))
                        .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename)
    {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename)
    {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll()
    {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
        alreadyImported = false;
    }

    @Override
    public void init()
    {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void init(String[] args)
    {
        if (args.length > 0 && !args[0].isBlank()) {
            File destFile;
            try {
                File file = new File(args[0]);
                if (file.length() == 0) {
                    throw new StorageException("Failed to store empty file.");
                }

                destFile = new File(this.rootLocation.toString(), "decisionModel_GeneratedCSV.csv");
                destFile.createNewFile();
                try (InputStream inputStream = new FileInputStream(file);) {
                    Files.copy(inputStream, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                alreadyImported = true;
                fileNamespace = destFile.getAbsolutePath();

            }
            catch (IOException e) {
                throw new StorageException("Failed to store file.", e);
            }
        }
    }

    @Override
    public boolean isAlreadyImported()
    {
        return alreadyImported;
    }

    @Override
    public String getFileNamespace()
    {
        return fileNamespace;
    }

    @Override
    public void setFileNamespace(String fileNamespace)
    {
        this.fileNamespace = fileNamespace;
    }

    @Override
    public void setIsAlreadyImported(boolean alreadyImported)
    {
        this.alreadyImported = alreadyImported;
    }
}