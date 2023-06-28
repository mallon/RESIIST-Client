package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.axellience.resiist.client.decisionmaking.amd3.km.ahp.AHP;
import com.axellience.resiist.client.decisionmaking.amd3.km.anp.ANP;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.CalcInput;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.MatrixSym;
import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.Ranking;
import com.axellience.resiist.client.decisionmaking.amd3.km.csvhandler.csvHandler;
import com.axellience.resiist.client.decisionmaking.amd3.km.electre.Electre2;
import com.axellience.resiist.client.decisionmaking.amd3.km.fuzzy.Fuzzification;
import com.axellience.resiist.client.decisionmaking.amd3.km.promethee.PromMethod;
import com.axellience.resiist.client.decisionmaking.amd3.km.topsis.FuzzyAhpTopsis;
import com.axellience.resiist.client.decisionmaking.amd3.km.topsis.FuzzyTopsis;
import com.axellience.resiist.client.decisionmaking.amd3.km.topsis.Topsis;
import com.axellience.resiist.client.decisionmaking.amd3.storage.StorageService;

@Controller
public class ImportController
{

    // Importation
    String gpp;
    double MatImport;
    int    nombrecrit;
    int    nombredec;
    String CritereList;
    String DecisionList;
    String minOrMaxs;
    String weights;
    String prefFunctions;
    String prefThresholds;
    String indiffThresholds;
    // GP1
    double[]   poidsfuzzyTop;
    double[]   vectCritFuzzyAHP;
    double[]   criter1FuzzyAHP;
    double[]   criter2FuzzyAHP;
    double[]   criter3FuzzyAHP;
    double[]   criter4FuzzyAHP;
    double[]   criter5FuzzyAHP;
    double[]   criter6FuzzyAHP;
    double[]   criter7FuzzyAHP;
    double[]   criter8FuzzyAHP;
    double[]   criter9FuzzyAHP;
    double[]   criter10FuzzyAHP;
    double[]   vectCritAHP;
    double[]   criter1AHP;
    double[]   criter2AHP;
    double[]   criter3AHP;
    double[]   criter4AHP;
    double[]   criter5AHP;
    double[]   criter6AHP;
    double[]   criter7AHP;
    double[]   criter8AHP;
    double[]   criter9AHP;
    double[]   criter10AHP;
    double[][] vectNormFANP;
    double[]   vectCritFANP;
    double[][] vectNormANP;

    // GP2

    int        nbdecideurG2;
    int        nblevelG2;
    double[]   infoCriterFT;
    double[][] vecteurLevelFuzTop;
    double[]   poidsFuzTop;
    double[]   infoCriterFAHPT;
    double[]   poidsfinal;
    double[]   RCfinal;
    double[][] Dec;

    // GP3
    String[]   listMet;
    double[]   poidsElectre;
    double[]   poidsPromethee;
    double[][] LaMatrice;
    double[]   infoCriterTopsis;
    double[]   infoCriterAHPTopsis;
    int[]      infoCriterProm;
    double[]   poidsTopsis;
    int[]      NumFctPref;
    double[]   listS;
    double[]   listIndSeuil;
    double[]   listPrefSeuil;
    double[]   vectCritAHPTOP;
    double[]   vecteurCritANP;

    int nbmeth;

    // Resutlats groupe3
    int[][]  resultatElectre;
    double[] FluxPositif;
    double[] FluxNeg;
    double[] FluxTotal;
    double[] vectResultTop;
    int[]    rankTopsis;
    double[] vectResultAHPT;
    int[]    rankAHPTop;

    // Resultat groupe2

    double[] vectResultFuzTop;
    int[]    rankFuzTop;
    double[] vectResultFAHPT;
    int[]    rankFuzAhpTop;

    // Resultat groupe1

    double[] scoresAHP;
    double[] vectResultANP;
    double[] vectResultFANP;
    double[] scoresFAHP;
    int[]    rankscoresFAHP;
    int[]    rankscoresAHP;
    int[]    rankscoresFANP;
    int[]    rankscoresANP;

    private final StorageService storageService;

    @Autowired
    public ImportController(StorageService storageService)
    {
        this.storageService = storageService;
    }

    @GetMapping("/home")
    public String getForm()
    {
        return "Home";
    }
    // IMPORTATION

    @GetMapping("/importation")
    public String impt()
    {
        return "Importation";
    }

    // CHOIX DU GROUPE DE METHODES

    @PostMapping(value = "/GroupMet")
    public String SelectMet(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        // TEST OK

        String gp = parameters.get("groupMet");
        gpp = gp;
        modelMap2.put("gp", gpp);
        modelMap2.put("alreadyImported", storageService.isAlreadyImported());
        modelMap2.put("fileNamespace", storageService.getFileNamespace());
        return "Importation";
    }

    // IMPORTATION DE LA TABLE MULTICRITERE

    @PostMapping(value = "/fichMethImport")
    public String obtresWFichier(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String cheminAbsolu = chemin.toAbsolutePath().toString();

        importCriteriaTable(cheminAbsolu, modelMap2);

        storageService.setFileNamespace("");
        storageService.setIsAlreadyImported(false);

        return "Importation";
    }

    private void importCriteriaTable(String cheminAbsolu, ModelMap modelMap2) throws IOException
    {
        double[][] matrice = csvHandler.csvToArray(cheminAbsolu, 1, 1);
        nombrecrit = matrice[0].length;
        nombredec = matrice.length;
        CritereList = csvHandler.csvFirstRow(cheminAbsolu);
        DecisionList = csvHandler.csvFirstCol(cheminAbsolu);
        minOrMaxs = csvHandler.csvNRow(cheminAbsolu, "Min or max");
        weights = csvHandler.csvNRow(cheminAbsolu, "Weight");
        prefFunctions = csvHandler.csvNRow(cheminAbsolu, "Preference function");
        indiffThresholds = csvHandler.csvNRow(cheminAbsolu, "Indifference threshold");
        prefThresholds = csvHandler.csvNRow(cheminAbsolu, "Preference threshold");
        LaMatrice = matrice;

        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("table", matrice);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("minOrMaxs", minOrMaxs);
        modelMap2.put("weights", weights);
        modelMap2.put("prefFunctions", prefFunctions);
        modelMap2.put("prefThresholds", prefThresholds);
        modelMap2.put("indiffThresholds", indiffThresholds);
        modelMap2.put("gp", gpp);
    }

    @PostMapping(value = "/fichMethAlreadyImport")
    public String importCriteriaTableFromAlreadyImportedFile(ModelMap modelMap2) throws IOException
    {
        String cheminAbsolu = storageService.getFileNamespace();

        importCriteriaTable(cheminAbsolu, modelMap2);

        return "Importation";
    }

    // RECOLTE DES PARAMETRES GROUPE 1

    @PostMapping("/fuzAhpSaveDetailsImport")                     // it only
                                                                 // support port
                                                                 // method
    public String saveDetails(@RequestParam("nbcrit") Integer nbCrit,
                              @RequestParam("nbdec") Integer nbDec,
                              @RequestParam("critlist") String CritList,
                              @RequestParam("declist") String DecList, ModelMap modelMap)
    {
        // write your code to save details

        nombredec = nbDec;
        nombrecrit = nbCrit;
        CritereList = CritList;
        DecisionList = DecList;
        modelMap.put("nbcrit", nbCrit);
        modelMap.put("nbdec", nbDec);
        modelMap.put("listcrit", CritList);
        modelMap.put("listdec", DecList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);
        vectNormFANP = new double[nombredec + nombrecrit][Math.max(nombredec, nombrecrit)];
        vectNormANP = new double[nombredec + nombrecrit][Math.max(nombredec, nombrecrit)];
        return "Importation";
    }

    @PostMapping(value = "/Methode1")
    public String obtMeth1(@RequestParam String[] met1, ModelMap modelMap2)
    {

        listMet = met1;
        nbmeth = listMet.length;

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        modelMap2.put("nbcrit", nombrecrit);

        modelMap2.put("nbdec", nombredec);

        return "Importation";
    }
    // FUZZY-AHP

