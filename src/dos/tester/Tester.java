package dos.tester;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttException;

import dos.Publisher;
import dos.Subscriber;

public class Tester {
	public static int numPub = 0;
	public static int numSub = 0;
	public static int timeRunning = 30 * 1000;
	public static volatile boolean start = false;
	public static volatile long startTime = 0;
	public static volatile long delta = 0;
	public static volatile boolean terminated = false;
	
	public static void main(String args[]) throws InterruptedException, MqttException {
		// Da metere tutto in una funzione (bisogna passare anche il qos)
		// Valutare se calcolare i messaggi inviati anche usando i 
		// subscriber (guardare i messaggi che stanno tra il tempo di
		// inizio della conta e un tempo prestabilito).
		ArrayList<Subscriber> listSub = new ArrayList<Subscriber>();
		ArrayList<Publisher> listPub = new ArrayList<Publisher>();
		
		if(checkParams(args)) {
			Object cre = new Object();
			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numPub + numSub);
			for(int i = 1; i <= numPub; i++) {
				listPub.add(new Publisher(listPub, i));
				executor.execute(listPub.get(listPub.size()-1));
			}
			for(int i = 1; i <= numSub; i++) {
				listSub.add(new Subscriber(listSub, cre, i));
				executor.execute(listSub.get(listSub.size()-1));
			}
			System.out.println("Waiting for clients to start...");
			Thread.sleep(1000);
			
			// Counting
			Tester.delta = timeRunning;
			Tester.startTime = System.currentTimeMillis();
			Tester.start = true;
			Thread.sleep(timeRunning);
			Tester.start = false;
			
			// Terminating pubs and subs
			Tester.terminated = true;
			System.out.println("Waiting for threads to terminate...");
			for(Subscriber sub : listSub) {
				sub.terminate();
			}
			
			// Asking for termination of main clients threads and waiting
			// for it
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.HOURS);
			int numMsg = 0;
			for(Subscriber sub : listSub) {
				numMsg += sub.count;
			}
			System.out.println("Messages per second managed: " + numMsg / (timeRunning / 1000));
		}
	}
	
	private static boolean checkParams(String args[]) {
		if(args.length != 2) {
			System.out.println("Prendo <numero_pub> <numero_sub>");
			return false;
		} else {
			try {
				numPub = Integer.parseInt(args[0]);
				numSub = Integer.parseInt(args[1]);
			} catch(NumberFormatException e) {
				System.out.println("Dati inseriti invalidi");
				return false;
			}
			return true;
		}
	}
	
}
