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
	public static int timeRunning = 10 * 1000;
	public static volatile boolean start = false;
	public static volatile boolean terminated = false;
	
	private static ResultWriter resWr = null;
	
	public static void main(String args[]) throws InterruptedException, MqttException, IOException {		
		resWr = new ResultWriter();
		
		for(int i = 0; i <= 0; i++) {
			benchmark(1,1,i);
		}
		
		resWr.close();
	}
	
	private static void benchmark(int numPub, int numSub, int qos) throws InterruptedException, MqttException, IOException {
		Subscriber.qos = qos;
		Publisher.qos = qos;
		
		ArrayList<Subscriber> listSub = new ArrayList<Subscriber>();
		ArrayList<Publisher> listPub = new ArrayList<Publisher>();
		
		
		Object cre = new Object();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numPub + numSub);
		for(int i = 1; i <= numPub; i++) {
			listPub.add(new Publisher(listPub, cre, i));
			executor.execute(listPub.get(listPub.size()-1));
		}
		for(int i = 1; i <= numSub; i++) {
			listSub.add(new Subscriber(listSub, i));
			executor.execute(listSub.get(listSub.size()-1));
		}
		System.out.println("Waiting for clients to start...");
		Thread.sleep(2000);
		
		// Counting
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
		for(Publisher pub : listPub) {
			numMsg += pub.count;
		}
		long numMsgSec = numMsg / (timeRunning / 1000);
		resWr.write(numPub, numSub, qos, numMsgSec);
		System.out.println("Messages per second managed: " + numMsgSec);
		
		reInit();
	}
	
	private static void reInit() {
		start = false;
		terminated = false;
	}
	
}
