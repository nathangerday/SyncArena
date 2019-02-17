import java.rmi.*;
import java.rmi.server.*;
import java.io.*;

public class Rappel<V extends Serializable> extends UnicastRemoteObject implements RappelRMI<V>, Unreferenced {
    private V result;
    private boolean f = false;
    public Rappel() throws RemoteException{}
    public void finish () {f=true;}
    public boolean is_finished () {return f;}
    public void put(V r) throws RemoteException {
     try{synchronized(this) {wait(2000);}}
     catch (InterruptedException ie){};
     result = r;
     finish();
     System.out.println("rangement du resultat " + result); synchronized(this) {
     this.notifyAll(); } }
     public void unreferenced() {
     try {boolean b = UnicastRemoteObject.unexportObject(this,true);}
     catch (NoSuchObjectException nsoe) {}; } 
   public V get_result() {return result;}
}
