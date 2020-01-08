package dos.tester;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttException;

import dos.Publisher;
import dos.SubscribeCallback;
import dos.Subscriber;

public class Tester {
	public static void main(String args[]) throws InterruptedException, MqttException {
		benchCase(100,1);
		// benchCase(1,1);
		// benchCase(1,500);
	}
	
	public static void benchCase(int numPub, int numSub) throws InterruptedException, MqttException {	
			ArrayList<Subscriber> listSub = new ArrayList<Subscriber>();
			ArrayList<Publisher> listPub = new ArrayList<Publisher>();
			/*
			Publisher.NUM_THREADS = numPub;
			Subscriber.NUM_THREADS = numSub;
			SubscribeCallback.sumDelay = -1;
			SubscribeCallback.count = 1;
			SubscribeCallback.avg = 0;
			*/
			
			for(int i = 1; i <= Subscriber.NUM_THREADS; i++) {
				listSub.add(new Subscriber(listSub, i));
				listSub.get(listSub.size()-1).run();
				Thread.sleep(Subscriber.DELAY_THREADS);
			}
			
			Object cre = new Object();
			synchronized(cre) {
				for(int i = 1; i <= Publisher.NUM_THREADS; i++) {
					System.out.println("Creating: " + i);
					listPub.add(new Publisher(listPub, cre, i));
					listPub.get(listPub.size()-1).start();
				}	
			}

			synchronized(listSub) {
				listSub.wait();
				for(Subscriber sub : listSub) {
					if(sub.mqttClient.isConnected()) {
						sub.mqttClient.disconnect();
					}
					sub.mqttClient.close();
				}
			}
			System.out.println("NUM PUB: " + Publisher.NUM_THREADS + "\n" +
							   "NUM SUB: " + Subscriber.NUM_THREADS + "\n" +
							   "MAX TROUGHPUT: " + 
							   			((1000 / Publisher.DELAY_PUBLISH) * Publisher.NUM_THREADS) +
							   			" msg/s" + "\n" +
							   "AVG DELAY: " + SubscribeCallback.avg);				

			
			System.out.println("Fine");
	}
	
}
