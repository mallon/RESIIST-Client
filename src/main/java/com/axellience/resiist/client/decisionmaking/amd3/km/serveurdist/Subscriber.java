package com.axellience.resiist.client.decisionmaking.amd3.km.serveurdist;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Subscriber
{

    public static void main(String chemin, String topic) throws MqttException
    {

        System.out.println("== START SUBSCRIBER ==");
        /*String clientId = "Client1"; 
        
        
        
        //Store messages until server fetches them
        
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence("C:\\Users\\kinta\\Documents\\StageCerema" + "\\" + clientId);
        
        MqttClient client=new MqttClient("tcp://test.mosquitto.org:1883", MqttClient.generateClientId(),dataStore);
        client.setCallback( new SimpleMqttCallBack() );
        client.connect();
        
        client.subscribe("iot_data");*/

        MqttClient client =
                new MqttClient("tcp://test.mosquitto.org:1883", MqttClient.generateClientId());
        SimpleMqttCallBack call = new SimpleMqttCallBack();
        call.path = chemin;
        client.setCallback(call);
        // client.setCallback(new SimpleMqttCallBack());
        client.connect();

        client.subscribe(topic);

    }

}