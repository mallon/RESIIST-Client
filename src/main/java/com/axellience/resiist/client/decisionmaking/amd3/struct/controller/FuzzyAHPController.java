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
import com.axellience.resiist.client.decisionmaking.amd3.km.fuzzy.Fuzzification;

@Controller

public class FuzzyAHPController
{
    // Infos du modèle
    int      nombredec  = 0;
    int      nombrecrit = 0;
    String   CritereList;
    String   DecisionList;
    double[] vecteurCrit;
    double[] criter1;
    double[] criter2;
    double[] criter3;
    double[] criter4;
    double[] criter5;
    double[] criter6;
    double[] criter7;
    double[] criter8;
    double[] criter9;
    double[] criter10;

    @GetMapping("fuzzyAhp")
    public String getForm()
    {
        return "fuzzyAHP";
    }

    @PostMapping("/fuzAhpSaveDetails")                     // it only support
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
        // vectNorm= new double[nombredec+nombrecrit][Math.max(nombredec,
        // nombrecrit)];
        return "fuzzyAHP";
    }

    @PostMapping(value = "/fuzAhpGetTableCrit")
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
        modelMap2.put("vecteur", Arrays.toString(vecteurCrit));

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit1")
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
        modelMap3.put("coheren2", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        if (coherence == "True") {
            criter1 = vecteurNormal;
        }

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit2")
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
        if (coherence == "True") {
            criter2 = vecteurNormal;
        }

        modelMap3.put("coheren3", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit3")
    public String obtCrit3(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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
            criter3 = vecteurNormal;
        }

        modelMap3.put("coheren4", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit4")
    public String obtCrit4(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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
            criter4 = vecteurNormal;
        }

        modelMap3.put("coheren5", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit5")
    public String obtCrit5(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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
            criter5 = vecteurNormal;
        }

        modelMap3.put("coheren6", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit6")
    public String obtCrit6(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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
            criter6 = vecteurNormal;
        }

        modelMap3.put("coheren7", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit7")
    public String obtCrit7(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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
            criter7 = vecteurNormal;
        }

        modelMap3.put("coheren8", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit8")
    public String obtCrit8(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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
            criter8 = vecteurNormal;
        }

        modelMap3.put("coheren9", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit9")
    public String obtCrit9(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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
            criter9 = vecteurNormal;
        }

        modelMap3.put("coheren10", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyAHP";

    }

    @PostMapping(value = "/fuzahpgetCrit10")
    public String obtCrit10(@RequestParam Map<String, String> parameters, ModelMap modelMap3) // Interface
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
            criter10 = vecteurNormal;
        }

        modelMap3.put("coheren11", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "fuzzyAHP";

    }

    @PostMapping(value = "fuzahpgetResult")
    public String resultat(ModelMap model)
    {
        double[][] tableaufinal = null;
        String sc = "";
        double indicemax = 0;
        if (nombrecrit == 2) {
            tableaufinal = AHP.tableauFinal(vecteurCrit, criter1, criter2);

        }
        if (nombrecrit == 3) {
            tableaufinal = AHP.tableauFinal(vecteurCrit, criter1, criter2, criter3);

        }
        if (nombrecrit == 4) {
            tableaufinal = AHP.tableauFinal(vecteurCrit, criter1, criter2, criter3, criter4);
        }
        if (nombrecrit == 5) {
            tableaufinal =
                    AHP.tableauFinal(vecteurCrit, criter1, criter2, criter3, criter4, criter5);
        }
        if (nombrecrit == 6) {
            tableaufinal = AHP.tableauFinal(vecteurCrit,
                                            criter1,
                                            criter2,
                                            criter3,
                                            criter4,
                                            criter5,
                                            criter6);
        }
        if (nombrecrit == 7) {
            tableaufinal = AHP.tableauFinal(vecteurCrit,
                                            criter1,
                                            criter2,
                                            criter3,
                                            criter4,
                                            criter5,
                                            criter6,
                                            criter7);
        }
        if (nombrecrit == 8) {
            tableaufinal = AHP.tableauFinal(vecteurCrit,
                                            criter1,
                                            criter2,
                                            criter3,
                                            criter4,
                                            criter5,
                                            criter6,
                                            criter7,
                                            criter8);
        }
        if (nombrecrit == 9) {
            tableaufinal = AHP.tableauFinal(vecteurCrit,
                                            criter1,
                                            criter2,
                                            criter3,
                                            criter4,
                                            criter5,
                                            criter6,
                                            criter7,
                                            criter8,
                                            criter9);
        }
        if (nombrecrit == 10) {
            tableaufinal = AHP.tableauFinal(vecteurCrit,
                                            criter1,
                                            criter2,
                                            criter3,
                                            criter4,
                                            criter5,
                                            criter6,
                                            criter7,
                                            criter8,
                                            criter9,
                                            criter10);
        }
        double[] scores = AHP.scores(tableaufinal);
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > indicemax) {
                indicemax = i;
            }
            if (i != (scores.length - 1)) {
                sc = sc + scores[i] + ",";
            } else {
                sc = sc + scores[i];
            }

        }

        model.put("laliste", Arrays.toString(scores));
        model.put("score", sc);
        model.put("listdec", DecisionList);
        model.put("solution", indicemax);
        return "fuzahpRes";

    }

}
