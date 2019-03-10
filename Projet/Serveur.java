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
        try {
            while (true){
                Socket client=ecoute.accept();
                Connexion c = new Connexion (client);
            }
            
        }catch (IOException e){System.err.println(e.getMessage());System.exit(1);}
    }
    public static void main (String[] args){
        new Serveur();
    }
}


class Connexion extends Thread {
    protected Socket client; 
    protected BufferedReader in;
    protected PrintStream out;
    
    public Connexion(Socket client_soc){
        client=client_soc;
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintStream(client.getOutputStream()); 
        }catch (IOException e){
            try {client.close();} catch (IOException e1){}
            System.err.println(e.getMessage());
            return;
        }
        this.start();
    }
    public void run(){
        try {
            while (true) {
                String ligne=in.readLine();
                System.out.println("Received : "+ligne);
                if (ligne.toUpperCase().compareTo("FIN")==0) {
                    break;
                }
                System.out.println(ligne.toUpperCase());
                out.print(ligne.toUpperCase()); 
                out.flush();
            }
        }catch (IOException e) {
            System.out.println("connexion : "+e.toString());
        }
        finally {
            try {
                client.close();
            }catch (IOException e) {}
        }
    }
}
        