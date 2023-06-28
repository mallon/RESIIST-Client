package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncAgregation;
import com.axellience.resiist.client.decisionmaking.amd3.km.csvhandler.csvHandler;
import com.axellience.resiist.client.decisionmaking.amd3.storage.StorageService;

@Controller
public class FoncAgreController
{

    private final StorageService storageService;

    @Autowired
    public FoncAgreController(StorageService storageService)
    {
        this.storageService = storageService;
    }

    // INFOS

    int        numFonc;
    double[]   vecteur;
    double[][] tableau;
    double[][] TabAgreg;
    double[]   vectAgreg;
    double     Result;
    double[][] target;
    int        nbligne;
    int        nbcol;
    int        oui = 0;

    @GetMapping("foncAgre")
    public String getForm()
    {
        return "FoncAgre";
    }

    @PostMapping("/SelectFAgreg")
    public String saveDetails(@RequestParam("numFonc") Integer numero, ModelMap modelMap)
    {
        // write your code to save details

        numFonc = numero;

        modelMap.put("nb", numero);
        return "FoncAgre";
    }

    @PostMapping(value = "/ImporVect4Agreg")
    public String obtinfo(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String path = chemin.toString();
        vecteur = csvHandler.csvToVect(path);

        modelMap2.put("nb", numFonc);

        return "FoncAgre";
    }

    @PostMapping(value = "/ImporTab4Agreg")
    public String obtitab(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String path = chemin.toString();
        tableau = csvHandler.csvToArray(path, 1, 0);

        modelMap2.put("nb", numFonc);

        return "FoncAgre";
    }

    @PostMapping(value = "/calculAgreg")
    public String calc(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        int taille = 0;
        if (numFonc == 1) {

            Result = FoncAgregation.sommeVect(vecteur);
            oui = 1;
        }
        if (numFonc == 2) {

            Result = FoncAgregation.averageVect(vecteur);
            oui = 1;
        }

        if (numFonc == 3) {

            Result = FoncAgregation.medianVect(vecteur);
            oui = 1;
        }
        if (numFonc == 4) {
            Result = FoncAgregation.minRowVect(vecteur);
            oui = 1;
        }
        if (numFonc == 5) {
            Result = FoncAgregation.maxRowVect(vecteur);
            oui = 1;
        }
        if (numFonc == 6) {
            Result = FoncAgregation.produitVect(vecteur);
            oui = 1;
        }
        if (numFonc == 7) {

            Result = FoncAgregation.incAverageVect(vecteur);
            oui = 1;
        }
        if (numFonc == 8) {

            Result = FoncAgregation.decAverageVect(vecteur);
            oui = 1;
        }

        if (numFonc == 9) {

            Result = FoncAgregation.incMaxVect(vecteur);
            oui = 1;
        }
        if (numFonc == 10) {
            Result = FoncAgregation.decMaxVect(vecteur);
            oui = 1;
        }
        if (numFonc == 11) {
            Result = FoncAgregation.incMinVect(vecteur);
            oui = 1;
        }
        if (numFonc == 12) {
            Result = FoncAgregation.decMinVect(vecteur);
            oui = 1;
        }

        if (numFonc == 13) {
            vectAgreg = FoncAgregation.sumPond(tableau);
            oui = 1;
            taille = vectAgreg.length;
        }
        if (numFonc == 14) {
            vectAgreg = FoncAgregation.prodPond1(tableau);
            oui = 1;
            taille = vectAgreg.length;
        }
        if (numFonc == 15) {
            TabAgreg = FoncAgregation.prodPond2(tableau);
            oui = 1;

            nbcol = TabAgreg[0].length;
            nbligne = TabAgreg.length;
        }

        modelMap2.put("tai", taille);
        modelMap2.put("oui", oui);
        modelMap2.put("vectRes", vectAgreg);
        modelMap2.put("resultat", Result);
        modelMap2.put("nbCol", nbcol);
        modelMap2.put("nbl", nbligne);
        modelMap2.put("nb", numFonc);
        modelMap2.put("tableauRes", TabAgreg);
        return "FoncAgre";
    }

}
