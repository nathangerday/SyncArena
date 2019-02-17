import java.io.*;
import java.net.*;

public class			echoserverThread
{
 public static void		main(String args[])
 {
  ServerSocket			serv;
  Socket				client;
	 
  try
  {
    int port = Integer.parseInt(args[0]);
    serv = new ServerSocket(port); 
    while(true)
    {
      client = serv.accept();
	  System.out.println("Nouvelle connexion.");
      echoClient clt = new echoClient(client);
      clt.start();
    }
   }
   catch(Throwable t) {t.printStackTrace(System.err); }
  }
}

class					echoClient extends Thread
{
  BufferedReader		inchan;
  DataOutputStream		outchan;
  Socket				socket;
	
  echoClient(Socket s)
  {
	try
    {
      inchan = new BufferedReader(new InputStreamReader(s.getInputStream()));
      outchan = new DataOutputStream(s.getOutputStream());
	}
    catch(IOException e) { e.printStackTrace(); System.exit(1);}
	socket = s;
  }
	
  public void run()
  {
    try {
    while (true)
    {
      String command = inchan.readLine();
      if(command.equals("")) { System.out.println("Fin de connexion."); break;}
      outchan.writeChars(command + "\n");
    }
	socket.close();
    } catch(IOException e) { e.printStackTrace(); System.exit(1);}
  }	  
}

