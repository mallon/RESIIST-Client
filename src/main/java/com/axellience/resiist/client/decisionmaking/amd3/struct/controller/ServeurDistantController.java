package com.axellience.resiist.client.decisionmaking.amd3.struct.controller;

import java.nio.file.Path;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.axellience.resiist.client.decisionmaking.amd3.km.serveurdist.Subscriber;
import com.axellience.resiist.client.decisionmaking.amd3.storage.StorageService;

@Controller
public class ServeurDistantController
{

    private final StorageService storageService;

    @Autowired
    public ServeurDistantController(StorageService storageService)
    {
        this.storageService = storageService;
    }

    // SAVE INFORMATIONS

    String topicname;
    String pathname;

    @GetMapping("/exterieur")
    public String getForm()
    {
        return "Ext";
    }

    @PostMapping("/TopicName")
    public String savetopic(@RequestParam("Topic") String topic, ModelMap modelMap)
            throws MqttException
    {
        // write your code to save details

        topicname = topic;
        modelMap.put("topic", topic);
        modelMap.put("topicLength", topic.length());

        return "Ext";
    }

    @PostMapping("/FileName")
    public String savefileNAme(@RequestParam("file") MultipartFile file, ModelMap modelMap)
            throws MqttException
    {
        // write your code to save details

        Path chemin = storageService.store(file);
        String path = chemin.toString();
        Subscriber.main(path, topicname);

        return "Ext";
    }

}
