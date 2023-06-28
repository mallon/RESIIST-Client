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

import com.axellience.resiist.client.decisionmaking.amd3.km.basicfunction.FoncEvolution;
import com.axellience.resiist.client.decisionmaking.amd3.km.csvhandler.csvHandler;
import com.axellience.resiist.client.decisionmaking.amd3.storage.StorageService;

@Controller
public class FoncEvoluController
{

    private final StorageService storageService;

    @Autowired
    public FoncEvoluController(StorageService storageService)
    {
        this.storageService = storageService;
    }

    // INFOS

    int numFonc;
    int nombre;

    double[] vecteur;

    double[] Result;

    // Param
    double var;
    double esp;
    double lambda;
    double eche;
    double form;
    int    bornInf;
    int    bornSup;
    double alin;
    double blin;
    double aexpo;
    double cexpo;
    double alog;
    double clog;

    int oui = 0;

    @GetMapping("foncEvolu")
    public String getForm()
    {
        return "FoncEvolu";
    }

    @PostMapping("/SelectFEvolu")
    public String saveDetails(@RequestParam("numFonc") Integer numero, ModelMap modelMap)
    {
        // write your code to save details

        numFonc = numero;

        modelMap.put("nb", numero);
        return "FoncEvolu";
    }

    @PostMapping(value = "/NbVariable")
    public String nombre(@RequestParam("nombrevar") Integer var, ModelMap modelMap2)
            throws IOException
    {

        nombre = var;

        modelMap2.put("nb", numFonc);
        return "FoncEvolu";
    }

    @PostMapping(value = "/VectVariable")
    public String obtinfo(@RequestParam("file") MultipartFile file, ModelMap modelMap2)
            throws IOException
    {

        Path chemin = storageService.store(file);
        String chemin1 = chemin.toString();
        vecteur = csvHandler.csvToVect(chemin1);

        modelMap2.put("nb", numFonc);
        return "FoncEvolu";
    }

    @PostMapping(value = "/LoiNormale")
    public String Loinormale(@RequestParam("esperance") Double es,
                             @RequestParam("variance") Double va, ModelMap modelMap2)
            throws IOException
    {
        var = va;
        esp = es;
        modelMap2.put("nb", numFonc);

        return "FoncEvolu";
    }

    @PostMapping(value = "/LoiWeibull")
    public String Loiwei(@RequestParam("echelle") Double ech, @RequestParam("forme") Double forme,
                         ModelMap modelMap2)
            throws IOException
    {
        form = forme;
        eche = ech;
        modelMap2.put("nb", numFonc);

        return "FoncEvolu";
    }

    @PostMapping(value = "/LoiPoisson")
    public String Loipoi(@RequestParam("lambda") Double lam, ModelMap modelMap2) throws IOException
    {
        lambda = lam;
        modelMap2.put("nb", numFonc);

        return "FoncEvolu";
    }

    @PostMapping(value = "/Intervalle")
    public String Intervall(@RequestParam("inf") Integer inf, @RequestParam("sup") Integer sup,
                            ModelMap modelMap2)
            throws IOException
    {
        bornInf = inf;
        bornSup = sup;

        modelMap2.put("nb", numFonc);
        return "FoncEvolu";
    }

    @PostMapping(value = "/Lineaire")
    public String Lineair(@RequestParam("a") Double a, @RequestParam("b") Double b,
                          ModelMap modelMap2)
            throws IOException
    {

        alin = a;
        blin = b;

        modelMap2.put("nb", numFonc);
        return "FoncEvolu";
    }

    @PostMapping(value = "/exponentiel")
    public String expo(@RequestParam("a") Double a, @RequestParam("c") Double c, ModelMap modelMap2)
            throws IOException
    {

        aexpo = a;
        cexpo = c;

        modelMap2.put("nb", numFonc);
        return "FoncEvolu";
    }

    @PostMapping(value = "/logarithm")
    public String loga(@RequestParam("a") Double a, @RequestParam("c") Double c, ModelMap modelMap2)
            throws IOException
    {

        alog = a;
        clog = c;

        modelMap2.put("nb", numFonc);
        return "FoncEvolu";
    }

    @PostMapping(value = "/calculEvolu")
    public String calc(@RequestParam Map<String, String> parameters, ModelMap modelMap2)
    {
        int taille = 0;
        if (numFonc == 1) {
            Result = new double[nombre];
            for (int i = 0; i < nombre; i++) {
                Result[i] = FoncEvolution.FE1(esp, var);
            }

            oui = 1;
            taille = Result.length;
        }

        if (numFonc == 2) {
            Result = new double[nombre];
            for (int i = 0; i < nombre; i++) {
                Result[i] = FoncEvolution.FE2(eche, form);
            }
            oui = 1;
            taille = Result.length;
        }

        if (numFonc == 3) {
            Result = new double[nombre];
            for (int i = 0; i < nombre; i++) {
                Result[i] = FoncEvolution.FE3(lambda);
            }
            oui = 1;
            taille = Result.length;
        }
        if (numFonc == 4) {
            Result = new double[nombre];
            for (int i = 0; i < nombre; i++) {
                Result[i] = FoncEvolution.FE5(true, 0, 0);
            }
            oui = 1;
            taille = Result.length;
        }
        if (numFonc == 5) {
            Result = new double[nombre];
            for (int i = 0; i < nombre; i++) {
                Result[i] = FoncEvolution.FE5(false, bornInf, bornSup);
            }
            oui = 1;
            taille = Result.length;
        }
        if (numFonc == 6) {
            Result = new double[vecteur.length];
            for (int i = 0; i < vecteur.length; i++) {
                Result[i] = FoncEvolution.FE7(alin, blin, vecteur[i]);
            }

            oui = 1;
            taille = Result.length;
        }
        if (numFonc == 7) {

            Result = new double[vecteur.length];
            for (int i = 0; i < vecteur.length; i++) {
                Result[i] = FoncEvolution.FE8(aexpo, cexpo, vecteur[i]);
            }
            oui = 1;
            taille = Result.length;
        }
        if (numFonc == 8) {

            Result = new double[vecteur.length];
            for (int i = 0; i < vecteur.length; i++) {
                Result[i] = FoncEvolution.FE9(alog, clog, vecteur[i]);

            }
            taille = Result.length;
            oui = 1;
        }

        modelMap2.put("tai", taille);
        modelMap2.put("oui", oui);

        modelMap2.put("resultat", Result);

        modelMap2.put("nb", numFonc);

        return "FoncEvolu";
    }

}
