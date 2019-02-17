import java.util.Vector;
import java.io.*;
import java.net.*;

public class			echoserverPool
{
 public static void		main(String args[])
 {
    int port = Integer.parseInt(args[0]);
	int capacity = Integer.parseInt(args[1]);
	echoServer server = new echoServer(port, capacity);
	server.run();
  }
}

class					echoServer
{
  Vector<echoClient>	clients;
  Vector<Socket>        sockets;
  ServerSocket			serv;
  Socket				client;
  int					capacity;
  int					nbConnectedClients;
  int					nbWaitingSocks;
  int					port;
	
  echoServer(int p, int c)
  {
	  capacity = c;
	  port = p;
	  clients = new Vector<echoClient>(c);
	  sockets = new Vector<Socket>();
	  for (int i = 0; i < c; i++)
	  {
		  echoClient tmpEcho = new echoClient(this);
		  clients.add(tmpEcho);
		  tmpEcho.start();
	  }
	  nbConnectedClients = 0;
	  nbWaitingSocks = 0;
  }
	
  public Socket			removeFirstSocket()
  {
	  Socket ret = sockets.get(0);
	  sockets.removeElementAt(0);
	  return ret;
  }
	
  public void			newConnect()
  {
    nbConnectedClients++;
    nbWaitingSocks--;
    System.out.println(" Thread handled connection.");
	System.out.println("   * " + nbConnectedClients + " connected.");
    System.out.println("   * " + nbWaitingSocks + " waiting.");
  }

  public void			clientLeft()
  {
    nbConnectedClients--;
    System.out.println(" Client left.");
    System.out.println("   * " + nbConnectedClients + " connected.");
    System.out.println("   * " + nbWaitingSocks + " waiting.");
  }
	
  public int			stillWaiting() { return nbWaitingSocks;}
	
  public void			run()
  {
    try 
    {
	  serv = new ServerSocket(port); 
      while(true)
      {
        client = serv.accept();
        System.out.println("New connexion at server.");  
        synchronized (this)
        {
          sockets.add(client);
          nbWaitingSocks++;
          this.notify();
		}
	  }
    }
    catch(Throwable t) {t.printStackTrace(System.err); }
  }
}

class					echoClient extends Thread
{
  BufferedReader		inchan;
  DataOutputStream		outchan;
  echoServer			server;
  Socket				socket;
  int					idC;
	
  echoClient(echoServer s) { server = s; }
	
  public void			run()
  {
    Socket				s;
	  
	while (true)
	{
      synchronized (server)
      {
        if (server.stillWaiting() == 0)
          try { server.wait(); } catch(InterruptedException e) {e.printStackTrace();}
        s = server.removeFirstSocket();
        server.newConnect();
	  }
      try
	  {
        inchan = new BufferedReader(new InputStreamReader(s.getInputStream()));
        outchan = new DataOutputStream(s.getOutputStream());
        socket = s;
        while (true)
        {
	      String command = inchan.readLine();
		  if(command == null || command.equals("")) { System.out.println("Fin de connexion."); break;}
		  outchan.writeChars(command + "\n");
		}
	    socket.close();
		synchronized (server) { server.clientLeft(); }
	  } catch(IOException e) { e.printStackTrace(); System.exit(1); }
	}
  }	  
}

