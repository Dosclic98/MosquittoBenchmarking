package dos.tester;

import java.util.ArrayList;

import dos.Publisher;
import dos.SubscribeCallback;
import dos.Subscriber;

public class Tester {
	public static void main(String args[]) throws InterruptedException {
		ArrayList<Subscriber> listSub = new ArrayList<Subscriber>();
		ArrayList<Publisher> listPub = new ArrayList<Publisher>();
		
		for(int i = 1; i <= Subscriber.NUM_THREADS; i++) {
			listSub.add(new Subscriber(listSub, i));
			listSub.get(listSub.size()-1).start();
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
		
		System.out.println("MEGA AVG: " + SubscribeCallback.avg);
	}
}
