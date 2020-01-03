package dos;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Subscriber extends Thread {
	private static int NUM_THREADS = 300;
	
	private static int DELAY_THREADS = 50;
	
	// Url del brocker
	public static final String BROKER_URL = "tcp://localhost:1883";
	
	// Id del client generato dinamicamente ottenendo il numero di secondi
	public String clientId = null;
	
	// Il topic a cui sottoscrivere
	private static final String TOPIC = "dos/dosMult";
	
	// client mqtt
	private MqttClient mqttClient;

	public int counter = 0;
	
	public Subscriber(int i) {
		try {
			counter = i;
			clientId = Long.toString(new Date().getTime()) + "-sub";
			// crea un nuovo MqttCLient con l'url del brocker e l'id del client
			mqttClient = new MqttClient(BROKER_URL, clientId);
		} catch(MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} 
	}
	
	public void run() {
		try {
			// Imposta le callback
			mqttClient.setCallback(new SubscribeCallback());
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
			listSub.add(new Subscriber(i));
			listSub.get(listSub.size()-1).run();
			
			Thread.sleep(DELAY_THREADS);
		}
		
		System.out.println("Fine");
	}
	
	// Provare uno con tanti e tanti con tanti
	
}
