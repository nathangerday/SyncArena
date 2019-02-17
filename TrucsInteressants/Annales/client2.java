import java.io.*;
import java.net.*;
import java.util.*;

public class client2 {

    public static void main(String args[]) {
	try {
	    String adresse = args[0];             
	    int port = Integer.parseInt(args[1]);            
	    Socket sock = new Socket(adresse,port);
	    BufferedReader inchan = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	    DataOutputStream outchan = new DataOutputStream(sock.getOutputStream());
	    syncComm handler = new syncComm(inchan);
	    user user1 = new user(handler,"Rouge",inchan,outchan,2);
	    user user2 = new user(handler,"Noir",inchan,outchan,2);	   
	    user user3 = new user(handler,"Bleu",inchan,outchan,2);
	    user user4 = new user(handler,"Vert",inchan,outchan,4);	   
	    user user5 = new user(handler,"Jaune",inchan,outchan,4);
	    user user6 = new user(handler,"Rose",inchan,outchan,8);	   
	    handler.start();
	    user1.start();
	    user6.start();
	    user4.start();
	    user2.start();
	    user5.start();
	    user3.start();
	    user1.join();
	    user2.join();
	    user3.join();
	    user4.join();
	    user5.join();
	    user6.join();
	    outchan.writeBytes("EXIT\n");	    
	    System.out.println(inchan.readLine());
	} catch(Throwable t) { t.printStackTrace(System.err); }
  } 
} 