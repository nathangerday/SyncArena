import java.io.*;
import java.net.*;

public class client {
    public static void syncSend(BufferedReader inc, DataOutputStream outc, String message) {
	try{
	    String ack = "ACK";
	    System.out.print("J'envoie "+message);
	    outc.writeBytes(message);
	    while(true){
		String answer = inc.readLine();
		if(ack.equals(answer)){System.out.println("Ack recu.");break;} 
	    }} catch(Throwable t){ t.printStackTrace(System.err); }
    }
    
    public static void main(String args[]) {
	try {
        String adresse = args[0];             
        int port = Integer.parseInt(args[1]);            
        Socket sock = new Socket(adresse,port);
        BufferedReader inchan = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        DataOutputStream outchan = new DataOutputStream(sock.getOutputStream());
        syncSend(inchan,outchan,"Longtemps,\n");
	syncSend(inchan,outchan,"je me suis couche\n");
	syncSend(inchan,outchan,"de bonne heure.\n");
	syncSend(inchan,outchan,"EXIT\n");
	 } catch(Throwable t) { t.printStackTrace(System.err); }
  } 
} 