    @PostMapping(value = "/fuzAhpGetTableCritImport")
    public String obtFich(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);

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
            vectCritFuzzyAHP = vecteurNormal;
        }
        modelMap2.put("vecteur", Arrays.toString(vectCritFuzzyAHP));
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit1Import")
    public String obtCrit(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException // Interface Map<K,V> Type Parameters: K - the
                               // type of keys maintained by this map V - the
                               // type of mapped values
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);

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
            criter1FuzzyAHP = vecteurNormal;
        }
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit2Import")
    public String obtCrit2(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);

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
            criter2FuzzyAHP = vecteurNormal;
        }

        modelMap3.put("coheren3", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit3Import")
    public String obtCrit3(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            criter3FuzzyAHP = vecteurNormal;
        }

        modelMap3.put("coheren4", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit4Import")
    public String obtCrit4(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            criter4FuzzyAHP = vecteurNormal;
        }

        modelMap3.put("coheren5", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit5Import")
    public String obtCrit5(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            criter5FuzzyAHP = vecteurNormal;
        }

        modelMap3.put("coheren6", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit6Import")
    public String obtCrit6(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            criter6FuzzyAHP = vecteurNormal;
        }

        modelMap3.put("coheren7", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit7Import")
    public String obtCrit7(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            criter7FuzzyAHP = vecteurNormal;
        }

        modelMap3.put("coheren8", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit8Import")
    public String obtCrit8(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            criter8FuzzyAHP = vecteurNormal;
        }

        modelMap3.put("coheren9", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit9Import")
    public String obtCrit9(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            criter9FuzzyAHP = vecteurNormal;
        }

        modelMap3.put("coheren10", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzahpgetCrit10Import")
    public String obtCrit10(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            criter10FuzzyAHP = vecteurNormal;
        }

        modelMap3.put("coheren11", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    // AHP

    @PostMapping(value = "/AhpGetTableCritImport")
    public String obtTablAHP(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableCrit = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableCrit);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        modelMap2.put("coherenahp", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        if (coherence == "True") {
            vectCritAHP = vecteurNormal;
        }

        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit1Import")
    public String obtCritAHP(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter1AHP = vecteurNormal;
        }

        modelMap3.put("coheren2ahp", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);
        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit2Import")
    public String obtCrit2AHP(@RequestParam("file") MultipartFile file, ModelMap modelMap4)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter2AHP = vecteurNormal;
        }

        modelMap4.put("coheren3ahp", coherence);
        modelMap4.put("nbcrit", nombrecrit);
        modelMap4.put("nbdec", nombredec);
        modelMap4.put("listcrit", CritereList);
        modelMap4.put("listdec", DecisionList);
        modelMap4.put("listMet", listMet);
        modelMap4.put("nbmet", nbmeth);
        modelMap4.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit3Import")
    public String obtCrit3AHP(@RequestParam("file") MultipartFile file, ModelMap modelMap5)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter3AHP = vecteurNormal;
        }

        modelMap5.put("coheren4ahp", coherence);
        modelMap5.put("nbcrit", nombrecrit);
        modelMap5.put("nbdec", nombredec);
        modelMap5.put("listcrit", CritereList);
        modelMap5.put("listdec", DecisionList);
        modelMap5.put("listMet", listMet);
        modelMap5.put("nbmet", nbmeth);
        modelMap5.put("gp", gpp);
        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit4Import")
    public String obtCrit4AHP(@RequestParam("file") MultipartFile file, ModelMap modelMap6)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter4AHP = vecteurNormal;
        }

        modelMap6.put("coheren5ahp", coherence);
        modelMap6.put("nbcrit", nombrecrit);
        modelMap6.put("nbdec", nombredec);
        modelMap6.put("listcrit", CritereList);
        modelMap6.put("listdec", DecisionList);
        modelMap6.put("listMet", listMet);
        modelMap6.put("nbmet", nbmeth);
        modelMap6.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit5Import")
    public String obtCrit5AHP(@RequestParam("file") MultipartFile file, ModelMap modelMap7)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter5AHP = vecteurNormal;
        }

        modelMap7.put("coheren6ahp", coherence);
        modelMap7.put("nbcrit", nombrecrit);
        modelMap7.put("nbdec", nombredec);
        modelMap7.put("listcrit", CritereList);
        modelMap7.put("listdec", DecisionList);
        modelMap7.put("listMet", listMet);
        modelMap7.put("nbmet", nbmeth);
        modelMap7.put("gp", gpp);
        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit6Import")
    public String obtCrit6AHP(@RequestParam("file") MultipartFile file, ModelMap modelMap8)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter6AHP = vecteurNormal;
        }

        modelMap8.put("coheren7ahp", coherence);
        modelMap8.put("nbcrit", nombrecrit);
        modelMap8.put("nbdec", nombredec);
        modelMap8.put("listcrit", CritereList);
        modelMap8.put("listdec", DecisionList);
        modelMap8.put("listMet", listMet);
        modelMap8.put("nbmet", nbmeth);
        modelMap8.put("gp", gpp);
        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit7Import")
    public String obtCrit7AHP(@RequestParam("file") MultipartFile file, ModelMap modelMap9)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter7AHP = vecteurNormal;
        }

        modelMap9.put("coheren8ahp", coherence);
        modelMap9.put("nbcrit", nombrecrit);
        modelMap9.put("nbdec", nombredec);
        modelMap9.put("listcrit", CritereList);
        modelMap9.put("listdec", DecisionList);
        modelMap9.put("listMet", listMet);
        modelMap9.put("nbmet", nbmeth);
        modelMap9.put("gp", gpp);
        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit8Import")
    public String obtCrit8AHP(@RequestParam("file") MultipartFile file, ModelMap modelMap10)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter8AHP = vecteurNormal;
        }

        modelMap10.put("coheren9ahp", coherence);
        modelMap10.put("nbcrit", nombrecrit);
        modelMap10.put("nbdec", nombredec);
        modelMap10.put("listcrit", CritereList);
        modelMap10.put("listdec", DecisionList);
        modelMap10.put("listMet", listMet);
        modelMap10.put("nbmet", nbmeth);
        modelMap10.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit9Import")
    public String obtCrit9AHP(@RequestParam("file") MultipartFile file, ModelMap modelMap11)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter9AHP = vecteurNormal;
        }

        modelMap11.put("coheren10ahp", coherence);
        modelMap11.put("nbcrit", nombrecrit);
        modelMap11.put("nbdec", nombredec);
        modelMap11.put("listcrit", CritereList);
        modelMap11.put("listdec", DecisionList);
        modelMap11.put("listMet", listMet);
        modelMap11.put("nbmet", nbmeth);
        modelMap11.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/ahpgetCrit10Import")
    public String obtCrit10AHP(@RequestParam("file") MultipartFile file, ModelMap modelMap12)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            criter10AHP = vecteurNormal;
        }

        modelMap12.put("coheren11ahp", coherence);
        modelMap12.put("nbcrit", nombrecrit);
        modelMap12.put("nbdec", nombredec);
        modelMap12.put("listcrit", CritereList);
        modelMap12.put("listdec", DecisionList);
        modelMap12.put("listMet", listMet);
        modelMap12.put("nbmet", nbmeth);
        modelMap12.put("gp", gpp);
        return "Importation";

    }

    // FUZZY-ANP

    @PostMapping(value = "/fuzAnpGetTableCritImport")
    public String obtTabl(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);

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
        modelMap2.put("coherenanp", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            vectCritFANP = vecteurNormal;
        }
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit1Import")
    public String obtCritFANP(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);

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
            vectNormFANP[0] = vecteurNormal;
        }

        modelMap3.put("coheren2anp", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit2Import")
    public String obtCrit2FANP(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
        modelMap3.put("coheren3anp", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        if (coherence == "True") {
            vectNormFANP[1] = vecteurNormal;
        }
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit3Import")
    public String obtCrit3FANP(@RequestParam("file") MultipartFile file, ModelMap modelMap5)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[2] = vecteurNormal;
        }

        modelMap5.put("coheren4anp", coherence);
        modelMap5.put("nbcrit", nombrecrit);
        modelMap5.put("nbdec", nombredec);
        modelMap5.put("listcrit", CritereList);
        modelMap5.put("listdec", DecisionList);
        modelMap5.put("listMet", listMet);
        modelMap5.put("nbmet", nbmeth);
        modelMap5.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit4Import")
    public String obtCrit4FANP(@RequestParam("file") MultipartFile file, ModelMap modelMap6)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[3] = vecteurNormal;
        }

        modelMap6.put("coheren5anp", coherence);
        modelMap6.put("nbcrit", nombrecrit);
        modelMap6.put("nbdec", nombredec);
        modelMap6.put("listcrit", CritereList);
        modelMap6.put("listdec", DecisionList);
        modelMap6.put("listMet", listMet);
        modelMap6.put("nbmet", nbmeth);
        modelMap6.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit5Import")
    public String obtCrit5FANP(@RequestParam("file") MultipartFile file, ModelMap modelMap7)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[4] = vecteurNormal;
        }

        modelMap7.put("coheren6anp", coherence);
        modelMap7.put("nbcrit", nombrecrit);
        modelMap7.put("nbdec", nombredec);
        modelMap7.put("listcrit", CritereList);
        modelMap7.put("listdec", DecisionList);
        modelMap7.put("listMet", listMet);
        modelMap7.put("nbmet", nbmeth);
        modelMap7.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit6Import")
    public String obtCrit6FNAP(@RequestParam("file") MultipartFile file, ModelMap modelMap8)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[5] = vecteurNormal;
        }

        modelMap8.put("coheren7anp", coherence);
        modelMap8.put("nbcrit", nombrecrit);
        modelMap8.put("nbdec", nombredec);
        modelMap8.put("listcrit", CritereList);
        modelMap8.put("listdec", DecisionList);
        modelMap8.put("listMet", listMet);
        modelMap8.put("nbmet", nbmeth);
        modelMap8.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit7Import")
    public String obtCrit7FANP(@RequestParam("file") MultipartFile file, ModelMap modelMap9)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[6] = vecteurNormal;
        }
        modelMap9.put("coheren8anp", coherence);
        modelMap9.put("nbcrit", nombrecrit);
        modelMap9.put("nbdec", nombredec);
        modelMap9.put("listcrit", CritereList);
        modelMap9.put("listdec", DecisionList);
        modelMap9.put("listMet", listMet);
        modelMap9.put("nbmet", nbmeth);
        modelMap9.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit8Import")
    public String obtCrit8FANP(@RequestParam("file") MultipartFile file, ModelMap modelMap10)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[7] = vecteurNormal;
        }

        modelMap10.put("coheren9anp", coherence);
        modelMap10.put("nbcrit", nombrecrit);
        modelMap10.put("nbdec", nombredec);
        modelMap10.put("listcrit", CritereList);
        modelMap10.put("listdec", DecisionList);
        modelMap10.put("listMet", listMet);
        modelMap10.put("nbmet", nbmeth);
        modelMap10.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit9Import")
    public String obtCrit9FANP(@RequestParam("file") MultipartFile file, ModelMap modelMap11)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[8] = vecteurNormal;
        }

        modelMap11.put("coheren10anp", coherence);
        modelMap11.put("nbcrit", nombrecrit);
        modelMap11.put("nbdec", nombredec);
        modelMap11.put("listcrit", CritereList);
        modelMap11.put("listdec", DecisionList);
        modelMap11.put("listMet", listMet);
        modelMap11.put("nbmet", nbmeth);
        modelMap11.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetCrit10Import")
    public String obtCrit10FANP(@RequestParam("file") MultipartFile file, ModelMap modelMap12)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[9] = vecteurNormal;
        }

        modelMap12.put("coheren11anp", coherence);
        modelMap12.put("nbcrit", nombrecrit);
        modelMap12.put("nbdec", nombredec);
        modelMap12.put("listcrit", CritereList);
        modelMap12.put("listdec", DecisionList);
        modelMap12.put("listMet", listMet);
        modelMap12.put("nbmet", nbmeth);
        modelMap12.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec1Import")
    public String obtDec1(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit] = vecteurNormal;
        }

        modelMap.put("anpcoheren1", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);
        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec2Import")
    public String obtDec2(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit + 1] = vecteurNormal;
        }

        modelMap.put("anpcoheren2", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec3Import")
    public String obtDec3(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit + 2] = vecteurNormal;
        }

        modelMap.put("anpcoheren3", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec4Import")
    public String obtDec4(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit + 3] = vecteurNormal;
        }

        modelMap.put("anpcoheren4", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec5Import")
    public String obtDec5(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit + 4] = vecteurNormal;
        }

        modelMap.put("anpcoheren5", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec6Import")
    public String obtDec6(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit + 5] = vecteurNormal;
        }

        modelMap.put("anpcoheren6", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec7Import")
    public String obtDec7(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit + 6] = vecteurNormal;
        }

        modelMap.put("anpcoheren7", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);
        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec8Import")
    public String obtDec8(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit + 7] = vecteurNormal;
        }

        modelMap.put("anpcoheren8", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec9Import")
    public String obtDec9(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit + 8] = vecteurNormal;
        }

        modelMap.put("anpcoheren9", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fuzAnpgetDec10Import")
    public String obtDec10(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
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
            vectNormFANP[nombrecrit + 9] = vecteurNormal;
        }

        modelMap.put("anpcoheren10", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    // ANP

    @PostMapping(value = "/anpGetTableCritImport")
    public String obtTablANP(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableCrit = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableCrit);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        modelMap2.put("vcoheren", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        if (coherence == "True") {
            vecteurCritANP = vecteurNormal;
        }

        return "Importation";

    }

    // Une fois qu'on a récupéré le nombre de décisions et de critères on doit
    // créer le vecteur vectNorm pour la super Matrix tq :
    // vectNorm de la forme : (nbcrit x vectNorm Criter->Decisions) + (nbdec x
    // vectNorm Decision-> Criter)
    // l'important est le mobre de ligne et pas le nombre de colonnes.

    @PostMapping(value = "/anpgetCrit1Import")
    public String obtCritANP(@RequestParam("file") MultipartFile file, ModelMap modelMap3)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[0] = vecteurNormal;
        }

        modelMap3.put("vcoheren2", coherence);
        modelMap3.put("nbcrit", nombrecrit);
        modelMap3.put("nbdec", nombredec);
        modelMap3.put("listcrit", CritereList);
        modelMap3.put("listdec", DecisionList);
        modelMap3.put("listMet", listMet);
        modelMap3.put("nbmet", nbmeth);
        modelMap3.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetCrit2Import")
    public String obtCrit2ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap4)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);
        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[1] = vecteurNormal;
        }

        modelMap4.put("vcoheren3", coherence);
        modelMap4.put("nbcrit", nombrecrit);
        modelMap4.put("nbdec", nombredec);
        modelMap4.put("listcrit", CritereList);
        modelMap4.put("listdec", DecisionList);
        modelMap4.put("listMet", listMet);
        modelMap4.put("nbmet", nbmeth);
        modelMap4.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetCrit3Import")
    public String obtCrit3ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap5)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);
        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[2] = vecteurNormal;
        }

        modelMap5.put("vcoheren4", coherence);
        modelMap5.put("nbcrit", nombrecrit);
        modelMap5.put("nbdec", nombredec);
        modelMap5.put("listcrit", CritereList);
        modelMap5.put("listdec", DecisionList);
        modelMap5.put("listMet", listMet);
        modelMap5.put("nbmet", nbmeth);
        modelMap5.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetCrit4Import")
    public String obtCrit4ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap6)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[3] = vecteurNormal;
        }

        modelMap6.put("vcoheren5", coherence);
        modelMap6.put("nbcrit", nombrecrit);
        modelMap6.put("nbdec", nombredec);
        modelMap6.put("listcrit", CritereList);
        modelMap6.put("listdec", DecisionList);
        modelMap6.put("listMet", listMet);
        modelMap6.put("nbmet", nbmeth);
        modelMap6.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetCrit5Import")
    public String obtCrit5ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap7)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);
        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[4] = vecteurNormal;
        }

        modelMap7.put("vcoheren6", coherence);
        modelMap7.put("nbcrit", nombrecrit);
        modelMap7.put("nbdec", nombredec);
        modelMap7.put("listcrit", CritereList);
        modelMap7.put("listdec", DecisionList);
        modelMap7.put("listMet", listMet);
        modelMap7.put("nbmet", nbmeth);
        modelMap7.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetCrit6Import")
    public String obtCrit6ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap8)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[5] = vecteurNormal;
        }

        modelMap8.put("vcoheren7", coherence);
        modelMap8.put("nbcrit", nombrecrit);
        modelMap8.put("nbdec", nombredec);
        modelMap8.put("listcrit", CritereList);
        modelMap8.put("listdec", DecisionList);
        modelMap8.put("listMet", listMet);
        modelMap8.put("nbmet", nbmeth);
        modelMap8.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetCrit7Import")
    public String obtCrit7ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap9)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[6] = vecteurNormal;
        }
        modelMap9.put("vcoheren8", coherence);
        modelMap9.put("nbcrit", nombrecrit);
        modelMap9.put("nbdec", nombredec);
        modelMap9.put("listcrit", CritereList);
        modelMap9.put("listdec", DecisionList);
        modelMap9.put("listMet", listMet);
        modelMap9.put("nbmet", nbmeth);
        modelMap9.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetCrit8Import")
    public String obtCrit8ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap10)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[7] = vecteurNormal;
        }

        modelMap10.put("vcoheren9", coherence);
        modelMap10.put("nbcrit", nombrecrit);
        modelMap10.put("nbdec", nombredec);
        modelMap10.put("listcrit", CritereList);
        modelMap10.put("listdec", DecisionList);
        modelMap10.put("listMet", listMet);
        modelMap10.put("nbmet", nbmeth);
        modelMap10.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetCrit9Import")
    public String obtCrit9ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap11)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[8] = vecteurNormal;
        }

        modelMap11.put("vcoheren10", coherence);
        modelMap11.put("nbcrit", nombrecrit);
        modelMap11.put("nbdec", nombredec);
        modelMap11.put("listcrit", CritereList);
        modelMap11.put("listdec", DecisionList);
        modelMap11.put("listMet", listMet);
        modelMap11.put("nbmet", nbmeth);
        modelMap11.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetCrit10Import")
    public String obtCrit10ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap12)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[9] = vecteurNormal;
        }

        modelMap12.put("vcoheren11", coherence);
        modelMap12.put("nbcrit", nombrecrit);
        modelMap12.put("nbdec", nombredec);
        modelMap12.put("listcrit", CritereList);
        modelMap12.put("listdec", DecisionList);
        modelMap12.put("listMet", listMet);
        modelMap12.put("nbmet", nbmeth);
        modelMap12.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec1Import")
    public String obtDec1ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit] = vecteurNormal;
        }

        modelMap.put("vanpcoheren1", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec2Import")
    public String obtDec2ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit + 1] = vecteurNormal;
        }

        modelMap.put("vanpcoheren2", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec3Import")
    public String obtDec3ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit + 2] = vecteurNormal;
        }

        modelMap.put("vanpcoheren3", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec4Import")
    public String obtDec4ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);
        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit + 3] = vecteurNormal;
        }

        modelMap.put("vanpcoheren4", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec5Import")
    public String obtDec5ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit + 4] = vecteurNormal;
        }

        modelMap.put("vanpcoheren5", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec6Import")
    public String obtDec6ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit + 5] = vecteurNormal;
        }

        modelMap.put("vanpcoheren6", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec7Import")
    public String obtDec7ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit + 6] = vecteurNormal;
        }

        modelMap.put("vanpcoheren7", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec8Import")
    public String obtDec8ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit + 7] = vecteurNormal;
        }

        modelMap.put("vanpcoheren8", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec9Import")
    public String obtDec9ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit + 8] = vecteurNormal;
        }

        modelMap.put("vanpcoheren9", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/anpgetDec10Import")
    public String obtDec10ANP(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableDec = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableDec);
        double[] vecteurNormal = ANP.vecteurNorm(tableauRempli);
        String coherence = ANP.verifCoherenc(tableauRempli, vecteurNormal);
        if (coherence == "True") {
            vectNormANP[nombrecrit + 9] = vecteurNormal;
        }

        modelMap.put("vanpcoheren10", coherence);
        modelMap.put("nbcrit", nombrecrit);
        modelMap.put("nbdec", nombredec);
        modelMap.put("listcrit", CritereList);
        modelMap.put("listdec", DecisionList);
        modelMap.put("listMet", listMet);
        modelMap.put("nbmet", nbmeth);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    // Calcul méthodes groupe 1

    // FUZZY AHP

    @PostMapping(value = "fuzahpgetResultImport")
    public String resultatFAHP(ModelMap modelMap2)
    {
        double[][] tableaufinal = null;

        if (nombrecrit == 2) {
            tableaufinal = AHP.tableauFinal(vectCritFuzzyAHP, criter1FuzzyAHP, criter2FuzzyAHP);

        }
        if (nombrecrit == 3) {
            tableaufinal = AHP.tableauFinal(vectCritFuzzyAHP,
                                            criter1FuzzyAHP,
                                            criter2FuzzyAHP,
                                            criter3FuzzyAHP);

        }
        if (nombrecrit == 4) {
            tableaufinal = AHP.tableauFinal(vectCritFuzzyAHP,
                                            criter1FuzzyAHP,
                                            criter2FuzzyAHP,
                                            criter3FuzzyAHP,
                                            criter4FuzzyAHP);
        }
        if (nombrecrit == 5) {
            tableaufinal = AHP.tableauFinal(vectCritFuzzyAHP,
                                            criter1FuzzyAHP,
                                            criter2FuzzyAHP,
                                            criter3FuzzyAHP,
                                            criter4FuzzyAHP,
                                            criter5FuzzyAHP);
        }
        if (nombrecrit == 6) {
            tableaufinal = AHP.tableauFinal(vectCritFuzzyAHP,
                                            criter1FuzzyAHP,
                                            criter2FuzzyAHP,
                                            criter3FuzzyAHP,
                                            criter4FuzzyAHP,
                                            criter5FuzzyAHP,
                                            criter6FuzzyAHP);
        }
        if (nombrecrit == 7) {
            tableaufinal = AHP.tableauFinal(vectCritFuzzyAHP,
                                            criter1FuzzyAHP,
                                            criter2FuzzyAHP,
                                            criter3FuzzyAHP,
                                            criter4FuzzyAHP,
                                            criter5FuzzyAHP,
                                            criter6FuzzyAHP,
                                            criter7FuzzyAHP);
        }
        if (nombrecrit == 8) {
            tableaufinal = AHP.tableauFinal(vectCritFuzzyAHP,
                                            criter1FuzzyAHP,
                                            criter2FuzzyAHP,
                                            criter3FuzzyAHP,
                                            criter4FuzzyAHP,
                                            criter5FuzzyAHP,
                                            criter6FuzzyAHP,
                                            criter7FuzzyAHP,
                                            criter8FuzzyAHP);
        }
        if (nombrecrit == 9) {
            tableaufinal = AHP.tableauFinal(vectCritFuzzyAHP,
                                            criter1FuzzyAHP,
                                            criter2FuzzyAHP,
                                            criter3FuzzyAHP,
                                            criter4FuzzyAHP,
                                            criter5FuzzyAHP,
                                            criter6FuzzyAHP,
                                            criter7FuzzyAHP,
                                            criter8FuzzyAHP,
                                            criter9FuzzyAHP);
        }
        if (nombrecrit == 10) {
            tableaufinal = AHP.tableauFinal(vectCritFuzzyAHP,
                                            criter1FuzzyAHP,
                                            criter2FuzzyAHP,
                                            criter3FuzzyAHP,
                                            criter4FuzzyAHP,
                                            criter5FuzzyAHP,
                                            criter6FuzzyAHP,
                                            criter7FuzzyAHP,
                                            criter8FuzzyAHP,
                                            criter9FuzzyAHP,
                                            criter10FuzzyAHP);
        }

        scoresFAHP = AHP.scores(tableaufinal);

        // COPIE DE SCORESFAHP POUR LE CALCUL DU RANG
        double[] copie = new double[scoresFAHP.length];
        for (int i = 0; i < scoresFAHP.length; i++) {
            copie[i] = scoresFAHP[i];
        }

        rankscoresFAHP = Ranking.rank(copie);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Importation";

    }

    // AHP

    @PostMapping(value = "getResultAHPImport")
    public String resultat(ModelMap modelMap2)
    {
        double[][] tableaufinal = null;

        if (nombrecrit == 2) {
            tableaufinal = AHP.tableauFinal(vectCritAHP, criter1AHP, criter2AHP);

        }
        if (nombrecrit == 3) {
            tableaufinal = AHP.tableauFinal(vectCritAHP, criter1AHP, criter2AHP, criter3AHP);

        }
        if (nombrecrit == 4) {
            tableaufinal =
                    AHP.tableauFinal(vectCritAHP, criter1AHP, criter2AHP, criter3AHP, criter4AHP);
        }
        if (nombrecrit == 5) {
            tableaufinal = AHP.tableauFinal(vectCritAHP,
                                            criter1AHP,
                                            criter2AHP,
                                            criter3AHP,
                                            criter4AHP,
                                            criter5AHP);
        }
        if (nombrecrit == 6) {
            tableaufinal = AHP.tableauFinal(vectCritAHP,
                                            criter1AHP,
                                            criter2AHP,
                                            criter3AHP,
                                            criter4AHP,
                                            criter5AHP,
                                            criter6AHP);
        }
        if (nombrecrit == 7) {
            tableaufinal = AHP.tableauFinal(vectCritAHP,
                                            criter1AHP,
                                            criter2AHP,
                                            criter3AHP,
                                            criter4AHP,
                                            criter5AHP,
                                            criter6AHP,
                                            criter7AHP);
        }
        if (nombrecrit == 8) {
            tableaufinal = AHP.tableauFinal(vectCritAHP,
                                            criter1AHP,
                                            criter2AHP,
                                            criter3AHP,
                                            criter4AHP,
                                            criter5AHP,
                                            criter6AHP,
                                            criter7AHP,
                                            criter8AHP);
        }
        if (nombrecrit == 9) {
            tableaufinal = AHP.tableauFinal(vectCritAHP,
                                            criter1AHP,
                                            criter2AHP,
                                            criter3AHP,
                                            criter4AHP,
                                            criter5AHP,
                                            criter6AHP,
                                            criter7AHP,
                                            criter8AHP,
                                            criter9AHP);
        }
        if (nombrecrit == 10) {
            tableaufinal = AHP.tableauFinal(vectCritAHP,
                                            criter1AHP,
                                            criter2AHP,
                                            criter3AHP,
                                            criter4AHP,
                                            criter5AHP,
                                            criter6AHP,
                                            criter7AHP,
                                            criter8AHP,
                                            criter9AHP,
                                            criter10AHP);
        }

        scoresAHP = AHP.scores(tableaufinal);

        // COPIE DE SCORESFAHP POUR LE CALCUL DU RANG
        double[] copie = new double[scoresAHP.length];
        for (int i = 0; i < scoresAHP.length; i++) {
            copie[i] = scoresAHP[i];
        }

        rankscoresAHP = Ranking.rank(copie);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Importation";

    }

    // FUZZY ANP

    @PostMapping(value = "/fuzAnpgetResultImport")
    public String resultatfanp(ModelMap modelMap2)
    {
        double[][] matrice = ANP.superMatrix(vectCritFANP, nombrecrit, nombredec, vectNormFANP);
        double[][] resultat = ANP.powerMatrix(matrice, 20);
        vectResultFANP = new double[nombredec];

        for (int i = 0; i < nombredec; i++) {
            vectResultFANP[i] = resultat[nombrecrit + 1 + i][0];
        }

        // COPIE DE SCORESFAHP POUR LE CALCUL DU RANG
        double[] copie = new double[vectResultFANP.length];
        for (int i = 0; i < vectResultFANP.length; i++) {
            copie[i] = vectResultFANP[i];
        }

        rankscoresFANP = Ranking.rank(copie);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Importation";

    }

    // ANP

    @PostMapping(value = "/anpgetResultImport")
    public String resultatanp(ModelMap modelMap2)
    {
        double[][] matrice = ANP.superMatrix(vecteurCritANP, nombrecrit, nombredec, vectNormANP);
        double[][] resultat = ANP.powerMatrix(matrice, 20);
        vectResultANP = new double[nombredec];

        for (int i = 0; i < nombredec; i++) {
            vectResultANP[i] = resultat[nombrecrit + 1 + i][0];
        }

        // COPIE DE SCORESFAHP POUR LE CALCUL DU RANG
        double[] copie = new double[vectResultANP.length];
        for (int i = 0; i < vectResultANP.length; i++) {
            copie[i] = vectResultANP[i];
        }
        rankscoresANP = Ranking.rank(copie);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Importation";

    }

    // RECOLTE DES PARAMETRES GROUPE 2

    @PostMapping(value = "/Methode2")
    public String obtMeth2(@RequestParam String[] met2, ModelMap modelMap2)
    {

        listMet = met2;
        nbmeth = listMet.length;

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nblevel", nblevelG2);

        modelMap2.put("nbdec", nombredec);

        return "Importation";
    }

    @PostMapping(value = "/Group2InfosImport")

    public String obtab(@RequestParam("nblevel") Integer nbLevel, ModelMap modelMap,
                        @RequestParam("nbdecideur") Integer nbDecideur)
    {

        nbdecideurG2 = nbDecideur;
        nblevelG2 = nbLevel;
        modelMap.put("nblevel", nbLevel);
        modelMap.put("nbDecideur", nbDecideur);
        modelMap.put("gp", gpp);

        return "Importation";

    }

    @PostMapping(value = "/fichMeth2Import")
    public String obtFuzzyTopFichier(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] matrice = csvHandler.csvToArray(chemin1, 1, 1);
        nombrecrit = matrice[0].length;
        nombredec = matrice.length / nbdecideurG2;
        CritereList = csvHandler.csvFirstRow(chemin1);
        DecisionList = csvHandler.csvFirstCol(chemin1);
        LaMatrice = matrice;
        infoCriterFT = new double[nombrecrit];
        poidsFuzTop = new double[nombrecrit];
        vecteurLevelFuzTop = new double[nblevelG2][3];

        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("table", matrice);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);

        return "Importation";
    }

    // FUZZY-TOPSIS

    @PostMapping(value = "/FuzzytopsisInfoImport")
    public String obtinfoFT(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        infoCriterFT = new double[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            if (value.equals("Min")) {
                infoCriterFT[i] = 0;
            } else {
                infoCriterFT[i] = 1;
            }

        }

        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);
        modelMap2.put("nblevel", nblevelG2);

        return "Importation";
    }

    @PostMapping(value = "/FuzzytopsisPoidsImport")
    public String obtpoidsFT(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        poidsfuzzyTop = new double[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            poidsfuzzyTop[i] = Double.parseDouble(value);

        }
        modelMap2.put("poids", Arrays.toString(poidsfuzzyTop));
        modelMap2.put("nbDecideur", nbdecideurG2);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);
        modelMap2.put("nblevel", nblevelG2);
        return "Importation";
    }

    @PostMapping(value = "/valeurFuzzyImport")
    public String obtfuz(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        for (int i = 0; i < nblevelG2; i++) {

            Integer numero = i + 1;
            String cell = numero.toString();
            double[] fuzzy = new double[3];
            String car = parameters.get(cell);
            String res = CalcInput.fuzzycalc(car);
            String vect[] = res.split(",");
            for (int k = 0; k < 3; k++) {
                fuzzy[k] = Double.parseDouble(vect[k]);
            }
            vecteurLevelFuzTop[i] = fuzzy;
        }

        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("gp", gpp);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("nblevel", nblevelG2);

        return "Importation";

    }

    // FUZZY AHP TOPSIS

    @PostMapping(value = "/FuzzyAhptopsisCritImport")
    public String obtinfoCritahpt(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        infoCriterFAHPT = new double[nombrecrit];
        poidsfinal = new double[nombrecrit];
        RCfinal = new double[nbdecideurG2];
        Dec = new double[nbdecideurG2][nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            if (value.equals("Min")) {
                infoCriterFAHPT[i] = 0;
            } else {
                infoCriterFAHPT[i] = 1;
            }

        }

        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nblevel", nblevelG2);

        return "Importation";
    }

    // Calcul du poids par Fuzzy-AHP multidécideur.

    @PostMapping(value = "/fuzAhptopGetTableCrit1Import")
    public String obtCritfuzahpcrit1(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException // Interface Map<K,V> Type Parameters: K - the
                               // type of keys maintained by this map V - the
                               // type of mapped values
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("fahptcoheren2", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[0] = poids1;
            RCfinal[0] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);

        modelMap2.put("RC", RC);
        modelMap2.put("poids", Arrays.toString(poids1));

        return "Importation";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit2Import")
    public String fuzahpcrit2(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException // Interface Map<K,V> Type Parameters: K - the
                               // type of keys maintained by this map V - the
                               // type of mapped values
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("fahptcoheren3", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[1] = poids1;
            RCfinal[1] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("RC2", RC);
        modelMap2.put("poids2", Arrays.toString(poids1));
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);

        return "Importation";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit3Import")
    public String fuzahpcrit3(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException // Interface Map<K,V> Type Parameters: K - the
                               // type of keys maintained by this map V - the
                               // type of mapped values
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("fahptcoheren4", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[2] = poids1;
            RCfinal[2] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);

        modelMap2.put("RC3", RC);
        modelMap2.put("poids3", Arrays.toString(poids1));

        return "Importation";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit4Import")
    public String fuzahpcrit4(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException // Interface Map<K,V> Type Parameters: K - the
                               // type of keys maintained by this map V - the
                               // type of mapped values
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("fahptcoheren5", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[3] = poids1;
            RCfinal[3] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("RC4", RC);
        modelMap2.put("poids4", Arrays.toString(poids1));

        return "Importation";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit5Import")
    public String fuzahpcrit5(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException // Interface Map<K,V> Type Parameters: K - the
                               // type of keys maintained by this map V - the
                               // type of mapped values
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("fahptcoheren6", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[4] = poids1;
            RCfinal[4] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);

        modelMap2.put("RC5", RC);
        modelMap2.put("poids5", Arrays.toString(poids1));

        return "Importation";
    }

    @PostMapping(value = "/fuzAhptopGetTableCrit6Import")
    public String fuzahpcrit6(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException // Interface Map<K,V> Type Parameters: K - the
                               // type of keys maintained by this map V - the
                               // type of mapped values
    {
        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][][] matrice = csvHandler.csvToArrayFuzzy(chemin1, 1, 1);
        // Maintenant qu'on a récupéré de l'interface utilisateur : on rempli la
        // matrice par symétrie et on calcule RI

        double[][][] matFuzzy = Fuzzification.remplirFuzzy(matrice);
        double[] poids1 = FuzzyAhpTopsis.FuzzCalcWeights(matrice);
        double[][] tabUn = FuzzyAhpTopsis.FuzAhptabUn(matFuzzy);
        double RC = FuzzyAhpTopsis.getRC(tabUn, poids1);

        String coherence = Fuzzification.verifCoherenc(tabUn, poids1);

        modelMap2.put("fahptcoheren7", coherence);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        if (coherence == "True") {
            Dec[5] = poids1;
            RCfinal[5] = RC;

        }
        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);

        modelMap2.put("RC6", RC);
        modelMap2.put("poids6", Arrays.toString(poids1));

        return "Importation";
    }

    @PostMapping(value = "/FusionPoidsImport")
    public String fusion(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        poidsfinal = FuzzyAhpTopsis.ConsistentWeights(RCfinal, Dec);
        modelMap2.put("poidsfinal", Arrays.toString(poidsfinal));
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        return "Importation";

    }

    // RECOLTE DES PARAMETRES GROUPE 3

    @PostMapping(value = "/Methode3")
    public String obtMeth3(@RequestParam String[] met3, ModelMap modelMap2)
    {
        // TEST OK

        listMet = met3;
        nbmeth = listMet.length;

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("minOrMaxs", minOrMaxs);
        modelMap2.put("weights", weights);
        modelMap2.put("prefFunctions", prefFunctions);
        modelMap2.put("prefThresholds", prefThresholds);
        modelMap2.put("indiffThresholds", indiffThresholds);

        return "Importation";
    }

    // AHP TOPSIS

    @PostMapping(value = "/ahptopsisInfoImport")
    public String obtinfo(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        infoCriterAHPTopsis = new double[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            if (value.equals("Min")) {
                infoCriterAHPTopsis[i] = 0;
            } else {
                infoCriterAHPTopsis[i] = 1;
            }

        }

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);

        return "Importation";
    }

    @PostMapping(value = "/ahptopsisPoidsImport")
    public String obtpoids(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        double[][] tableCrit = csvHandler.csvToArray(chemin1, 1, 1);

        double[][] tableauRempli = MatrixSym.remplir(tableCrit);
        double[] vecteurNormal = AHP.vecteurNorm(tableauRempli);
        String coherence = AHP.verifCoherenc(tableauRempli, vecteurNormal);
        modelMap2.put("coheren", coherence);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);

        if (coherence == "True") {
            vectCritAHPTOP = vecteurNormal;
        }
        modelMap2.put("poids", Arrays.toString(vectCritAHPTOP));

        return "Importation";

    }

    // ELECTRE POIDS

    @PostMapping(value = "/electrePoidsImport")
    public String obtpoidsElectre(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        // TEST OK

        poidsElectre = new double[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            poidsElectre[i] = Double.parseDouble(value);

        }

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("poids", Arrays.toString(poidsElectre));
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);

        return "Importation";
    }

    // TOPSIS

    @PostMapping(value = "/topsisInfoImport")
    public String obtinfoTopsis(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        infoCriterTopsis = new double[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            if (value.equals("Min")) {
                infoCriterTopsis[i] = 0;
            } else {
                infoCriterTopsis[i] = 1;
            }

        }
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);

        return "Importation";
    }

    @PostMapping(value = "/topsisPoidsImport")
    public String obtpoidstopsis(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        poidsTopsis = new double[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            poidsTopsis[i] = Double.parseDouble(value);

        }
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("poids", Arrays.toString(poidsTopsis));
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);
        return "Importation";
    }

    // PROMETHEE

    @PostMapping(value = "/PrometheeCritImport")
    public String obtinfoprom(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        infoCriterProm = new int[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            if (value.equals("Min")) {
                infoCriterProm[i] = 0;
            } else {
                infoCriterProm[i] = 1;
            }

        }

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);

        modelMap2.put("minOrMaxs", minOrMaxs);
        modelMap2.put("weights", weights);
        modelMap2.put("prefFunctions", prefFunctions);
        modelMap2.put("prefThresholds", prefThresholds);
        modelMap2.put("indiffThresholds", indiffThresholds);

        return "Importation";
    }

    @PostMapping(value = "/PrometheePoidsImport")
    public String obtpoidsprom(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        poidsPromethee = new double[nombrecrit];
        for (int i = 0; i < nombrecrit; i++) {
            Integer numero = i + 1;
            String cell = numero.toString();
            String value = parameters.get(cell);

            poidsPromethee[i] = Double.parseDouble(value);

        }
        modelMap2.put("poids", Arrays.toString(poidsPromethee));
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);

        modelMap2.put("minOrMaxs", minOrMaxs);
        modelMap2.put("weights", weights);
        modelMap2.put("prefFunctions", prefFunctions);
        modelMap2.put("prefThresholds", prefThresholds);
        modelMap2.put("indiffThresholds", indiffThresholds);

        return "Importation";
    }

    @PostMapping(value = "/PrometheePrefImport")
    // TEST OK
    public String obtpref(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        NumFctPref = new int[nombrecrit];
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

        modelMap2.put("NumPref", Arrays.toString(NumFctPref));

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);

        modelMap2.put("minOrMaxs", minOrMaxs);
        modelMap2.put("weights", weights);
        modelMap2.put("prefFunctions", prefFunctions);
        modelMap2.put("prefThresholds", prefThresholds);
        modelMap2.put("indiffThresholds", indiffThresholds);

        return "Importation";

    }

    @PostMapping(value = "/ValeurPrefImport")
    // TEST OK
    public String obtValeurPref(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        // ON récupère les infos : 1ere ligne : Q indifférence seuil
        // 2eme ligne : P préférence seuil
        // 3eme ligne : S

        listIndSeuil = new double[nombrecrit];
        listPrefSeuil = new double[nombrecrit];
        listS = new double[nombrecrit];
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

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);
        modelMap2.put("listInd", Arrays.toString(listIndSeuil));
        modelMap2.put("listPref", Arrays.toString(listPrefSeuil));
        modelMap2.put("listS", Arrays.toString(listS));

        modelMap2.put("minOrMaxs", minOrMaxs);
        modelMap2.put("weights", weights);
        modelMap2.put("prefFunctions", prefFunctions);
        modelMap2.put("prefThresholds", prefThresholds);
        modelMap2.put("indiffThresholds", indiffThresholds);

        return "Importation";
    }

    // CALCUL ELECTRE
    @PostMapping(value = "/ResultatElectreImport")
    public String obtResulta(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        resultatElectre = Electre2.electre(LaMatrice, poidsElectre);

        modelMap2.put("table", resultatElectre);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);
        return "Importation";
    }

    // Calcul Topsis
    @PostMapping(value = "/ResultatTopsisImport")
    public String obtResultaT(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        vectResultTop = Topsis.topmethod(LaMatrice, poidsTopsis, infoCriterTopsis);

        // COPIE DE VECTRESULT POUR LE CALCUL DU RANG
        double[] copie = new double[vectResultTop.length];
        for (int i = 0; i < vectResultTop.length; i++) {
            copie[i] = vectResultTop[i];
        }

        modelMap2.put("vectResult", vectResultTop);

        rankTopsis = Ranking.rank(copie);
        modelMap2.put("rang", rankTopsis);

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        return "Importation";
    }

    // Calcul Promethee

    @PostMapping(value = "/PromResultatImport")
    // TEST OK
    public String obtResultatP(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        double[][][] tabecar = PromMethod.calcTabEcart(LaMatrice);
        double[][][] tabpref = PromMethod.ApplyPref(tabecar,
                                                    infoCriterProm,
                                                    NumFctPref,
                                                    listPrefSeuil,
                                                    listIndSeuil,
                                                    listS);
        double[][] tabPi = PromMethod.MatPi(tabpref);
        double[][] tabFlux = PromMethod.CalcFlux(tabPi, poidsPromethee);
        FluxPositif = PromMethod.PosFlux(tabFlux);
        FluxNeg = PromMethod.NegFlux(tabFlux);
        FluxTotal = PromMethod.FluxTot(FluxNeg, FluxPositif);
        modelMap2.put("FlNeg", Arrays.toString(FluxNeg));
        modelMap2.put("FlPos", Arrays.toString(FluxPositif));
        modelMap2.put("FlTot", Arrays.toString(FluxTotal));
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);

        return "Importation";
    }

    // Calcul AHP Topsis

    @PostMapping(value = "/AHPTOPResImport")
    public String obtResultatAHPTP(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        vectResultAHPT = Topsis.topmethod(LaMatrice, vectCritAHPTOP, infoCriterAHPTopsis);
        // COPIE DE VECTRESULT POUR LE CALCUL DU RANG

        double[] copie = new double[vectResultAHPT.length];
        for (int i = 0; i < vectResultAHPT.length; i++) {
            copie[i] = vectResultAHPT[i];
        }

        modelMap2.put("vectResultAHPTop", vectResultAHPT);

        rankAHPTop = Ranking.rank(copie);
        modelMap2.put("rangAHPTop", rankAHPTop);

        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);

        return "Importation";
    }

    // Calcul Fuzzy Topsis

    @PostMapping(value = "/FuzzyTopResImport")
    public String obttable(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        vectResultFuzTop = FuzzyTopsis.FuzzyTopsisMethod(LaMatrice,
                                                         vecteurLevelFuzTop,
                                                         nbdecideurG2,
                                                         poidsfuzzyTop,
                                                         infoCriterFT);

        // COPIE DE VECTRESULT POUR LE CALCUL DU RANG
        double[] copie = new double[vectResultFuzTop.length];
        for (int i = 0; i < vectResultFuzTop.length; i++) {
            copie[i] = vectResultFuzTop[i];
        }

        modelMap2.put("vectResFuzTop", vectResultFuzTop);

        rankFuzTop = Ranking.rank(copie);

        modelMap2.put("rangFuzTop", rankFuzTop);
        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("gp", gpp);

        return "Importation";

    }

    // Calcul Fuzzy AHP Topsis

    @PostMapping(value = "/FuzAHPTopsisResImport")
    public String obtresAHPTOPFUz(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        vectResultFAHPT = FuzzyTopsis.FuzzyTopsisMethod(LaMatrice,
                                                        vecteurLevelFuzTop,
                                                        nbdecideurG2,
                                                        poidsfinal,
                                                        infoCriterFAHPT);

        // COPIE DE VECTRESULT POUR LE CALCUL DU RANG
        double[] copie = new double[vectResultFAHPT.length];
        for (int i = 0; i < vectResultFAHPT.length; i++) {
            copie[i] = vectResultFAHPT[i];
        }

        modelMap2.put("vectResult", vectResultFAHPT);

        rankFuzAhpTop = Ranking.rank(copie);
        modelMap2.put("rang", rankFuzAhpTop);

        modelMap2.put("nbDecideur", nbdecideurG2);
        modelMap2.put("vecteurLevel", vecteurLevelFuzTop);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "FuzzytopsisRes";

    }

    @PostMapping(value = "/ResultatFinalGp2")
    // TEST OK
    public String obtResultatFinalgp2(@RequestParam Map<String, String> parameters,
                                      ModelMap modelMap2)
    {

        modelMap2.put("vectResFuzTop", vectResultFuzTop);
        modelMap2.put("rangFuzTop", rankFuzTop);
        modelMap2.put("rangFuzAhpTop", rankFuzAhpTop);
        modelMap2.put("vectResFuzAhpTop", vectResultFAHPT);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Groupe2Res";
    }

    @PostMapping(value = "/ResultatFinalGp1")
    // TEST OK
    public String obtResultatFinalgp1(@RequestParam Map<String, String> parameters,
                                      ModelMap modelMap2)
    {

        modelMap2.put("rangscoresANP", rankscoresANP);
        modelMap2.put("rangscoresFANP", rankscoresFANP);
        modelMap2.put("rangscoresAHP", rankscoresAHP);
        modelMap2.put("rangscoresFAHP", rankscoresFAHP);
        modelMap2.put("scorFAHP", scoresFAHP);
        modelMap2.put("scorFANP", vectResultFANP);
        modelMap2.put("scorANP", vectResultANP);
        modelMap2.put("scorAHP", scoresAHP);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Groupe1Res";
    }

    @PostMapping(value = "/ResultatFinalGp3")
    // TEST OK
    public String obtResultatFinal(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {

        modelMap2.put("resElectre", resultatElectre);
        modelMap2.put("FluxPosit", FluxPositif);
        modelMap2.put("FluxNega", FluxNeg);
        modelMap2.put("FluxTot", FluxTotal);
        modelMap2.put("resTopsis", vectResultTop);
        modelMap2.put("rangTop", rankTopsis);
        modelMap2.put("rangAHPTop", rankAHPTop);
        modelMap2.put("resAHPTop", vectResultAHPT);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Groupe3Res";
    }

    @PostMapping(value = "/telechargement1")

    public String downloadpost(ModelMap modelMap2)
    {

        String userpath = System.getProperty("user.home");
        new File(userpath, "AMDdownload").mkdirs();

        File download = new File(userpath + "/AMDdownload",
                                 "ResGp1" + UUID.randomUUID().toString() + ".csv");

        try {
            download.createNewFile();
            String path = download.getAbsolutePath();
            FileWriter myWriter = new FileWriter(path, true);

            for (int i = 0; i < nbmeth; i++) {
                if (listMet[i].equals("1")) {
                    myWriter.write("Resultat Fuzzy AHP" + "\n");
                    myWriter.write(DecisionList + "\n");
                    myWriter.write(Arrays.toString(scoresFAHP) + "\n");
                    myWriter.write(Arrays.toString(rankscoresFAHP) + "\n");

                }

                if (listMet[i].equals("2")) {
                    myWriter.write("Resultat AHP" + "\n");
                    myWriter.write(DecisionList + "\n");
                    myWriter.write(Arrays.toString(scoresAHP) + "\n");
                    myWriter.write(Arrays.toString(rankscoresAHP) + "\n");

                }
                if (listMet[i].equals("3")) {
                    myWriter.write("Resultat Fuzzy ANP" + "\n");
                    myWriter.write(DecisionList + "\n");
                    myWriter.write(Arrays.toString(vectResultFANP) + "\n");
                    myWriter.write(Arrays.toString(rankscoresFANP) + "\n");

                }

                if (listMet[i].equals("4")) {
                    myWriter.write("Resultat ANP" + "\n");
                    myWriter.write(DecisionList + "\n");
                    myWriter.write(Arrays.toString(vectResultANP) + "\n");
                    myWriter.write(Arrays.toString(rankscoresANP) + "\n");

                }
            }

            myWriter.close();
            modelMap2.put("chemin", userpath);
            modelMap2.put("tel", 1);

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        modelMap2.put("rangscoresANP", rankscoresANP);
        modelMap2.put("rangscoresFANP", rankscoresFANP);
        modelMap2.put("rangscoresAHP", rankscoresAHP);
        modelMap2.put("rangscoresFAHP", rankscoresFAHP);
        modelMap2.put("scorFAHP", scoresFAHP);
        modelMap2.put("scorFANP", vectResultFANP);
        modelMap2.put("scorANP", vectResultANP);
        modelMap2.put("scorAHP", scoresAHP);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Groupe1Res";
    }

    @PostMapping(value = "/telechargement2")

    public String downloadpost2(ModelMap modelMap2)
    {

        String userpath = System.getProperty("user.home");
        new File(userpath, "AMDdownload").mkdirs();

        File download = new File(userpath + "/AMDdownload",
                                 "ResGp2" + UUID.randomUUID().toString() + ".csv");

        try {
            download.createNewFile();
            String path = download.getAbsolutePath();
            FileWriter myWriter = new FileWriter(path, true);

            for (int i = 0; i < nbmeth; i++) {
                if (listMet[i].equals("1")) {
                    myWriter.write("Resultat Fuzzy Topsis" + "\n");
                    myWriter.write(DecisionList + "\n");
                    myWriter.write(Arrays.toString(vectResultFuzTop) + "\n");
                    myWriter.write(Arrays.toString(rankFuzTop) + "\n");

                }

                if (listMet[i].equals("2")) {
                    myWriter.write("Resultat Fuzzy AHP Topsis" + "\n");
                    myWriter.write(DecisionList + "\n");
                    myWriter.write(Arrays.toString(vectResultFAHPT) + "\n");
                    myWriter.write(Arrays.toString(rankFuzAhpTop) + "\n");

                }
            }

            myWriter.close();
            modelMap2.put("chemin", userpath);
            modelMap2.put("tel", 1);

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        modelMap2.put("vectResFuzTop", vectResultFuzTop);
        modelMap2.put("rangFuzTop", rankFuzTop);
        modelMap2.put("rangFuzAhpTop", rankFuzAhpTop);
        modelMap2.put("vectResFuzAhpTop", vectResultFAHPT);
        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Groupe2Res";
    }

    @PostMapping(value = "/telechargement3")

    public String downloadpost3(ModelMap modelMap2)
    {

        String userpath = System.getProperty("user.home");
        new File(userpath, "AMDdownload").mkdirs();

        File download = new File(userpath + "/AMDdownload",
                                 "ResGp3" + UUID.randomUUID().toString() + ".csv");

        try {
            download.createNewFile();
            String path = download.getAbsolutePath();
            FileWriter myWriter = new FileWriter(path, true);

            for (int i = 0; i < nbmeth; i++) {
                if (listMet[i].equals("1")) {
                    myWriter.write("Resultat AHP Topsis" + "\n");
                    myWriter.write(DecisionList + "\n");
                    myWriter.write(Arrays.toString(vectResultAHPT) + "\n");
                    myWriter.write(Arrays.toString(rankAHPTop) + "\n");

                }

                if (listMet[i].equals("2")) {
                    myWriter.write("Resultat Electre" + "\n");
                    myWriter.write(DecisionList + "\n");
                    for (int[] r : resultatElectre) {
                        myWriter.write(Arrays.toString(r) + "\n");
                    }

                }
                if (listMet[i].equals("3")) {
                    myWriter.write("Resultat Topsis" + "\n");
                    myWriter.write(DecisionList + "\n");
                    myWriter.write(Arrays.toString(vectResultTop) + "\n");
                    myWriter.write(Arrays.toString(rankTopsis) + "\n");

                }

                if (listMet[i].equals("4")) {
                    myWriter.write("Resultat Promethee" + "\n");
                    myWriter.write("Decisions," + DecisionList + "\n");
                    myWriter.write("FluxPositif," + Arrays.toString(FluxPositif) + "\n");
                    myWriter.write("FluxNegatif," + Arrays.toString(FluxNeg) + "\n");
                    myWriter.write("FluxTotal," + Arrays.toString(FluxTotal) + "\n");

                }
            }

            myWriter.close();
            modelMap2.put("chemin", userpath);
            modelMap2.put("tel", 1);

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        modelMap2.put("resElectre", resultatElectre);
        modelMap2.put("FluxPosit", FluxPositif);
        modelMap2.put("FluxNega", FluxNeg);
        modelMap2.put("FluxTot", FluxTotal);
        modelMap2.put("resTopsis", vectResultTop);
        modelMap2.put("rangTop", rankTopsis);
        modelMap2.put("rangAHPTop", rankAHPTop);
        modelMap2.put("resAHPTop", vectResultAHPT);

        modelMap2.put("listMet", listMet);
        modelMap2.put("nbmet", nbmeth);
        modelMap2.put("gp", gpp);
        modelMap2.put("nbcrit", nombrecrit);
        modelMap2.put("nbdec", nombredec);
        modelMap2.put("listcrit", CritereList);
        modelMap2.put("listdec", DecisionList);

        return "Groupe3Res";
    }

    public StorageService getStorageService()
    {
        return storageService;
    }
}
