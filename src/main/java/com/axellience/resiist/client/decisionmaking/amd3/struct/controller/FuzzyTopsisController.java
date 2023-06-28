package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.CalcInput;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.Ranking;
import com.axellience.resiist.client.decisionmaking.amd3.km.topsis.FuzzyTopsis;

@Controller

public class FuzzyTopsisController
{
    // Infos du modèle

    int        nblevel    = 0;
    double[][] vecteurLevel;
    int        nbdecideur = 0;
    double[]   infoCriter;
    double[]   poids;
    int        nombredec  = 0;
    int        nombrecrit = 0;
    String     CritereList;
    String     DecisionList;

    @GetMapping("fuzzyTopsis")
    public String getForm(ModelMap modelMap)
    {

        return "fuzzyTopsis";
    }

    @PostMapping(value = "/FuzTopsisGetTable")
    public String obtab(@RequestParam("nblevel") Integer nbLevel, ModelMap modelMap,
                        @RequestParam("nbdecideur") Integer nbDecideur,
                        @RequestParam("nbcrit") Integer nbCrit,
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
        nbdecideur = nbDecideur;
        nblevel = nbLevel;
        modelMap.put("nblevel", nbLevel);
        modelMap.put("nbDecideur", nbDecideur);
        infoCriter = new double[nombrecrit];
        poids = new double[nombrecrit];
        vecteurLevel = new double[nblevel][3];
        return "fuzzyTopsis";

    }

    @PostMapping(value = "/FuzzytopsisInfo")
    public String obtinfo(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        String lit = "";
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);
            lit = lit + value + ".";
            if (value.equals("Min")) {
                infoCriter[i] = 0;
            } else {
                infoCriter[i] = 1;
            }

        }

        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "fuzzyTopsis";
    }

    @PostMapping(value = "/FuzzytopsisPoids")
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

        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        return "fuzzyTopsis";
    }

    @PostMapping(value = "/valeurFuzzy")
    public String obtfuz(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        for (int i = 0; i < nblevel; i++) {

            Integer numero = i + 1;
            String cell = numero.toString();
            double[] fuzzy = new double[3];
            String car = parameters.get(cell);
            String res = CalcInput.fuzzycalc(car);
            String vect[] = res.split(",");
            for (int k = 0; k < 3; k++) {
                fuzzy[k] = Double.parseDouble(vect[k]);
            }
            vecteurLevel[i] = fuzzy;
        }

        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "fuzzyTopsis";

    }

    @PostMapping(value = "/Table")
    public String obttable(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        double[][] table = new double[nombredec * nbdecideur][nombrecrit];

        // On recupere la table remplie par le ou les décideur(s)
        for (int i = 0; i < nombredec; i++) {
            for (int dec = 0; dec < nbdecideur; dec++) {

                for (int j = 0; j < nombrecrit; j++) {
                    Integer numero = i * nombrecrit * nbdecideur + dec * nombrecrit + j;
                    String cell = numero.toString();
                    String value = parameters.get(cell);
                    double valeur = Double.parseDouble(value);
                    table[i * (nbdecideur) + dec][j] = valeur;
                }
            }
        }

        double[] vectResult =
                FuzzyTopsis.FuzzyTopsisMethod(table, vecteurLevel, nbdecideur, poids, infoCriter);

        // COPIE DE VECTRESULT POUR LE CALCUL DU RANG
        double[] copie = new double[vectResult.length];
        for (int i = 0; i < vectResult.length; i++) {
            copie[i] = vectResult[i];
        }

        modelMap2.put("vectResult", vectResult);

        int[] rank = Ranking.rank(copie);
        modelMap2.put("rang", rank);

        modelMap2.put("ligne1", Arrays.toString(table[0]));
        modelMap2.put("ligne2", Arrays.toString(table[1]));
        modelMap2.put("ligne3", Arrays.toString(table[2]));
        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "FuzzytopsisRes";

    }

}
