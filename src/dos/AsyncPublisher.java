package dos;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class AsyncPublisher{
	private static int NUM_THREADS = 300;
	
	private static int DELAY_THREADS = 30;
	
	private static int DELAY_PUBLISH = 6000;
	
	private static String BROKER_URL = "tcp://mqtt.eclipse.org:1883";
	
	private int myId = 0; 
	
	private String clientId;
	
	private static String TOPIC = "/prova/cert";
	
	private static MqttAsyncClient mqttClient;
	
	public AsyncPublisher(int i) {
		try {
			myId = i;
			clientId = MqttAsyncClient.generateClientId();
					// i + Long.toString(new Date().getTime()) + "pub";
			mqttClient = new MqttAsyncClient(BROKER_URL, clientId);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void run() {
		try {
			MqttConnectOptions option = new MqttConnectOptions();
            // option.setUserName("sonor");
            // option.setPassword("sono".toCharArray());
			
			// Imposta il fatto che i messaggi pubblicati da questo publisher non vadano recuperati da un subscriber
			// che si connette dopo l'inizio della trasmissione
			//option.setCleanSession(true);
			
			// Imposta il suo comportamento in casi particolari
			// option.setWill("retilab/LWT", "I'm gone".getBytes(), 0 , false);
			
			mqttClient.connect(option);
			Thread.sleep(1000);
			
			while(true) {
				publishTime(myId);
				
				Thread.sleep(DELAY_PUBLISH);
			}
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);			
		}
	}
	
	private synchronized void publishTime(int num) throws MqttException {
		MqttMessage msg = new MqttMessage();
		final String message = "DosPub numero: " + num + " " + Long.toString(new Date().getTime());
		
		msg.setPayload(message.getBytes());
		
		
		mqttClient.publish(TOPIC, msg);
		
		System.out.println("Published by DosPub numero " + num + " on topic: " + TOPIC + " Message: " + message);
	}

	public static void main(String args[]) throws InterruptedException {
		ArrayList<AsyncPublisher> listPub = new ArrayList<AsyncPublisher>();
		
		for(int i = 1; i <= NUM_THREADS; i++) {
			listPub.add(new AsyncPublisher(i));
			
			listPub.get(listPub.size()-1).run();
			
			Thread.sleep(DELAY_THREADS);
		}
		
		System.out.println("Fine");
	}
}
