package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.Ranking;
import com.axellience.resiist.client.decisionmaking.amd3.km.csvhandler.csvHandler;
import com.axellience.resiist.client.decisionmaking.amd3.km.topsis.Topsis;

@Controller

public class TopsisController
{

    // Infos du modèle
    int      nombredec  = 0;
    int      nombrecrit = 0;
    String   CritereList;
    String   DecisionList;
    double[] vecteurCrit;
    double[] infoCriter;
    double[] poids;

    @GetMapping("/topsis")
    public String getForm()
    {
        return "topsis";
    }

    @PostMapping("/topsisSaveDetails")                     // it only support
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
        infoCriter = new double[nombrecrit];
        poids = new double[nombrecrit];
        return "topsis";
    }

    @PostMapping(value = "/topsisInfo")
    public String obtinfo(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            if (value.equals("Min")) {
                infoCriter[i] = 0;
            } else {
                infoCriter[i] = 1;
            }

        }

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "topsis";
    }

    @PostMapping(value = "/topsisPoids")
    public String obtpoids(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            poids[i] = Double.parseDouble(value);

        }
        modelMap2.put("poids", Arrays.toString(poids));

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        return "topsis";
    }

    @PostMapping(value = "/fichTopsis")
    public String obtFichier(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
            throws IOException
    {

        // questionnaire sur la forme du fichier csv

        double[][] matrice = csvHandler.csvToArray(parameters.get("fichier"), 1, 1);

        double[] vectResult = Topsis.topmethod(matrice, poids, infoCriter);

        // COPIE DE VECTRESULT POUR LE CALCUL DU RANG
        double[] copie = new double[vectResult.length];
        for (int i = 0; i < vectResult.length; i++) {
            copie[i] = vectResult[i];
        }

        modelMap2.put("vectResult", vectResult);

        int[] rank = Ranking.rank(copie);
        modelMap2.put("rang", rank);

        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "topsisRes";

    }

    @PostMapping(value = "/topsisGetTable")
    public String obtTabl(@RequestParam Map<String, String> parameters, ModelMap modelMap2) // Interface
                                                                                            // Map<K,V>
                                                                                            // Type
                                                                                            // Parameters:
                                                                                            // K
                                                                                            // -
                                                                                            // the
                                                                                            // type
                                                                                            // of
                                                                                            // keys
                                                                                            // maintained
                                                                                            // by
                                                                                            // this
                                                                                            // map
                                                                                            // V
                                                                                            // -
                                                                                            // the
                                                                                            // type
                                                                                            // of
                                                                                            // mapped
                                                                                            // values
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

        double[] vectResult = Topsis.topmethod(table, poids, infoCriter);
        // COPIE DE VECTRESULT POUR LE CALCUL DU RANG
        double[] copie = new double[vectResult.length];
        for (int i = 0; i < vectResult.length; i++) {
            copie[i] = vectResult[i];
        }

        modelMap2.put("vectResult", vectResult);

        int[] rank = Ranking.rank(copie);
        modelMap2.put("rang", rank);

        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "topsisRes";

    }
}