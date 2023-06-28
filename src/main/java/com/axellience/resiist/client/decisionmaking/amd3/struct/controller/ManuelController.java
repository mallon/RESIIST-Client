package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ManuelController
{

    @GetMapping("/manuel")
    public String getForm(ModelMap modelMap)
    {

        return "Manuel";
    }

    @PostMapping(value = "/GroupMetManuel")
    public String InfosFuzAhpTopsis(ModelMap modelMap, @RequestParam("groupMetman") String meth)
    {

        return meth;

    }

}
