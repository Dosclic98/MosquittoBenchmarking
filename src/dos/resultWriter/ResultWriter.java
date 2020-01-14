package dos.resultWriter;

import java.io.FileWriter;
import java.io.IOException;

public class ResultWriter {
	
	FileWriter fileRes = null;
	
	public ResultWriter() throws IOException {
		fileRes = new FileWriter("results.csv", false);
		fileRes.write("numero_pub,numero_sub,num_topic,qos,msg_sec\n");
	}
	
	public void write(int numPub, int numSub, int qos, int numTopic, long msgPerSec) throws IOException {
		fileRes.write(numPub + "," + numSub + "," + qos + "," + numTopic + "," + msgPerSec + "\n");	
		
	}
	
	public void close() throws IOException {
		fileRes.close();
	}
	
}
