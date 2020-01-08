package dos;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import dos.tester.Tester;

public class SubscribeCallback implements MqttCallback {
	
	private Subscriber subCaller = null;
	
	public SubscribeCallback(Subscriber sub) {
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
		long timeMsg = Long.parseLong(mqttMessage.toString());
		if(Tester.start) {
			if(timeMsg >= Tester.startTime && 
			   (timeMsg <= (Tester.startTime + Tester.delta)) ) {
				subCaller.count++;
			}
		}
	}
}
