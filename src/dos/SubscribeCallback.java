package dos;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SubscribeCallback implements MqttCallback {

	private Object lock;
	public static long sumDelay = -1;
	public static long count = 1;
	public static long avg = 0;


	public SubscribeCallback(Object lock) {
		if(lock == null) {
			this.lock = new Object();
		} else {
			this.lock = lock;
		}
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		System.out.println("######################################");
	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		long delay = 0;
		System.out.println("Message arrived from topic: " + topic + " Message content: " + mqttMessage.toString());
		if(topic.equals("home/LWT")) System.out.println("Sensor gone");
		else {
			if(mqttMessage.toString().equals("EXIT")) {
				synchronized(lock) {
					if(sumDelay != (long) -2) {
						System.out.println("AVG Delay: " + sumDelay / count);
						sumDelay = (long) -2;
						count = 1;
						avg = sumDelay / count;
						lock.notify();
					}
				}
				System.exit(0);
			} else {
				delay = System.currentTimeMillis() - Long.parseLong(mqttMessage.toString());
				synchronized(lock) {
					if(sumDelay == (long) -1) sumDelay = delay;
					else if(sumDelay == (long) -2) {/* nulla */}
					else {
						sumDelay += delay;
						count++;
					}
				}
				System.out.println("Calculated delay: " + delay + " ms");					
			}
		}
	}

}
