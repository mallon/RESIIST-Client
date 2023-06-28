package com.axellience.resiist.client.decisionmaking.amd3.km.csvhandler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.CalcInput;

public class csvHandler
{

    // lit un tableau depuis un fichier de lignes contenant des colonnes separes
    // par un caractere c. le nom doit etre absolu ou relatif au dossier de
    // travail
    public static String[][] readCSV(String nomFichier, char c, Charset charset, int first)
            throws IOException
    {
        return Files.lines(Paths.get(nomFichier), charset)
                    .skip(first)
                    .map(ligne -> ligne.split(String.valueOf(c)))
                    .toArray(String[][]::new);
    }

    public static double convert(String input)
    {
        input = input.replace(',', '.');
        int decimalSeperator = input.lastIndexOf('.');

        if (decimalSeperator > -1) {
            input = input.substring(0, decimalSeperator).replace(".", "")
                    + input.substring(decimalSeperator);
        }

        if (input.isBlank() || input.isEmpty())
            input = "0";

        return Double.valueOf(input);
    }

    public static double[][] csvToArray(String nomFichier, int rowTitle, int colTitle)
            throws IOException
    {

        // On saute une ligne au
        // debut pour
        // ne pas tenir
        // compte des
        // titres si
        // rowTitle = 1
        String[][] tableau2 = readCSV(nomFichier, ';', StandardCharsets.UTF_8, rowTitle);

        // On creer
        // un tableau
        // avec 1
        // ou 0
        // colonne de
        // moins depend
        // de si
        // premiere colonne
        // est des
        // titres
        double[][] tableauCsv = new double[tableau2.length][tableau2[0].length - colTitle];

        for (int ligne = 0; ligne < tableau2.length; ligne++) {
            // Notre indice
            // va de
            // 0 a
            // nb col -1
            for (int colonne = 0; colonne < tableau2[ligne].length - colTitle; colonne++) {
                // on prend
                // colonne+1 pour
                // sauter la
                // premiere colonne
                // de titres
                String cell = tableau2[ligne][colonne + colTitle];
                tableauCsv[ligne][colonne] = convert(cell);
            }

        }

        return tableauCsv;

    }

    public static double[][][] csvToArrayFuzzy(String nomFichier, int rowTitle, int colTitle)
            throws IOException
    {
        // On
        // saute une
        // ligne au
        // debut pour
        // ne pas
        // tenir compte
        // des titres
        // si rowTitle
        // = 1
        String[][] tableau2 = readCSV(nomFichier, ';', StandardCharsets.UTF_8, rowTitle);
        // On creer
        // un tableau
        // avec 1
        // ou 0
        // colonne de
        // moins depend
        // de si
        // premiere colonne
        // est des
        // titres
        double[][][] tableauCsv = new double[tableau2.length][tableau2[0].length - colTitle][3];

        for (int ligne = 0; ligne < tableau2.length; ligne++) {
            // Notre indice
            // va de
            // 0 a
            // nb col -1
            for (int colonne = 0; colonne < tableau2[ligne].length - colTitle; colonne++) {
                double[] fuzzy = new double[3];
                // on prend
                // colonne+1 pour
                // sauter la
                // premiere colonne
                // de titres
                String res = CalcInput.fuzzycalc(tableau2[ligne][colonne + colTitle]);

                String vect[] = res.split(",");
                for (int k = 0; k < 3; k++) {
                    fuzzy[k] = Double.parseDouble(vect[k]);
                }
                tableauCsv[ligne][colonne] = fuzzy;

            }

        }

        return tableauCsv;

    }
    // Recuperer liste criteres

    public static String csvFirstRow(String nomFichier) throws IOException
    {
        String[][] tableau2 = readCSV(nomFichier, ';', StandardCharsets.UTF_8, 0);
        String res = "";
        for (int i = 1; i < tableau2[0].length; i++) {
            if (i == 1) {
                res += tableau2[0][i];
            } else {
                res += "," + tableau2[0][i];
            }

        }

        return res;
    }

    // Recuperer liste decisions

    public static String csvFirstCol(String nomFichier) throws IOException
    {
        String[][] tableau2 = readCSV(nomFichier, ';', StandardCharsets.UTF_8, 0);
        String res = "";
        for (int i = 1; i < tableau2.length; i++) {
            String cellValue = tableau2[i][0];
            if (isADecision(cellValue)) {
                // if (i == 1) {
                // res += cellValue;
                // } else {
                // res += "," + cellValue;
                // }
                if (res.isEmpty()) {
                    res += cellValue;
                } else {
                    res += "," + cellValue;
                }
            }
        }

        return res;
    }

    public static String csvNRow(String fileName, String rowTitle) throws IOException
    {
        String[][] fileTable = readCSV(fileName, ';', StandardCharsets.UTF_8, 0);

        String res = "";
        String[] wantedRow = getWantedRowWithoutTitle(fileTable, rowTitle);
        for (int i = 0; i < wantedRow.length; i++) {
            if (i == 0) {
                res += wantedRow[i];
            } else {
                res += "," + wantedRow[i];
            }

        }

        return res;
    }

    private static String[] getWantedRowWithoutTitle(String[][] fileTable, String rowTitle)
    {
        for (int i = 0; i < fileTable.length; i++) {
            String[] currentRow = fileTable[i];

            if (rowTitle.equals(currentRow[0])) {
                List<String> asList = new ArrayList<>();
                for (int j = 1; j < currentRow.length; j++) {
                    asList.add(currentRow[j]);
                }
                return asList.toArray(String[]::new);
            }
        }
        return null;
    }

    private static boolean isADecision(String cellValue)
    {
        return !"Min or max".equals(cellValue)
               && !"Preference function".equals(cellValue)
               && !"Preference threshold".equals(cellValue)
               && !"Indifference threshold".equals(cellValue)
               && !"Weight".equals(cellValue);
    }

    // Recuperer vecteur
    public static double[] csvToVect(String nomFichier) throws IOException
    {

        String[][] tableau2 = readCSV(nomFichier, ';', StandardCharsets.UTF_8, 0); // On
                                                                                   // saute
                                                                                   // une
                                                                                   // ligne
                                                                                   // au
                                                                                   // début
                                                                                   // pour
                                                                                   // ne
                                                                                   // pas
                                                                                   // tenir
                                                                                   // compte
                                                                                   // des
                                                                                   // titres
                                                                                   // si
                                                                                   // rowTitle
                                                                                   // =
                                                                                   // 1

        double[] tableauCsv = new double[tableau2[0].length];  // On créer un
                                                               // tableau avec 1
                                                               // ou 0 colonne
                                                               // de moins
                                                               // dépend de si
                                                               // première
                                                               // colonne est
                                                               // des titres

        for (int ligne = 0; ligne < tableau2[0].length; ligne++) {

            tableau2[0][ligne] = tableau2[0][ligne].replaceAll("\\uFEFF", "");
            tableauCsv[ligne] = convert(tableau2[0][ligne]); // on prend
                                                             // colonne+1 pour
                                                             // sauter la
                                                             // première colonne
                                                             // de titres
        }

        return tableauCsv;

    }
}
