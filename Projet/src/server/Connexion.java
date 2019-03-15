package server;

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
                String[] commands;
                commands = in.readLine().split("/");
                // String tmp = in.readLine();
                // System.out.println(tmp);
                // commands = tmp.split("/");
                // System.out.println(commands[1]);
                // System.out.flush();
                
                switch(commands[0]){
                    case "CONNECT":
                        //TODO Maybe Check only char between a - z
                        commands[1] = commands[1].toLowerCase();
                        connect(commands);
                        break;
                    case "EXIT":
                        commands[1] = commands[1].toLowerCase();
                        if(this.username.equals(commands[1])){
                            disconnect();
                        }
                        break;
                    case "NEWPOS":
                       //TODO Partie B : Il ne devrait plus recevoir de newpos ( a verifier )
                        if(this.isConnectedToSession){
                            String[] vals = commands[1].split("X|Y");
                            double x = Double.valueOf(vals[1]);
                            double y = Double.valueOf(vals[2]);
                            session.changePos(this.username, x, y);
                        }
                        break;
                    case "NEWCOM":
                        if(this.isConnectedToSession){
                            String[] vals = commands[1].split("A|T");
                            double angle = Double.valueOf(vals[1]);
                            int nb_thrust = Integer.valueOf(vals[2]);
                            session.newCom(this.username, angle, nb_thrust);
                        }
                }       



                // out.print(); 
                // out.flush();
            }
        }catch (IOException e) {
            System.out.println("connexion : "+e.toString());
        }
        finally {
            try {
                in.close();
                out.close();
                client.close();
            }catch (IOException e) {
                //TODO Do something
            }

            disconnect();
        }
    }


    private void connect(String commands[]){
        if(!this.isConnectedToSession && commands[1].length() > 0){
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
        if(this.isConnectedToSession){
            this.session.disconnect(this.username);
            this.isConnectedToSession = false;
            this.username = "";
        }
    }

    public void sendConnectionDenied(){
        this.out.println("DENIED/");
        this.out.flush();
    }

    public void sendConnectionAccepted(String phase, String scores, String coord, String obstacles_coords){
        this.out.println("WELCOME/"+phase+"/"+scores+"/"+coord+"/"+obstacles_coords+"/");
        this.out.flush();
    }

    public void sendNewPlayer(String user){
        this.out.println("NEWPLAYER/"+user+"/");
        this.out.flush();
    }

    public void sendDisconnectPlayer(String user){
        this.out.println("PLAYERLEFT/"+user+"/");
        this.out.flush();
    }

    public void sendStartSession(String coords, String coord, String obstacles_coords){
        this.out.println("SESSION/"+coords+"/"+coord+"/"+obstacles_coords+"/");
        this.out.flush();
    }

    public void sendEndSession(String scores){
        this.out.println("WINNER/"+scores+"/");
        this.out.flush();
    }

    public void sendTick(String vcoords){
        this.out.println("TICK/"+vcoords+"/");
        this.out.flush();
    }

    public void sendNewObjectif(String coord, String scores){
        this.out.println("NEWOBJ/"+coord+"/"+scores+"/");
        this.out.flush();
    }
    
}
        