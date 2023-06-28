package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.axellience.resiist.client.decisionmaking.amd3.km.ahp.AHP;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.CalcInput;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.MatrixSym;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.Ranking;
import com.axellience.resiist.client.decisionmaking.amd3.km.topsis.Topsis;

@Controller

public class AHPTopsisController
{

    // Infos du modèle
    int      nombredec  = 0;
    int      nombrecrit = 0;
    String   CritereList;
    String   DecisionList;
    double[] vecteurCrit;
    double[] infoCriter;
    double[] poids;

    @GetMapping("/ahptopsis")
    public String getForm()
    {
        return "AHPTopsis";
    }

    @PostMapping("/ahptopsisSaveDetails")                     // it only support
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
        return "AHPTopsis";
    }

    @PostMapping(value = "/ahptopsisInfo")
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

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "AHPTopsis";
    }

    @PostMapping(value = "/ahptopsisPoids")
    public String obtpoids(@RequestParam Map<String, String> parameters, ModelMap modelMap2) // Interface
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

        double[][] tableCrit = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableCrit[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableCrit);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        modelMap2.put("coheren", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        if (coherence == "True") {
            vecteurCrit = vecteurNormal;
        }
        modelMap2.put("poids", Arrays.toString(vecteurCrit));

        return "AHPTopsis";

    }

    @PostMapping(value = "/ahptopsisGetTable")
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

        double[] vectResult = Topsis.topmethod(table, vecteurCrit, infoCriter);
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

        return "ahpTopsisRes";

    }
}
