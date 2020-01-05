package dos;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class Publisher extends Thread {

	public static int NUM_THREADS = 500;
	public static int DELAY_PUBLISH = 100;	
	public static int NUM_PUBLISH = 100;
	
	public static int qos = 1;

	// private static String BROKER_URL = "tcp://mqtt.eclipse.org:1883";
	private static String BROKER_URL = "tcp://localhost:1883";

	private int myId = 0;

	private String clientId;

	private static String TOPIC = "dos/Bench";

	private MqttClient mqttClient;

	private Object lock;
	
	private Object lockCre;


	public Publisher(Object lock1, Object lock2, int i) {
		if(lock == null || lock2 == null) {
			this.lock = new Object();
			this.lockCre = new Object();
		} else {
			this.lock = lock1;
			this.lockCre = lock2;
		}

		try {
			synchronized(this.lock) {
				myId = i;
				clientId = MqttClient.generateClientId() + "-Pub";
				mqttClient = new MqttClient(BROKER_URL, clientId);
			}
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


	@Override
	public void run() {
		try {
			synchronized(lock) {
				MqttConnectOptions option = new MqttConnectOptions();
				// option.setUserName("sonor");
				// option.setPassword("sono".toCharArray());
				

				// Imposta il fatto che i messaggi pubblicati da questo publisher non vadano recuperati da un subscriber
				// che si connette dopo l'inizio della trasmissione
				option.setCleanSession(true);

				// Imposta il suo comportamento in casi particolari
				option.setWill(mqttClient.getTopic("retilab/LWT"), "I'm gone".getBytes(), 0 , false);

				//Thread.sleep(100);
				mqttClient.connect(option);
			}
			
			int i = 0;
			while(i < NUM_PUBLISH) {
					publishTime(myId);
					Thread.sleep(DELAY_PUBLISH);
					i++;
			}
			publishExit(myId);
			if(mqttClient.isConnected()) {
				mqttClient.disconnect();				
			}
			mqttClient.close();
			System.out.println(mqttClient.getClientId() + " disconnected");
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void publishTime(int num) throws MqttException {
		synchronized(lockCre) {
			synchronized(lock) {
				final MqttTopic timeTopic = mqttClient.getTopic(TOPIC);
				final String message = Long.toString(System.currentTimeMillis());
				MqttMessage msg = new MqttMessage(message.getBytes());
				msg.setQos(qos);
				timeTopic.publish(msg);
				System.out.println("Published by " + mqttClient.getClientId() + " numero " + num + " on topic: " + timeTopic.getName() + "\n\t Message: " + message);
			}			
		}
	}

	private void publishExit(int num) throws MqttException {
		synchronized(lockCre) {
			synchronized(lock) {
				final MqttTopic timeTopic = mqttClient.getTopic(TOPIC);
				final String message = "EXIT";
				MqttMessage msg = new MqttMessage(message.getBytes());
				msg.setQos(2);
				timeTopic.publish(msg);
				System.out.println("Published by " + mqttClient.getClientId() + " numero " + num + " on topic: " + timeTopic.getName() + "\n\t Message: " + message);
			}			
		}
	}

	public static void main(String args[]) throws InterruptedException {
		ArrayList<Publisher> listPub = new ArrayList<Publisher>();
		
		Object cre = new Object();
		synchronized(cre) {
			for(int i = 1; i <= NUM_THREADS; i++) {
				System.out.println("Creating: " + i);
				listPub.add(new Publisher(listPub, cre, i));
				listPub.get(listPub.size()-1).start();
			}	
		}
		
		System.out.println("Fine");
	}
}
