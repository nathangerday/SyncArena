
import java.rmi.*;
import java.io.*;

public interface ExecutorRMI extends Remote {
    <V extends Serializable> FutureRMI<V> submit(CallableSer<V> job) throws RemoteException,InterruptedException;
 }
