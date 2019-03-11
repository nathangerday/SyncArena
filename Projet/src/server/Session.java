package server;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import game_elements.*;

public class Session {
    private Map<String, Player> players = new HashMap<>();
    private Map<String, Connexion> connexions = new HashMap<>();
    private final Object userLock = new Object();
    private final Object phaseLock = new Object();
    private final Object objectifLock = new Object();

    private Objectif objectif = null;
    private String phase = "inactive";
    
    private int delayBeforeStart = 10;
    private int server_tickrate = 1;

    /**
     * Creates the player associated with the given username if 
     * it is not already known and save the reference to the Connexion
     */
    public boolean addUser(String name, Connexion c) {
        synchronized (userLock) {
            if (!players.containsKey(name)) {
                players.put(name, new Player(name));
                connexions.put(name, c);
                return true;
            }
            return false;
        }
    }

    /**
     * Try to add a new player.
     * If it is the first player in the session, start a 
     * countdown to start the session after "delayBeforeStart" seconds.
     * When succeeding, the newly connected client receives an accept message
     * and every other client is notified.
     * If it doesn't the client is sent a denied connection message.
     */
    public boolean connect(String name, Connexion c) {
        if (addUser(name, c)) {
            synchronized (phaseLock) {
                if (this.phase.equals("inactive")) {
                    this.phase = "waiting";
                    scheduleStart();
                }
            }
            connectionAccepted(c);
            notifyOfNewPlayerConnection(name);
            return true;
        }
        c.sendConnectionDenied();
        return false;
    }


    /**
     * Creates a thread that will begin after the amount of seconds defined in 
     * "delayBeforeStart", which will then call the start() function to begin the 
     * session
     */
    private void scheduleStart() {
        ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();

        Runnable task = new Runnable() {
            public void run() {
                start();
            }
        };

        sch.schedule(task, delayBeforeStart, TimeUnit.SECONDS);
    }

