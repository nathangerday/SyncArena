import java.net.*;
import java.io.*;

public class Connexion extends Thread {
    protected Socket client; 
    protected BufferedReader in;
    protected PrintStream out;
    protected boolean isConnectedToSession = false;
    protected String username = "";
    protected Session session;

    public Connexion(Socket client_soc, Session session){
        this.client = client_soc;
        this.session = session;

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
                String[] commands = in.readLine().split("/");
                // System.out.println("Received : "+commands);
                switch(commands[0]){
                    case "CONNECT":
                        connect(commands);
                }



                // out.print(); 
                out.flush();
            }
        }catch (IOException e) {
            System.out.println("connexion : "+e.toString());
        }
        finally {
            try {
                client.close();
            }catch (IOException e) {
                //TODO Do something
            }

            disconnect();
        }
    }


    private void connect(String commands[]){
        if(!this.isConnectedToSession){
            if(this.isConnectedToSession = this.session.connect(commands[1], this)){ //Connexion reussie
                this.username = commands[1];
            }
        }else{
            System.err.println("Error : Trying to connect to a session as " + commands[1] + " but already connected as " + this.username);
            sendConnectionDenied();
        }

        // if(this.username == null){ // Not yet connected
        //     System.out.println("User " + commands[1] + " connecting");        
        //     if(session.connect(commands[1])){
        //         this.username = commands[1];
        //         System.out.println("Connection acceptée");   
        //     }else{
        //         System.out.println("Connection denied");
        //         sendConnectionDenied();
        //     }
        // }else{
        //     System.out.println("Already connected with username : " + this.username);
        //     sendConnectionDenied();
        // }
    }

    private void disconnect(){
        this.session.disconnect(this.username);
        this.isConnectedToSession = false;
        this.username = "";
    }

    public void sendConnectionDenied(){
        this.out.print("DENIED/");
    }

    public void sendConnectionAccepted(){
        String res = "WELCOME/";
    }
}
        