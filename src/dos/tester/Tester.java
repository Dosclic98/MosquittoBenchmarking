package dos.tester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttException;

import dos.Publisher;
import dos.Subscriber;
import dos.resultWriter.ResultWriter;

public class Tester {
	public static int numPub = 0;
	public static int numSub = 0;
	public static int timeRunning = 30 * 1000;
	public static volatile boolean start = false;
	public static volatile long startTime = 0;
	public static volatile long delta = 0;
	public static volatile boolean terminated = false;
	public static String[] topics = null;
	public static ResultWriter resWr = null;
	
	public static void main(String args[]) throws InterruptedException, MqttException, IOException {
		resWr = new ResultWriter();
		for(int i = 0; i < 3; i++) {
			benchmark(1,200,i);
		}
		for(int i = 0; i < 3; i++) {
			benchmark(200,200,i);
		}
		resWr.close();
	}
	
	private static void benchmark(int numPub, int numSub, int qos) throws InterruptedException, MqttException, IOException {
		// Da metere tutto in una funzione (bisogna passare anche il qos)
		// Valutare se calcolare i messaggi inviati anche usando i 
		// subscriber (guardare i messaggi che stanno tra il tempo di
		// inizio della conta e un tempo prestabilito).
		reInit();
		ArrayList<Subscriber> listSub = new ArrayList<Subscriber>();
		ArrayList<Publisher> listPub = new ArrayList<Publisher>();
		Tester.numPub = numPub;
		Tester.numSub = numSub;
		topics = initTopics();
		Subscriber.qos = qos;
		Publisher.qos = qos;
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
		int msgSec = numMsg / (timeRunning / 1000);
		System.out.println("Messages per second managed: " + msgSec);
		
		resWr.write(numPub, numSub, topics.length, qos, msgSec);
	}
	
	private static void reInit() {
		numPub = 0;
		numSub = 0;
		timeRunning = 30 * 1000;
		start = false;
		startTime = 0;
		delta = 0;
		terminated = false;
		topics = null;
	}
	
	private static String[] initTopics() {
		String[] topics = new String[numPub];
		for(int i = 0; i < numPub; i++) {
			topics[i] = "dos/Bench" + i;
		}
		return topics;
	}
	
}
