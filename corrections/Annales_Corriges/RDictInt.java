import java.rmi.*;

public interface RDictInt extends Remote   {
    public String get (String o) throws RemoteException;
    public String put (String c, String v) throws RemoteException;
}
