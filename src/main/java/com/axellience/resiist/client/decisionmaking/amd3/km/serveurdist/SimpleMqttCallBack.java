package com.axellience.resiist.client.decisionmaking.amd3.km.serveurdist;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SimpleMqttCallBack implements MqttCallback
{
    /*Map<String,String> myMap = new HashMap<String,String>(); 
    
    LocalDateTime time = LocalDateTime.now(); 
    static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    String formattedDateTime = time.format(formatter);*/

    String path;

    public void connectionLost(Throwable throwable)
    {
        System.out.println("Connection to MQTT broker lost!");
    }

    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception
    {

        System.out.println("Message received:\t" + new String(mqttMessage.getPayload()));

        String mess = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
        try {

            FileWriter myWriter = new FileWriter(path, true);

            myWriter.write(mess + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            System.out.println("Writer file: " + path);

        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
    {
        try {
            iMqttDeliveryToken.getMessage();
        }
        catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
