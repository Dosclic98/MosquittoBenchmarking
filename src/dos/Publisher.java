package dos;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class Publisher extends Thread {
	private static int NUM_THREADS = 100;

	private static int DELAY_PUBLISH = 6000;

	private static String BROKER_URL = "tcp://mqtt.eclipse.org:1883";

	private int myId = 0;

	private String clientId;

	private static String TOPIC = "dos/dosMult";

	private static MqttClient mqttClient;

	private Object lock;


	public Publisher(Object lock, int i) {
		if(lock == null) {
			this.lock = new Object();
		} else {
			this.lock = lock;
		}

		try {
			synchronized(this.lock) {
				myId = i;
				clientId = i + Long.toString(new Date().getTime()) + "pub";
				mqttClient = new MqttClient(BROKER_URL, clientId);
			}
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Publisher(int i) {
		this(null, i); }


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

				mqttClient.connect(option);
				// Thread.sleep(100);
			}

			while(true) {
				synchronized(lock) {
					publishTime(myId);
					Thread.sleep(DELAY_PUBLISH);
				}
			}
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void publishTime(int num) throws MqttException {
		synchronized(lock) {
			final MqttTopic timeTopic = mqttClient.getTopic(TOPIC);
			final String message = "DosPub numero: " + num + " " + Long.toString(new Date().getTime());
			timeTopic.publish(new MqttMessage(message.getBytes()));
			System.out.println("Published by DosPub numero " + num + " on topic: " + timeTopic.getName() + " Message: " + message);
		}
	}

	public static void main(String args[]) throws InterruptedException {
		ArrayList<Publisher> listPub = new ArrayList<Publisher>();

		for(int i = 1; i <= NUM_THREADS; i++) {
			listPub.add(new Publisher(listPub, i));
			listPub.get(listPub.size()-1).start();
		}

		System.out.println("Fine");
	}
}
