import java.util.concurrent.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject; 

public class Creation {
  public static void main (String args[]) {
    if (System.getSecurityManager() == null) { 
      System.setSecurityManager(new RMISecurityManager());
    }

   try {
       ExecutorD ex0 = new ExecutorD(10);
       Naming.rebind("//localhost/ex0",ex0);
       System.out.println("Objet distribue ’ex0’ est enregistreex0"); 
   }
   catch (Exception e) { e.printStackTrace(); }
  } 
}

