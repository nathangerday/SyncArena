import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.concurrent.*;

public class FutureD<V extends Serializable> extends UnicastRemoteObject implements FutureRMI<V> {

    Future<V> f;
    FutureD(Future<V> f) throws RemoteException,InterruptedException {this.f=f;}

    public V get() throws RemoteException,InterruptedException,ExecutionException { return f.get();}
    public boolean isDone() throws RemoteException { return f.isDone();}
}
