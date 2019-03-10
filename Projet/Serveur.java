import java.net.*;
import java.io.*;

public class Serveur extends Thread {
    protected static final int PORT =45678;
    protected ServerSocket ecoute;
    public Serveur () {
        try {ecoute = new ServerSocket(PORT);}
        catch (IOException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Serveur en ecoute sur le port : "+PORT);
        this.start();
    }
    
    public void run (){
        Session currentSession = new Session();
        try {
            while (true){
                Socket client=ecoute.accept();
                Connexion c = new Connexion (client, currentSession);
            }
            
        }catch (IOException e){System.err.println(e.getMessage());System.exit(1);}
    }
    public static void main (String[] args){
        new Serveur();
    }
}