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
import com.axellience.resiist.client.decisionmaking.amd3.km.promethee.PromMethod;

@Controller

public class PrometheeControlleur
{
    // Infos du modèle

    int nblevel = 0;

    int        nbdecideur = 0;
    int[]      infoCriter;
    double[]   poids;
    int        nombredec  = 0;
    int        nombrecrit = 0;
    String     CritereList;
    String     DecisionList;
    double[][] table;
    int[]      NumFctPref;
    double[]   listS;
    double[]   listIndSeuil;
    double[]   listPrefSeuil;

    @GetMapping("promethee")
    public String getForm(ModelMap modelMap)
    {

        return "promethee";
    }

    @PostMapping(value = "/InfosPromethee")
    public String InfosFuzAhpTopsis(ModelMap modelMap, @RequestParam("nbcrit") Integer nbCrit,
                                    @RequestParam("nbdec") Integer nbDec,
                                    @RequestParam("critlist") String CritList,
                                    @RequestParam("declist") String DecList)
    {

        modelMap.put("nbcrit", nbCrit);
        modelMap.put("nbdec", nbDec);
        modelMap.put("listcrit", CritList);
        modelMap.put("listdec", DecList);
        nombredec = nbDec;
        nombrecrit = nbCrit;
        CritereList = CritList;
        DecisionList = DecList;
        table = new double[nombredec][nombrecrit];
        infoCriter = new int[nombrecrit];
        poids = new double[nombrecrit];
        NumFctPref = new int[nombrecrit];
        listIndSeuil = new double[nombrecrit];
        listPrefSeuil = new double[nombrecrit];
        listS = new double[nombrecrit];

        return "promethee";

    }

    @PostMapping(value = "/PrometheeCrit")
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

        return "promethee";
    }

    @PostMapping(value = "/PrometheePoids")
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
        return "promethee";
    }

    @PostMapping(value = "/fichPromethee")
    public String obtFichier(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
            throws IOException
    {

        // questionnaire sur la forme du fichier csv

        table = csvHandler.csvToArray(parameters.get("fichier"), 1, 1);

        modelMap2.put("poids", Arrays.toString(poids));

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        return "promethee";
    }

    @PostMapping(value = "/TablePromethee")
    public String obttable(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        // On recupere la table remplie par le ou les décideur(s)
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = Double.parseDouble(value);
                table[i][j] = valeur;
            }
        }

        modelMap2.put("poids", Arrays.toString(poids));

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "promethee";

    }

    @PostMapping(value = "/PrometheePref")
    // TEST OK
    public String obtpref(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);
            if (value.equals("Usual")) {
                NumFctPref[i] = 1;
            }
            if (value.equals("U-Shape")) {
                NumFctPref[i] = 2;
            }
            if (value.equals("V-Shape")) {
                NumFctPref[i] = 3;
            }
            if (value.equals("V-Shape-Ind")) {
                NumFctPref[i] = 4;
            }
            if (value.equals("Level")) {
                NumFctPref[i] = 5;
            }
            if (value.equals("Gaussian")) {
                NumFctPref[i] = 6;
            }

        }

        modelMap2.put("poids", Arrays.toString(poids));
        modelMap2.put("NumPref", Arrays.toString(NumFctPref));

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        return "promethee";

    }

    @PostMapping(value = "/ValeurPref")
    // TEST OK
    public String obtValeurPref(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        // ON récupère les infos : 1ere ligne : Q indifférence seuil
        // 2eme ligne : P préférence seuil
        // 3eme ligne : S

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = Double.parseDouble(value);
                if (i == 0) {
                    listIndSeuil[j] = valeur;
                }
                if (i == 1) {
                    listPrefSeuil[j] = valeur;
                }
                if (i == 2) {
                    listS[j] = valeur;
                }
            }
        }
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("listInd", Arrays.toString(listIndSeuil));
        modelMap2.put("listPref", Arrays.toString(listPrefSeuil));
        modelMap2.put("listS", Arrays.toString(listS));
        return "promethee";
    }

    @PostMapping(value = "/PromResultat")
    // TEST OK
    public String obtResultat(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        double[][][] tabecar = PromMethod.calcTabEcart(table);
        double[][][] tabpref = PromMethod.ApplyPref(tabecar,
                                                    infoCriter,
                                                    NumFctPref,
                                                    listPrefSeuil,
                                                    listIndSeuil,
                                                    listS);
        double[][] tabPi = PromMethod.MatPi(tabpref);
        double[][] tabFlux = PromMethod.CalcFlux(tabPi, poids);
        double[] FluxPositif = PromMethod.PosFlux(tabFlux);
        double[] FluxNeg = PromMethod.NegFlux(tabFlux);
        double[] FluxTotal = PromMethod.FluxTot(FluxNeg, FluxPositif);
        modelMap2.put("FlNeg", Arrays.toString(FluxNeg));
        modelMap2.put("FlPos", Arrays.toString(FluxPositif));
        modelMap2.put("FlTot", Arrays.toString(FluxTotal));
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "promethee";

    }

}
