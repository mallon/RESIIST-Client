package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.axellience.resiist.client.decisionmaking.amd3.km.ahp.AHP;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.CalcInput;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.MatrixSym;

@Controller
public class AHPController
{

    // infos importantes du modele
    // POUR AHP
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

    @GetMapping({"/", "/hello"})
    public String hello(@RequestParam(value = "name",
                                      defaultValue = "World",
                                      required = true) String name,
                        Model model)
    {
        model.addAttribute("name", name);
        return "hello";
    }

    @GetMapping("ahp")
    public String getForm()
    {
        return "ahp";
    }

    @PostMapping("/saveDetails")                     // it only support port
                                                     // method
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
        return "ahp";           // firstStep is view name. It will call
                                // firstStep.jsp
    }

    @PostMapping(value = "/getTableCrit")
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

        return "ahp";

    }

    @PostMapping(value = "/getCrit1")
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter1 = vecteurNormal;
        }

        modelMap3.put("coheren2", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "/getCrit2")
    public String obtCrit2(@RequestParam Map<String, String> parameters, ModelMap modelMap4) // Interface
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter2 = vecteurNormal;
        }

        modelMap4.put("coheren3", coherence);
        modelMap4.put("nbcrit", nombrecrit);
        modelMap4.put("nbdec", nombredec);
        modelMap4.put("listcrit", CritereList);
        modelMap4.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "/getCrit3")
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter3 = vecteurNormal;
        }

        modelMap5.put("coheren4", coherence);
        modelMap5.put("nbcrit", nombrecrit);
        modelMap5.put("nbdec", nombredec);
        modelMap5.put("listcrit", CritereList);
        modelMap5.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "/getCrit4")
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter4 = vecteurNormal;
        }

        modelMap6.put("coheren5", coherence);
        modelMap6.put("nbcrit", nombrecrit);
        modelMap6.put("nbdec", nombredec);
        modelMap6.put("listcrit", CritereList);
        modelMap6.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "/getCrit5")
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter2 = vecteurNormal;
        }

        modelMap7.put("coheren6", coherence);
        modelMap7.put("nbcrit", nombrecrit);
        modelMap7.put("nbdec", nombredec);
        modelMap7.put("listcrit", CritereList);
        modelMap7.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "/getCrit6")
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter6 = vecteurNormal;
        }

        modelMap8.put("coheren7", coherence);
        modelMap8.put("nbcrit", nombrecrit);
        modelMap8.put("nbdec", nombredec);
        modelMap8.put("listcrit", CritereList);
        modelMap8.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "/getCrit7")
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter7 = vecteurNormal;
        }

        modelMap9.put("coheren8", coherence);
        modelMap9.put("nbcrit", nombrecrit);
        modelMap9.put("nbdec", nombredec);
        modelMap9.put("listcrit", CritereList);
        modelMap9.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "/getCrit8")
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter8 = vecteurNormal;
        }

        modelMap10.put("coheren9", coherence);
        modelMap10.put("nbcrit", nombrecrit);
        modelMap10.put("nbdec", nombredec);
        modelMap10.put("listcrit", CritereList);
        modelMap10.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "/getCrit9")
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter9 = vecteurNormal;
        }

        modelMap11.put("coheren10", coherence);
        modelMap11.put("nbcrit", nombrecrit);
        modelMap11.put("nbdec", nombredec);
        modelMap11.put("listcrit", CritereList);
        modelMap11.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "/getCrit10")
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

        double[][] tableDec = new double[nombredec][nombredec];
        for (int i = 0; i < nombredec; i++) {
            for (int j = 0; j < nombredec; j++) {
                Integer numero = i * nombredec + j;
                String cell = numero.toString();
                String value = parameters.get(cell);
                double valeur = CalcInput.calc(value);
                tableDec[i][j] = valeur;
            }
        }

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter10 = vecteurNormal;
        }

        modelMap12.put("coheren11", coherence);
        modelMap12.put("nbcrit", nombrecrit);
        modelMap12.put("nbdec", nombredec);
        modelMap12.put("listcrit", CritereList);
        modelMap12.put("listdec", DecisionList);

        return "ahp";

    }

    @PostMapping(value = "getResult")
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
        return "res";

    }

}