    /**
     * Creates a thread that will keep running as long as the Session is in a 
     * "ingame" phase and will call the tick() function "server_tickrate" times 
     * per second
     */
    private void autoTick() {
        Runnable task = new Runnable() {
            public void run() {
                while (true) {
                    synchronized (phaseLock) {
                        if (!phase.equals("ingame")) {
                            return;
                        }
                    }
                    tick();
                    try {
                        Thread.sleep(1000 / server_tickrate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(task).start();
    }

    /**
     * Start the session if the phase is currently "waiting".
     * It then creates an Objectif, send a message to every client to
     * notify the start of the session.
     * Finally it calls autoTick() to send message to client regularly of the
     * state of the game
     */
    public void start() {
        synchronized (phaseLock) {
            if (this.phase.equals("waiting")) {
                // Start the game
                this.phase = "ingame";
                synchronized(objectifLock){
                    this.objectif = new Objectif();
                }
            } else {
                return;
            }
        }
        synchronized (userLock) {
            String coords = "";
            String coord = "";
            int i = players.size();
            DecimalFormat sixdecimals = new DecimalFormat("#.######");

            for (Player p : players.values()) {
                p.reset();
                i--;
                Double xformat = Double.valueOf(sixdecimals.format(p.getX()));
                Double yformat = Double.valueOf(sixdecimals.format(p.getY()));
                coords += p.getUsername() + ":X" + xformat + "Y" + yformat;
                if (i > 0) {
                    coords += "|";
                }
            }
            synchronized(objectifLock){
                coord = "X" + this.objectif.getX() + "Y" + this.objectif.getY();
            }
            for (Map.Entry<String, Connexion> entry : connexions.entrySet()) {
                entry.getValue().sendStartSession(coords, coord);
            }
        }
        autoTick(); // Start a thread that will call tick once every server_tickrate
    }

    /**
     * Function called every server_tickrate to send a message with updated
     * information to every client
     */
    public void tick(){
        synchronized(userLock){
            String coords = "";
            int i = players.size();
            DecimalFormat sixdecimals = new DecimalFormat("#.######");

            for (Player p : players.values()) {
                i--;
                Double xformat = Double.valueOf(sixdecimals.format(p.getX()));
                Double yformat = Double.valueOf(sixdecimals.format(p.getY()));
                coords += p.getUsername() + ":X" + xformat + "Y" + yformat;

                if (i > 0) {
                    coords += "|";
                }
            }
            for (Map.Entry<String, Connexion> entry : connexions.entrySet()) {
                entry.getValue().sendTick(coords);
            }
        }
    }

    /**
     * Changes the current objectif and send a message to every client.
     */
    public void changeObjectif(){
        String scores = "";
        String coord = "";
        synchronized(objectifLock){
            this.objectif = new Objectif();
            coord = "X"+this.objectif.getX()+"Y"+this.objectif.getY();
        }

        synchronized(userLock){
            int i = players.size();
            for(Player p : players.values()){
                i--;
                scores += p.getUsername() + ":" + p.getScore();
                if(i>0){
                    scores += "|";
                }
            }
            for(Map.Entry<String, Connexion> entry : connexions.entrySet()){
                entry.getValue().sendNewObjectif(coord, scores);
            }
        }
        
    }   

    /**
     * Terminate the current session if it is in "ingame" phase.
     * If there are still players in the session, it sends a message 
     * with scores to all of them, then prepare to restart a new gameplay 
     * session by starting the function which will call start() after a delay
     */
    public void endSession(){
        synchronized(userLock){
            synchronized(phaseLock){
                if(this.phase.equals("ingame")){
                    if(this.players.size() == 0){
                        this.phase = "inactive";
                    }else{
                        String scores = "";
                        int i = players.size();
                        for(Player p : players.values()){
                            i--;
                            scores += p.getUsername() + ":" + p.getScore();
                            if(i>0){
                                scores += "|";
                            }
                        }
                        for(Map.Entry<String, Connexion> entry : connexions.entrySet()){
                            entry.getValue().sendEndSession(scores);
                        }
                        this.phase = "waiting";
                        scheduleStart();
                    }
                }
            }
        }
    }

    /**
     * Changes the current pos of the given player
     */
    public void changePos(String user, double x, double y){
        synchronized(userLock){
            this.players.get(user).moveTo(x, y);
        }
    }


    /**
     * Disconnect the given player from the session.
     * If it was the last player, terminate the current session.
     * Otherwise, send a message to every other client to notify of the disconnect
     */
	public void disconnect(String username) {
        synchronized(userLock){
            this.players.remove(username);
            this.connexions.remove(username);
            if(this.players.size() == 0){
                synchronized(phaseLock){
                    this.phase = "inactive";
                    endSession();
                }
            }else{
                for(Map.Entry<String, Connexion> entry : connexions.entrySet()){
                    if(entry.getKey().equals(username)){
                        continue;
                    }
                    entry.getValue().sendDisconnectPlayer(username);
                }
            }
        }
    }
    
    /**
     * Send a message to the newly connected player with 
     * relevant information.
     */
    private void connectionAccepted(Connexion c){
        String scores = "";
        String coord = "";
        synchronized(userLock){
            synchronized(phaseLock){
                if(!this.phase.equals("ingame")){
                    scores = "";
                    coord = "";
                }else{
                    int i = players.size();
                    for(Player p : players.values()){
                        i--;
                        scores += p.getUsername() + ":" + p.getScore();
                        if(i>0){
                            scores += "|";
                        }
                    }
                    synchronized(objectifLock){
                        coord = "X"+this.objectif.getX()+"Y"+this.objectif.getY();
                    }
                }
            }
        }
        c.sendConnectionAccepted(this.phase, scores, coord);
    }

    /**
     * Send a message to every client except the added one, to notify
     * of the new player connection.
     */
    private void notifyOfNewPlayerConnection(String addedPlayer){
        synchronized(userLock){
            for(Map.Entry<String, Connexion> entry : connexions.entrySet()){
                if(entry.getKey().equals(addedPlayer)){
                    continue;
                }
                entry.getValue().sendNewPlayer(addedPlayer);
            }
        }
    }
}