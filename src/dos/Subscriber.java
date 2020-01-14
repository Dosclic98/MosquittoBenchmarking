package dos;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import dos.tester.Tester;

public class Subscriber implements Runnable{
	
	public int count = 0;
	
	// Url del brocker
	// public static final String BROKER_URL = "tcp://mqtt.eclipse.org:1883";
	private static String BROKER_URL = "tcp://localhost:1883";

	// Id del client generato dinamicamente ottenendo il numero di secondi
	public String clientId = null;

	// Il topic a cui sottoscrivere
	
	public static int qos = 0;

	// client mqtt
	public MqttClient mqttClient;

	private Object lock;
	private Object lock2;
	int myId = 0;


	public Subscriber(Object lock, Object lock2, int i) {
		this.lock = lock;	
		this.lock2 = lock2;
		myId = i;

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
			mqttClient.subscribe(Tester.topics[(myId-1) % Tester.topics.length], Subscriber.qos);

			System.out.println("Subscriber " + mqttClient.getClientId() + " listening on topic: " + Tester.topics[(myId-1) % Tester.topics.length]);
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
