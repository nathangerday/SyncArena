import java.io.*;
import java.net.*;

public class echoclient {
  public static void main(String args[]) {
    try {
        String adresse = args[0];             
        int port = Integer.parseInt(args[1]);            
        String message = args[2];   
        Socket sock = new Socket(adresse,port);
        BufferedReader inchan = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        DataOutputStream outchan = new DataOutputStream(sock.getOutputStream());
        outchan.writeChars(message+"\n");
        String answer = inchan.readLine();
        System.out.println("Echo : " + answer);
    } catch(Throwable t) { t.printStackTrace(System.err); }
  } // end main
} // end class