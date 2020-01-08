package dos;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Subscriber{
	public static int NUM_THREADS = 1;
	public static int DELAY_THREADS = 10;

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
			mqttClient.setCallback(new SubscribeCallback(lock));
			mqttClient.connect();
			mqttClient.subscribe(TOPIC, Subscriber.qos);

			System.out.println("Subscriber " + mqttClient.getClientId() + " listening on topic: " + TOPIC);
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} 
	}

	public static void main(String args[]) throws InterruptedException, MqttException {
		String fileName = new java.io.File(Publisher.class.getProtectionDomain()
				  .getCodeSource()
				  .getLocation()
				  .getPath())
				.getName();

		if(args.length != 2) {
			System.out.println("Prendo --> java -jar " + fileName + " <num_sub> <qos>");
		} else {
			try {
				int numSub = Integer.parseInt(args[0]);
				int qos = Integer.parseInt(args[1]);
				if(numSub < 1) {
					System.out.println("Numero subscriber non valido");
				} else {
					if(qos < 0 || qos > 2) {
						System.out.println("QOS non valido");
					} else {
						NUM_THREADS = numSub;
						Subscriber.qos = qos;
						
						ArrayList<Subscriber> listSub = new ArrayList<Subscriber>();
						for(int i = 1; i <= NUM_THREADS; i++) {
							listSub.add(new Subscriber(listSub, i));
							listSub.get(listSub.size()-1).run();
							Thread.sleep(DELAY_THREADS);
						}
						System.out.println("Partial end");
						synchronized(listSub) {
							listSub.wait();
						}
						for(Subscriber sub : listSub) {
							if(sub.mqttClient.isConnected()) {
								sub.mqttClient.disconnect();
							}
							sub.mqttClient.close();
						}
						
						System.out.println("Fine");		
					}
				}
			} catch(NumberFormatException e) {
				System.out.println("Inserire un numero di subscriber valido");
			}
		}
	}

}
