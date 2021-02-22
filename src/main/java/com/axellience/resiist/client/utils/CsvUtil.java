package com.axellience.resiist.client.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvUtil
{
    private static final Logger LOGGER = Logger.getLogger(CsvUtil.class.getName());

    public MappingIterator<Map<String, String>> readCSVFile(String csvFilePath)
    {
        MappingIterator<Map<String, String>> it = null;
        try {
            File csvFile = new File(csvFilePath);
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            it = mapper.readerFor(Map.class).with(schema).readValues(csvFile);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "***No CSV found !***");
        }
        return it;
    }

    public boolean isACSVFile(String filePath)
    {
        return filePath.endsWith("csv");
    }
}
