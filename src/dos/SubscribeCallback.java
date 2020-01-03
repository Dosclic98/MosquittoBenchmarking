package dos;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SubscribeCallback implements MqttCallback {

	private Object lock;


	public SubscribeCallback(Object lock) {
		if(lock == null) {
			this.lock = new Object();
		} else {
			this.lock = lock;
		}
	}

	public SubscribeCallback() {
		this(null); }


	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		synchronized(lock) {
			System.out.println("Message arrived from topic: " + topic + " Message content: " + mqttMessage.toString());
			if(topic.equals("home/LWT")) System.out.println("Sensor gone");
		}
	}

}
