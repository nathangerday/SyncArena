import java.rmi.*;
import java.io.*;
import java.util.concurrent.*;

 public interface FutureRMI<V extends Serializable> extends Remote {
     public V get() throws RemoteException,InterruptedException,ExecutionException;
   public boolean isDone() throws RemoteException;
}
