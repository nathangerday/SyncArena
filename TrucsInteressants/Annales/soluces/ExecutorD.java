import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.concurrent.*;

public class ExecutorD extends UnicastRemoteObject implements ExecutorRMI {

    private ExecutorService pool;
    ExecutorD(int capacity) throws RemoteException { pool = Executors.newFixedThreadPool(capacity);}
    

    public    <V extends Serializable> FutureRMI<V> submit(CallableSer<V> job) throws RemoteException,InterruptedException {
	Future<V> f = pool.submit(job);
        FutureRMI<V> fd = new FutureD(f);
        return fd;
    }

}
    
