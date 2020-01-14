package dos;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import dos.tester.Tester;

public class SubscribeCallback implements MqttCallback {
	
	private Subscriber subCaller = null;
	private Object lock = null;
	
	public SubscribeCallback(Subscriber sub, Object lock) {
		this.lock = lock;
		subCaller = sub;
	}

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
		String timeMsg = mqttMessage.toString();
		long time = Long.parseLong(timeMsg);
		if(Tester.start) {
			if(time >= Tester.startTime && 
			   (time <= (Tester.startTime + Tester.delta)) ) {
				subCaller.count++;
			}
		}
		
	}
}
