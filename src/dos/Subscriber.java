package dos;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Subscriber extends Thread {
	private static int NUM_THREADS = 300;

	// Url del brocker
	public static final String BROKER_URL = "tcp://mqtt.eclipse.org:1883";

	// Id del client generato dinamicamente ottenendo il numero di secondi
	public String clientId = null;

	// Il topic a cui sottoscrivere
	private static final String TOPIC = "dos/dosMult";

	// client mqtt
	private MqttClient mqttClient;

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
				clientId = Long.toString(new Date().getTime()) + "-sub";
				// crea un nuovo MqttCLient con l'url del brocker e l'id del client
				mqttClient = new MqttClient(BROKER_URL, clientId);
			}
		} catch(MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Subscriber(int i) {
		this(null, i); }


	@Override
	public void run() {
		try {
			// Imposta le callback
			mqttClient.setCallback(new SubscribeCallback(lock));
			// Si connette al broker
			mqttClient.connect();

			// Sottoscrive al topic desiderato
			mqttClient.subscribe(TOPIC);

			System.out.println("Now subscriber " + counter + " listening on topic: " + TOPIC);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String args[]) throws InterruptedException {
		ArrayList<Subscriber> listSub = new ArrayList<Subscriber>();
		for(int i = 1; i <= NUM_THREADS; i++) {
			listSub.add(new Subscriber(listSub, i));
			listSub.get(listSub.size()-1).run();
		}

		System.out.println("Fine");
	}

	// Provare uno con tanti e tanti con tanti

}
