import java.util.concurrent.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject; 
import java.util.*;
 
public class Client {


  public static void main( String argv[]) {
 

    CallableSer<Integer> jobi = new MyJob(2);

    String machine = argv[0];
    String port = argv[1];
    int max = Integer.parseInt(argv[2]);
    String url0="rmi://"+machine+":"+port+"/ex0"; 
    ArrayList<FutureRMI<Integer>> aFD = new ArrayList<FutureRMI<Integer>>(max);
    boolean tFD[] = new boolean[max];

    try {
      ExecutorRMI lex0 = (ExecutorRMI)Naming.lookup(url0);     
      CallableSer<Integer> job = new MyJob(2);
      FutureRMI<Integer> frmi = lex0.submit(job); 
      for (int i=0; i < max ; i++){ 
        System.out.println("job : "+i);
        job = new MyJob(i+35);
        aFD.add(lex0.submit(job));
      }

      System.out.println("Fin des lancements");

      // boucle de récupération des résultats 
      int i=0; int j;
      while (i < max) { int k = 0;
      for (FutureRMI<Integer> e : aFD) {
	  if (e.isDone() && !(tFD[k])) {
            j = e.get();
	    tFD[k]=true;
  	    System.out.println("X : Je suis dans le thread " + Thread.currentThread().getName() + " tFD["+k+"] = " + j); i++;} k++;}
      }
       
    
    }
   
    catch (Exception e) {
      System.err.println("exception : " + e.getMessage()); e.printStackTrace();  
    }
  }
}
