package dos;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Subscriber implements Runnable{
	// public static int DELAY_THREADS = 10;

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

	public int counter = 0;

	private Object lock;


	public Subscriber(Object lock, int i) {
		if(lock == null) {
			this.lock = new Object();
		} else {
			this.lock = lock;
		}

		try {
			synchronized(this.lock) {
				counter = i;
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
			mqttClient.setCallback(new SubscribeCallback());
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
