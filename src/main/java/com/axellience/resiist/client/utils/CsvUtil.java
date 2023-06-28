package com.axellience.resiist.client.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    public String createFileWithOneColumAndSomeLinesnOnly(String csvFilePath, String elementName,
                                                          int eachXLines)
    {
        elementName = elementName.replace(" ", "");
        File csvFile = new File(csvFilePath);
        String csvFileName = csvFile.getName();
        String newCSVFileName = csvFile.getParentFile().getPath()
                                + File.separator
                                + csvFileName
                                + "_"
                                + elementName.replace("#", "")
                                + ".csv";

        System.out.println("csvFilePath : " + csvFilePath);
        System.out.println("newCSVFileName : " + newCSVFileName);

        Integer elementNameColumnIndex = null;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
                FileWriter csvWriter = new FileWriter(newCSVFileName);)
        {
            new File(newCSVFileName).createNewFile();
            String line = "";
            csvWriter.write("Values" + System.lineSeparator());
            // int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                // if (lineNumber == 1 || lineNumber % eachXLines == 0) {
                String[] cols = line.split(";");
                for (int i = 0; i <= cols.length - 1; i++) {
                    String col = cols[i];
                    col = col.replace(" ", "");
                    if (elementName.equals(col)) {
                        elementNameColumnIndex = i;
                    } else if (elementNameColumnIndex != null && i == elementNameColumnIndex) {
                        csvWriter.write(col + System.lineSeparator());
                    }
                }
                // }
                // lineNumber++;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return newCSVFileName;
    }

    public File createCSVFile(String fileName, String separator,
                              Map<String, Map<String, String>> rowTitlesAndValues)
    {
        List<String> rows = toCsvLines(rowTitlesAndValues, separator);
        if (rows.isEmpty())
            return null;

        File csvFile = new File(fileName);
        Path csvFilePath = csvFile.toPath();

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(csvFilePath)) {
            bufferedWriter.write("");
            bufferedWriter.flush();

            for (String row : rows) {
                if (rows.indexOf(row) != 0) {
                    Files.write(csvFilePath,
                                System.lineSeparator().getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.APPEND);
                }
                Files.write(csvFilePath,
                            row.getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.APPEND);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return csvFile;
    }

    private List<String> toCsvLines(Map<String, Map<String, String>> rowTitlesAndValues,
                                    String separator)
    {
        List<String> rows = new ArrayList<>();
        List<String> columnTitles = new ArrayList<>();
        String emptyCellForRowAndColumnTitleGap = "";
        columnTitles.add(emptyCellForRowAndColumnTitleGap);

        for (Entry<String, Map<String, String>> entry : rowTitlesAndValues.entrySet()) {
            List<String> values = new ArrayList<>();
            values.add(entry.getKey());
            for (Entry<String, String> columnTitleAndCellValue : entry.getValue().entrySet()) {
                if (!columnTitles.contains(columnTitleAndCellValue.getKey()))
                    columnTitles.add(columnTitleAndCellValue.getKey());

                values.add(columnTitleAndCellValue.getValue());
            }
            rows.add(toCsvCells(values, separator, 0));
        }

        rows.add(0, toCsvCells(columnTitles, separator, 0));

        return rows;
    }

    private String toCsvCells(List<String> cellNames, String separator, int startingIndex)
    {
        StringBuilder cells = new StringBuilder();
        cells.append(getFirstAndEmptyCells(cellNames.get(0), separator, startingIndex));
        cellNames.remove(0);

        for (String cellName : cellNames) {
            String fullCelll = separator + cellName;
            cells.append(fullCelll);
        }

        return cells.toString();
    }

    private StringBuilder getFirstAndEmptyCells(String firstNotEmptyCellName, String separator,
                                                int startingIndex)
    {
        StringBuilder emptyCells = new StringBuilder();
        for (int i = 0; i < startingIndex; i++) {
            emptyCells.append(separator);
        }
        emptyCells.append(firstNotEmptyCellName);

        return emptyCells;
    }

    public boolean isACSVFile(String filePath)
    {
        return filePath.endsWith("csv");
    }
}
