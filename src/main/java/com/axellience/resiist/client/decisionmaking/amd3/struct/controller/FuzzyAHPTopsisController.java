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
import com.axellience.resiist.client.decisionmaking.amd3.km.fuzzy.Fuzzification;
import com.axellience.resiist.client.decisionmaking.amd3.km.topsis.FuzzyAhpTopsis;
import com.axellience.resiist.client.decisionmaking.amd3.km.topsis.FuzzyTopsis;

@Controller

public class FuzzyAHPTopsisController
{
    // Infos du modèle

    int        nblevel    = 0;
    double[][] vecteurLevel;
    int        nbdecideur = 0;
    double[]   infoCriter;
    double[]   poidsfinal;
    int        nombredec  = 0;
    int        nombrecrit = 0;
    String     CritereList;
    String     DecisionList;
    double[][] Dec;

    double[] RCfinal;

    @GetMapping("FuzzyAhpTopsis")
    public String getForm(ModelMap modelMap)
    {

        return "FuzzyAhpTopsis";
    }

    @PostMapping(value = "/InfosFuzAhpTopsis")
    public String InfosFuzAhpTopsis(@RequestParam("nblevel") Integer nbLevel, ModelMap modelMap,
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
        poidsfinal = new double[nombrecrit];
        vecteurLevel = new double[nblevel][3];
        RCfinal = new double[nbDecideur];
        Dec = new double[nbDecideur][nbCrit];

        return "FuzzyAhpTopsis";

    }

    @PostMapping(value = "/valeurFuzzy2")
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

        return "FuzzyAhpTopsis";

    }

    @PostMapping(value = "/FuzzyAhptopsisCrit")
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

        return "FuzzyAhpTopsis";
    }

    // Calcul du poids par Fuzzy-AHP multidécideur.

    @PostMapping(value = "/fuzAhptopGetTableCrit1")
    public String obtCrit(@RequestParam Map<String, String> parameters, ModelMap modelMap2) // Interface
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

        double[][][] matrice = new double[nombrecrit][nombrecrit][3];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                double[] fuzzy = new double[3];
                String car = parameters.get(cell);
                String res = CalcInput.fuzzycalc(car);
                String vect[] = res.split(",");
                for (int k = 0; k < 3; k++) {
                    fuzzy[k] = Double.parseDouble(vect[k]);
                }
                matrice[i][j] = fuzzy;
            }
        }
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("coheren2", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[0] = poids1;
            RCfinal[0] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);

        modelMap2.put("RC", RC);
        modelMap2.put("poids", Arrays.toString(poids1));

        return "FuzzyAhpTopsis";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit2")
    public String obtCrit2(@RequestParam Map<String, String> parameters, ModelMap modelMap2) // Interface
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

        double[][][] matrice = new double[nombrecrit][nombrecrit][3];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                double[] fuzzy = new double[3];
                String car = parameters.get(cell);
                String res = CalcInput.fuzzycalc(car);
                String vect[] = res.split(",");
                for (int k = 0; k < 3; k++) {
                    fuzzy[k] = Double.parseDouble(vect[k]);
                }
                matrice[i][j] = fuzzy;
            }
        }
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("coheren3", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[1] = poids1;
            RCfinal[1] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);

        modelMap2.put("RC2", RC);
        modelMap2.put("poids2", Arrays.toString(poids1));

        return "FuzzyAhpTopsis";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit3")
    public String obtCrit3(@RequestParam Map<String, String> parameters, ModelMap modelMap2) // Interface
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

        double[][][] matrice = new double[nombrecrit][nombrecrit][3];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                double[] fuzzy = new double[3];
                String car = parameters.get(cell);
                String res = CalcInput.fuzzycalc(car);
                String vect[] = res.split(",");
                for (int k = 0; k < 3; k++) {
                    fuzzy[k] = Double.parseDouble(vect[k]);
                }
                matrice[i][j] = fuzzy;
            }
        }
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("coheren4", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[2] = poids1;
            RCfinal[2] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);

        modelMap2.put("RC3", RC);
        modelMap2.put("poids3", Arrays.toString(poids1));

        return "FuzzyAhpTopsis";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit4")
    public String obtCrit4(@RequestParam Map<String, String> parameters, ModelMap modelMap2) // Interface
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

        double[][][] matrice = new double[nombrecrit][nombrecrit][3];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                double[] fuzzy = new double[3];
                String car = parameters.get(cell);
                String res = CalcInput.fuzzycalc(car);
                String vect[] = res.split(",");
                for (int k = 0; k < 3; k++) {
                    fuzzy[k] = Double.parseDouble(vect[k]);
                }
                matrice[i][j] = fuzzy;
            }
        }
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("coheren5", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[3] = poids1;
            RCfinal[3] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);

        modelMap2.put("RC4", RC);
        modelMap2.put("poids4", Arrays.toString(poids1));

        return "FuzzyAhpTopsis";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit5")
    public String obtCrit5(@RequestParam Map<String, String> parameters, ModelMap modelMap2) // Interface
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

        double[][][] matrice = new double[nombrecrit][nombrecrit][3];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                double[] fuzzy = new double[3];
                String car = parameters.get(cell);
                String res = CalcInput.fuzzycalc(car);
                String vect[] = res.split(",");
                for (int k = 0; k < 3; k++) {
                    fuzzy[k] = Double.parseDouble(vect[k]);
                }
                matrice[i][j] = fuzzy;
            }
        }
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("coheren6", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[4] = poids1;
            RCfinal[4] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);

        modelMap2.put("RC5", RC);
        modelMap2.put("poids5", Arrays.toString(poids1));

        return "FuzzyAhpTopsis";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit6")
    public String obtCrit6(@RequestParam Map<String, String> parameters, ModelMap modelMap2) // Interface
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

        double[][][] matrice = new double[nombrecrit][nombrecrit][3];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                Integer numero = i * nombrecrit + j;
                String cell = numero.toString();
                double[] fuzzy = new double[3];
                String car = parameters.get(cell);
                String res = CalcInput.fuzzycalc(car);
                String vect[] = res.split(",");
                for (int k = 0; k < 3; k++) {
                    fuzzy[k] = Double.parseDouble(vect[k]);
                }
                matrice[i][j] = fuzzy;
            }
        }
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("coheren7", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[5] = poids1;
            RCfinal[5] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideur);
        modelMap2.put("vecteurLevel", vecteurLevel);

        modelMap2.put("RC6", RC);
        modelMap2.put("poids6", Arrays.toString(poids1));

        return "FuzzyAhpTopsis";
    }

    @PostMapping(value = "/FusionPoids")
    public String fusion(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        poidsfinal = FuzzyAhpTopsis.ConsistentWeights(RCfinal, Dec);
        modelMap2.put("poidsfinal", poidsfinal);
        return "FuzzyAhpTopsis";

    }

    @PostMapping(value = "/TableFuzAHPTopsis")
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

        double[] vectResult = FuzzyTopsis.FuzzyTopsisMethod(table,
                                                            vecteurLevel,
                                                            nbdecideur,
                                                            poidsfinal,
                                                            infoCriter);

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
