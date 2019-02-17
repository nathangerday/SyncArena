import java.rmi.*;
import java.io.*;

public interface RappelRMI<V extends Serializable> extends Remote { 
   void put(V r) throws RemoteException;
}
