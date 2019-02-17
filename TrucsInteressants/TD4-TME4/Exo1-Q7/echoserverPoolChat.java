import java.util.Vector;
import java.io.*;
import java.net.*;

public class			echoserverPoolChat
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
  Vector<echoClient>		clients;
  Vector<Socket>			sockets;
  Vector<DataOutputStream>	streams;
  ServerSocket				serv;
  Socket					client;
  int						capacity;
  int						nbConnectedClients;
  int						nbWaitingSocks;
  int						port;
	
  echoServer(int p, int c)
  {
	  capacity = c;
	  port = p;
	  clients = new Vector<echoClient>(c);
	  sockets = new Vector<Socket>();
	  streams = new Vector<DataOutputStream>();
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
	
  public void			newConnect(DataOutputStream out)
  {
    nbConnectedClients++;
    nbWaitingSocks--;
    System.out.println(" Thread handled connection.");
	System.out.println("   * " + nbConnectedClients + " connected.");
    System.out.println("   * " + nbWaitingSocks + " waiting.");
	streams.add(out);
	writeAllButMe("*** New user on chat ***", out);
  }

  public void			clientLeft(DataOutputStream out)
  {
    nbConnectedClients--;
    System.out.println(" Client left.");
    System.out.println("   * " + nbConnectedClients + " connected.");
	System.out.println("   * " + nbWaitingSocks + " waiting.");
	writeAllButMe("*** A user has left ***", out);
    streams.remove(out);
  }
	
  public void			writeAllButMe(String s, DataOutputStream out)
  {
	  try
	  {
	  for (int i = 0; i < nbConnectedClients; i++)
		if (streams.elementAt(i) != out)
		  streams.elementAt(i).writeChars(s);
	  }
	  catch (IOException e) {}
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
	  }
      try
		{
		inchan = new BufferedReader(new InputStreamReader(s.getInputStream()));
		outchan = new DataOutputStream(s.getOutputStream());
		socket = s;
		synchronized (server) { server.newConnect(outchan); }
        while (true)
        {
	      String command = inchan.readLine();
		  if(command == null || command.equals("")) { System.out.println("Fin de connexion."); break;}
		  synchronized (server) { server.writeAllButMe(command + "\n", outchan); }
		}
		synchronized (server) { server.clientLeft(outchan); }
	    socket.close();
	  } catch(IOException e) { e.printStackTrace(); System.exit(1); }
	}
  }	  
}

