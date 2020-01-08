package dos.resultWriter;

import java.io.FileWriter;
import java.io.IOException;

public class ResultWriter {
	
	FileWriter fileRes = null;
	
	public ResultWriter() throws IOException {
		fileRes = new FileWriter("results.txt", false);
		fileRes.write("numero_pub,numero_sub,qos,msg_sec\n");
	}
	
	public void write(int numPub, int numSub, int qos, long msgPerSec) throws IOException {
		fileRes.write(numPub + "," + numSub + "," + qos + "," + msgPerSec + "\n");	
		
	}
	
	public void close() throws IOException {
		fileRes.close();
	}
	
}
