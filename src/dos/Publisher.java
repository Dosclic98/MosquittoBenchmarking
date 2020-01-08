package dos;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import dos.tester.Tester;

public class Publisher implements Runnable {

	// public static int DELAY_PUBLISH = 100;	
	// public static int NUM_PUBLISH = 100;
	
	public static int qos = 0;

	// private static String BROKER_URL = "tcp://mqtt.eclipse.org:1883";
	private static String BROKER_URL = "tcp://localhost:1883";

	private int myId = 0;

	private String clientId;

	private static String TOPIC = "dos/Bench";

	private MqttClient mqttClient;

	private Object lock;


	public Publisher(Object lock1, int i) {
		if(lock == null) {
			this.lock = new Object();
		} else {
			this.lock = lock1;
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
				
				System.out.println("Publisher " + mqttClient.getClientId() + " connected on topic: " + Publisher.TOPIC);
			}
			
			boolean conn = mqttClient.isConnected();
			while(conn && !Tester.terminated) {
				synchronized(lock) {
					conn = mqttClient.isConnected();
					if(conn && Tester.start) {
						publishTime(myId);		
					}
				}
			}
			terminate();
			
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

	private void publishTime(int num) throws MqttException {
		final MqttTopic timeTopic = mqttClient.getTopic(TOPIC);
		int rndId = (int)((Math.random() * 1000000) + 1);
		final String message = rndId + "+" + Long.toString(System.currentTimeMillis());
		MqttMessage msg = new MqttMessage(message.getBytes());
		msg.setQos(qos);
		timeTopic.publish(msg);
	}
}
