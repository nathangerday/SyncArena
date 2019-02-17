import java.rmi.*;
import java.io.*;

public interface MRExecutorRMI extends ExecutorRMI {
    <V extends Serializable> void submit(RappelRMI<V> r, CallableSer<V> job) throws RemoteException,InterruptedException;
 }
