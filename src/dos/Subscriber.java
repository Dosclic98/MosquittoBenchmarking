package dos;

import java.util.HashSet;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Subscriber implements Runnable{
	
	public int count = 0;
	
	public static volatile HashSet<String> countedMsgs = new HashSet<String>();
	
	// Url del brocker
	// public static final String BROKER_URL = "tcp://mqtt.eclipse.org:1883";
	private static String BROKER_URL = "tcp://localhost:1883";

	// Id del client generato dinamicamente ottenendo il numero di secondi
	public String clientId = null;

	// Il topic a cui sottoscrivere
	private static final String TOPIC = "dos/Bench";
	
	private static int qos = 0;

	// client mqtt
	public MqttClient mqttClient;

	private Object lock;
	private Object lock2;


	public Subscriber(Object lock, Object lock2, int i) {
		this.lock = lock;	
		this.lock2 = lock2;
		

		try {
			synchronized(this.lock) {
				clientId = MqttClient.generateClientId() + "-Sub";
				mqttClient = new MqttClient(BROKER_URL, clientId);
			}
		} catch(MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


	public void run() {
		try {
			mqttClient.setCallback(new SubscribeCallback(this, lock2));
			mqttClient.connect();
			mqttClient.subscribe(TOPIC, Subscriber.qos);

			System.out.println("Subscriber " + mqttClient.getClientId() + " listening on topic: " + TOPIC);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} 
	}
	
	public void terminate() throws MqttException {
		
		if(mqttClient.isConnected()) {
			mqttClient.disconnect();				
		}
		mqttClient.close();
		System.out.println(mqttClient.getClientId() + " disconnected");
	}

}
