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

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncNormalisation;
import com.axellience.resiist.client.decisionmaking.amd3.km.csvhandler.csvHandler;
import com.axellience.resiist.client.decisionmaking.amd3.storage.StorageService;

@Controller
public class FoncNormController
{

    private final StorageService storageService;

    @Autowired
    public FoncNormController(StorageService storageService)
    {
        this.storageService = storageService;
    }

    // INFOS

    int        numFonc;
    double[][] tableau;
    double[][] tableauNorm;
    double[]   target;
    int        nbligne;
    int        nbcol;

    @GetMapping("foncNorm")
    public String getForm()
    {
        return "FoncNorm";
    }

    @PostMapping("/SelectFNorm")
    public String saveDetails(@RequestParam("numFonc") Integer numero, ModelMap modelMap)
    {
        // write your code to save details

        numFonc = numero;

        modelMap.put("nb", numero);
        return "FoncNorm";
    }

    @PostMapping(value = "/ImporTab4Norm")
    public String obtinfo(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        tableau = csvHandler.csvToArray(chemin1, 0, 0);

        modelMap2.put("nb", numFonc);

        return "FoncNorm";
    }

    @PostMapping(value = "/ImporTargetNorm")
    public String obtTarget(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        target = csvHandler.csvToVect(chemin1);

        modelMap2.put("nb", numFonc);

        return "FoncNorm";
    }

    @PostMapping(value = "/calculNorm")
    public String calc(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        if (numFonc == 1) {

            tableauNorm = FoncNormalisation.maxNormalization(tableau);

        }
        if (numFonc == 2) {

            tableauNorm = FoncNormalisation.minmaxNormalization(tableau);
        }

        if (numFonc == 3) {

            tableauNorm = FoncNormalisation.sumNormalization(tableau);

        }
        if (numFonc == 4) {
            tableauNorm = FoncNormalisation.vectorNormalization(tableau);

        }
        if (numFonc == 5) {
            tableauNorm = FoncNormalisation.targetBasedNormalization(tableau, target);

        }
        if (numFonc == 6) {
            tableauNorm = FoncNormalisation.logNormalization(tableau);

        }

        nbcol = tableauNorm[0].length;
        nbligne = tableauNorm.length;
        modelMap2.put("nbCol", nbcol);
        modelMap2.put("nbl", nbligne);
        modelMap2.put("nb", numFonc);
        modelMap2.put("tableauRes", tableauNorm);
        return "FoncNorm";
    }

    @PostMapping(value = "/telechargementNorm")

    public String downloadpost3(ModelMap modelMap2)
    {

        String userpath = System.getProperty("user.home");
        new File(userpath, "AMDdownload").mkdirs();

        File download = new File(userpath + "/AMDdownload",
                                 "ResNorm" + UUID.randomUUID().toString() + ".csv");

        try {
            download.createNewFile();
            String path = download.getAbsolutePath();
            FileWriter myWriter = new FileWriter(path, true);

            myWriter.write("Resultat Tableau Normalise" + "\n");

            for (double[] r : tableauNorm) {
                myWriter.write(Arrays.toString(r) + "\n");
            }
            myWriter.close();
            modelMap2.put("chemin", userpath);
            modelMap2.put("tel", 1);

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        modelMap2.put("nbCol", nbcol);
        modelMap2.put("nbl", nbligne);
        modelMap2.put("nb", numFonc);
        modelMap2.put("tableauRes", tableauNorm);

        return "FoncNorm";
    }

}
