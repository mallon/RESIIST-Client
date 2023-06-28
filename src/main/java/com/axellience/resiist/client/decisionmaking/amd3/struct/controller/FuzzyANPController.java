package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.axellience.resiist.client.decisionmaking.amd3.km.anp.ANP;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.CalcInput;
import com.axellience.resiist.client.decisionmaking.amd3.km.fuzzy.Fuzzification;

@Controller
public class FuzzyANPController
{

    // infos importantes du modele
    // POUR ANP
    int        nombredec  = 0;
    int        nombrecrit = 0;
    String     CritereList;
    String     DecisionList;
    double[]   vecteurCrit;
    double[][] vectNorm;

    @GetMapping("fuzzyAnp")
    public String getForm()
    {
        return "fuzzyANP";
    }

    @PostMapping("/fuzAnpSaveDetails")                     // it only support
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
        vectNorm = new double[nombredec + nombrecrit][Math.max(nombredec, nombrecrit)];
        return "fuzzyANP";
    }

    @PostMapping(value = "/fuzAnpGetTableCrit")
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }
        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        modelMap2.put("coheren", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            vecteurCrit = vecteurNormal;
        }

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetCrit1")
    public String obtCrit(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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
        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[0] = vecteurNormal;
        }

        modelMap3.put("coheren2", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetCrit2")
    public String obtCrit2(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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

        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        modelMap3.put("coheren3", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        if (coherence == "True") {
            vectNorm[1] = vecteurNormal;
        }

        return "fuzzyAnp";

    }

    @PostMapping(value = "/fuzAnpgetCrit3")
    public String obtCrit3(@RequestParam Map<String, String> parameters, ModelMap modelMap5) // Interface
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
        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[2] = vecteurNormal;
        }

        modelMap5.put("coheren4", coherence);
        modelMap5.put("nbcrit", nombrecrit);
        modelMap5.put("nbdec", nombredec);
        modelMap5.put("listcrit", CritereList);
        modelMap5.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetCrit4")
    public String obtCrit4(@RequestParam Map<String, String> parameters, ModelMap modelMap6) // Interface
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
        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[3] = vecteurNormal;
        }

        modelMap6.put("coheren5", coherence);
        modelMap6.put("nbcrit", nombrecrit);
        modelMap6.put("nbdec", nombredec);
        modelMap6.put("listcrit", CritereList);
        modelMap6.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetCrit5")
    public String obtCrit5(@RequestParam Map<String, String> parameters, ModelMap modelMap7) // Interface
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

        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[4] = vecteurNormal;
        }

        modelMap7.put("coheren6", coherence);
        modelMap7.put("nbcrit", nombrecrit);
        modelMap7.put("nbdec", nombredec);
        modelMap7.put("listcrit", CritereList);
        modelMap7.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetCrit6")
    public String obtCrit6(@RequestParam Map<String, String> parameters, ModelMap modelMap8) // Interface
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
        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[5] = vecteurNormal;
        }

        modelMap8.put("coheren7", coherence);
        modelMap8.put("nbcrit", nombrecrit);
        modelMap8.put("nbdec", nombredec);
        modelMap8.put("listcrit", CritereList);
        modelMap8.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetCrit7")
    public String obtCrit7(@RequestParam Map<String, String> parameters, ModelMap modelMap9) // Interface
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

        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[6] = vecteurNormal;
        }
        modelMap9.put("coheren8", coherence);
        modelMap9.put("nbcrit", nombrecrit);
        modelMap9.put("nbdec", nombredec);
        modelMap9.put("listcrit", CritereList);
        modelMap9.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetCrit8")
    public String obtCrit8(@RequestParam Map<String, String> parameters, ModelMap modelMap10) // Interface
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

        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[7] = vecteurNormal;
        }

        modelMap10.put("coheren9", coherence);
        modelMap10.put("nbcrit", nombrecrit);
        modelMap10.put("nbdec", nombredec);
        modelMap10.put("listcrit", CritereList);
        modelMap10.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetCrit9")
    public String obtCrit9(@RequestParam Map<String, String> parameters, ModelMap modelMap11) // Interface
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
        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[8] = vecteurNormal;
        }

        modelMap11.put("coheren10", coherence);
        modelMap11.put("nbcrit", nombrecrit);
        modelMap11.put("nbdec", nombredec);
        modelMap11.put("listcrit", CritereList);
        modelMap11.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetCrit10")
    public String obtCrit10(@RequestParam Map<String, String> parameters, ModelMap modelMap12) // Interface
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

        double[][][] matrice = new double[nombredec][nombredec][3];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[9] = vecteurNormal;
        }

        modelMap12.put("coheren11", coherence);
        modelMap12.put("nbcrit", nombrecrit);
        modelMap12.put("nbdec", nombredec);
        modelMap12.put("listcrit", CritereList);
        modelMap12.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec1")
    public String obtDec1(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit] = vecteurNormal;
        }

        modelMap.put("anpcoheren1", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec2")
    public String obtDec2(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit + 1] = vecteurNormal;
        }

        modelMap.put("anpcoheren2", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec3")
    public String obtDec3(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit + 2] = vecteurNormal;
        }

        modelMap.put("anpcoheren3", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec4")
    public String obtDec4(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit + 3] = vecteurNormal;
        }

        modelMap.put("anpcoheren4", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec5")
    public String obtDec5(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit + 4] = vecteurNormal;
        }

        modelMap.put("anpcoheren5", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec6")
    public String obtDec6(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit + 5] = vecteurNormal;
        }

        modelMap.put("anpcoheren6", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec7")
    public String obtDec7(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit + 6] = vecteurNormal;
        }

        modelMap.put("anpcoheren7", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec8")
    public String obtDec8(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit + 7] = vecteurNormal;
        }

        modelMap.put("anpcoheren8", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec9")
    public String obtDec9(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit + 8] = vecteurNormal;
        }

        modelMap.put("anpcoheren9", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    @PostMapping(value = "/fuzAnpgetDec10")
    public String obtDec10(@RequestParam Map<String, String> parameters, ModelMap modelMap) // Interface
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
        double[][] ricalc = Fuzzification.RICalcul(matFuzzy);
        double[] inter = Fuzzification.RISum(ricalc);
        double[] invers = Fuzzification.invRIsum(inter);
        double[][] tableRI = Fuzzification.finalTableRI(ricalc, invers);
        double[] vecteurNormal = Fuzzification.poidsFinal(tableRI);

        // Pour vérifier la cohérence on a besoin du tableau du chiffre
        // correspondant au tripplé de Fuzzy ( la transformation selon une
        // fonction)
        double[][] tableaucoh = new double[nombrecrit][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            for (int j = 0; j < nombrecrit; j++) {
                tableaucoh[i][j] = Fuzzification.fonctionfuzzy1(matFuzzy[i][j]);
            }
        }

        String coherence = Fuzzification.verifCoherenc(tableaucoh, vecteurNormal);
        if (coherence == "True") {
            vectNorm[nombrecrit + 9] = vecteurNormal;
        }

        modelMap.put("anpcoheren10", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);

        return "fuzzyANP";

    }

    // Maintenant qu'on a tout récupéré on doit générer la super Matrix et
    // calculer les scores.

    @PostMapping(value = "/fuzAnpgetResult")
    public String resultat(ModelMap model)
    {
        double[][] matrice = ANP.superMatrix(vecteurCrit, nombrecrit, nombredec, vectNorm);
        double[][] resultat = ANP.powerMatrix(matrice, 20);
        double[] vectResult = new double[nombredec];
        String sc = "";
        int indicemax = 0;
        for (int i = 0; i < nombredec; i++) {
            vectResult[i] = resultat[nombrecrit + 1 + i][0];
        }

        for (int i = 0; i < vectResult.length; i++) {
            double lemax = vectResult[0];
            if (vectResult[i] > lemax) {
                indicemax = i;
            }
            if (i != (vectResult.length - 1)) {
                sc = sc + vectResult[i] + ",";
            } else {
                sc = sc + vectResult[i];
            }

        }
        model.put("scores", Arrays.toString(vectResult));
        model.put("listdec", DecisionList);
        model.put("solution", indicemax);
        model.put("listscore", sc);

        return "anpRes";

    }

}
