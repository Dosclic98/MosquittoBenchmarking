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
		boolean go = false;
		synchronized(lock) {
			go = !Subscriber.countedMsgs.contains(timeMsg);
			if(go) Subscriber.countedMsgs.add(timeMsg);
		}
		long time = parseTime(timeMsg);
		if(go) {
			if(Tester.start) {
				if(time >= Tester.startTime && 
				   (time <= (Tester.startTime + Tester.delta)) ) {
					subCaller.count++;
				}
			}
		}
	}
	
	private long parseTime(String msg) {
		String[] arr = msg.split("+");
		assert(arr.length == 2);
		return Long.parseLong(arr[1]);
	}
}
