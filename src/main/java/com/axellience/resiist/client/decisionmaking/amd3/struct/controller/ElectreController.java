package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.axellience.resiist.client.decisionmaking.amd3.km.csvhandler.csvHandler;
import com.axellience.resiist.client.decisionmaking.amd3.km.electre.Electre2;

@Controller

public class ElectreController
{

    // Infos du modèle
    int      nombredec  = 0;
    int      nombrecrit = 0;
    String   CritereList;
    String   DecisionList;
    double[] vecteurCrit;

    double[] poids;

    @GetMapping("/electre")
    public String getForm()
    {
        return "electre";
    }

    @PostMapping("/electreSaveDetails")                     // it only support
                                                            // port method
    public String saveDetails(@RequestParam("nbcrit") Integer nbCrit,
                              @RequestParam("nbdec") Integer nbDec,
                              @RequestParam("critlist") String CritList,
                              @RequestParam("declist") String DecList, ModelMap modelMap)
    {
        // write your code to save details
        modelMap.put("nbcrit", nbCrit);
        modelMap.put("nbdec", nbDec);
        modelMap.put("listcrit", CritList);
        modelMap.put("listdec", DecList);
        nombredec = nbDec;
        nombrecrit = nbCrit;
        CritereList = CritList;
        DecisionList = DecList;

        poids = new double[nombrecrit];
        return "electre";
    }

    @PostMapping(value = "/electrePoids")
    public String obtpoids(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        String lit = "";
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);
            lit = lit + value + ".";
            poids[i] = Double.parseDouble(value);

        }
        modelMap2.put("poids", Arrays.toString(poids));

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        return "electre";
    }

    @PostMapping(value = "/fichElectre")
    public String obtresWFichier(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
            throws IOException
    {
        // questionnaire sur la forme du fichier csv

        double[][] matrice = csvHandler.csvToArray(parameters.get("fichier"), 1, 1);

        int[][] resultat = Electre2.electre(matrice, poids);
        modelMap2.put("table", resultat);

        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbdec", nombredec);
        return "electreRes";
    }

    @PostMapping(value = "/electreRes")
    public String obtres(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        double[][] table = new double[nombredec][nombrecrit];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = Double.parseDouble(value);
                table[i][j] = valeur;
            }
        }

        int[][] resultat = Electre2.electre(table, poids);
        modelMap2.put("table", resultat);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "electreRes";

    }

}
