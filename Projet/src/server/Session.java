package server;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import game_elements.*;
import constants.Constants;

public class Session {
    private Map<String, Player> players = new HashMap<>();
    private Map<String, Connexion> connexions = new HashMap<>();
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private Deque<Attack> attacks = new LinkedList<>();
    
    private final Object userLock = new Object();
    private final Object phaseLock = new Object();
    private final Object objectifLock = new Object();
    private final Object attacksLock = new Object();

    private Objectif objectif = null;
    private String phase = "inactive";
    
    private int delayBeforeStart = 10;


    /**
     * Creates the player associated with the given username if 
     * it is not already known and save the reference to the Connexion
     */
    public boolean addUser(String name, Connexion c) {
        synchronized (userLock) {
            if (!players.containsKey(name)) {
                Player newplayer = new Player(name);
                resetToValidPosition(newplayer);
                players.put(name, newplayer);
                connexions.put(name, c);
                return true;
            }
            return false;
        }
    }

    

    /**
     * Try to add a new player. If it is the first player in the session, start a
     * countdown to start the session after "delayBeforeStart" seconds. When
     * succeeding, the newly connected client receives an accept message and every
     * other client is notified. If it doesn't the client is sent a denied
     * connection message.
     */
    public boolean connect(String name, Connexion c) {
    	synchronized (userLock) {
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
     * "ingame" phase and will call the tick() function "SERVER_TICKRATE" times per
     * second
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
                        Thread.sleep(1000 / Constants.SERVER_TICKRATE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(task).start();
    }

    /**
     * Start the session if the phase is currently "waiting". It then creates an
     * Objectif, send a message to every client to notify the start of the session.
     * Finally it calls autoTick() to send message to client regularly of the state
     * of the game
     */
    public void start() {
        Random r = new Random();
        synchronized (phaseLock) {
            if (this.phase.equals("waiting")) {
                // Start the game
                this.phase = "ingame";
                this.obstacles.clear();
                synchronized(attacksLock){
                    this.attacks.clear();
                }
                // int nbObstacles = r.nextInt(10);
                int nbObstacles = 45;
                for (int i = 0; i < nbObstacles; i++) {
                    this.obstacles.add(new Obstacle());
                }

                synchronized (objectifLock) {
                    createObjectifToValidPosition();
                }

            } else {
                return;
            }
        }
        synchronized (userLock) {
            String coords = "";
            String coord = "";
            String obstacles_coords = "";
            int i = players.size();
            DecimalFormat sixdecimals = new DecimalFormat("#.######");

            for (Player p : players.values()) {
                resetToValidPosition(p);
                i--;
                Double xformat = Double.valueOf(sixdecimals.format(p.getX()));
                Double yformat = Double.valueOf(sixdecimals.format(p.getY()));
                coords += p.getUsername() + ":X" + xformat + "Y" + yformat;
                if (i > 0) {
                    coords += "|";
                }
            }
            synchronized (objectifLock) {
                coord = "X" + this.objectif.getX() + "Y" + this.objectif.getY();
            }

            i = obstacles.size();
            for (Obstacle o : obstacles) {
                obstacles_coords += "X" + o.getX() + "Y" + o.getY();
                i--;
                if (i > 0) {
                    obstacles_coords += "|";
                }
            }
            for (Map.Entry<String, Connexion> entry : connexions.entrySet()) {
                entry.getValue().sendStartSession(coords, coord, obstacles_coords);
            }
        }
        autoTick(); // Start a thread that will call tick once every SERVER_TICKRATE
    }


    /**
     * Function called every SERVER_TICKRATE to send a message with updated
     * information to every client
     */
    public void tick(){
        synchronized(userLock){
            synchronized(phaseLock){
                synchronized(objectifLock){
                    String vcoords = "";
                    String attCoords = "";
                    int i = players.size();
                    DecimalFormat sixdecimals = new DecimalFormat("#.######");

                    // Checking collision in 3 steps in necessary to have a precise output (we don't want to check with a player which is not yet updated)
                    for (Player p : players.values()) {
                        p.update();
                    }
                    for (Player p : players.values()){
                        p.checkCollision(this.players.values(), this.obstacles);
                    }
                    for(Player p : players.values()){
                        p.reactToCollision();
                        
                        if(this.objectif.isCollectableBy(p)){
                            p.setScore(p.getScore() + 1);
                            if(p.getScore() >= Constants.WIN_CAP){
                                endSession();
                                return;
                            }else{
                                changeObjectif();
                            }
                        }
                        i--;
                        Double xformat = Double.valueOf(sixdecimals.format(p.getX()));
                        Double yformat = Double.valueOf(sixdecimals.format(p.getY()));
                        Double vectorxformat = Double.valueOf(sixdecimals.format(p.getVectorX()));
                        Double vectoryformat = Double.valueOf(sixdecimals.format(p.getVectorY()));
                        Double directionformat = Double.valueOf(sixdecimals.format(p.getDirection()));
                        vcoords += p.getUsername() + ":X" + xformat + "Y" + yformat + "VX" + vectorxformat + "VY" + vectoryformat + "T" + directionformat;

                        if (i > 0) {
                            vcoords += "|";
                        }
                    }
                    
                    synchronized(attacksLock){
                        for(Attack a : attacks){
                            a.update();
                            a.checkCollision(this.players.values(), this.obstacles);
                        }
                        Iterator<Attack> iter = attacks.iterator();
                        while(iter.hasNext()){
                            if(iter.next().toRemove()){
                                iter.remove();
                            }
                        }
                        i = attacks.size();
                        for(Attack a : attacks){
                            i--;
                            attCoords += "X" + a.getX() + "Y" + a.getY() + "VX" + a.getVectorX() + "VY" + a.getVectorY() + "T" + a.getDirection();
                            if(i > 0){
                                attCoords += "|";
                            }
                        }
                        for (Map.Entry<String, Connexion> entry : connexions.entrySet()) {
                            if(attCoords.length() >  0){
                                entry.getValue().sendTick2(vcoords, attCoords);
                            }else{
                                entry.getValue().sendTick(vcoords);
                            }
    
                        }
                    }
                }
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
            createObjectifToValidPosition();
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
                    this.obstacles.clear();
                    synchronized(this.attacksLock){
                        this.attacks.clear();
                    }
                }
            }
        }
    }

    /**
     * Changes the current pos of the given player and checks whether he can collect the Objectif
     */
    public void changePos(String user, double x, double y){
        synchronized(userLock){
            synchronized(phaseLock){
                synchronized(objectifLock){
                    Player player = this.players.get(user);
                    player.moveTo(x, y);

                    // ==== Partie A ====
                    // if(this.objectif.isCollectableBy(player)){
                    //     player.setScore(player.getScore() + 1);
                    //     if(player.getScore() >= Constants.WIN_CAP){
                    //         endSession();
                    //     }else{
                    //         changeObjectif();
                    //     }
                    // }
                }
            }
        }
    }


    public void newCom(String user, double angle, int nb_thrust){
        synchronized(userLock){
            Player p = this.players.get(user);
            p.receiveAngleCommand(angle);
            p.receiveThrustCommand(nb_thrust);
        }
    }
    public void newCom2(String user, double angle, int nb_thrust, int shoot){
        synchronized(userLock){
            Player p = this.players.get(user);
            p.receiveAngleCommand(angle);
            p.receiveThrustCommand(nb_thrust);
            synchronized(attacksLock){
                this.attacks.add(new Attack(p));
                if(this.attacks.size() > Constants.MAX_SIMULTANEOUS_ATTACKS){
                    this.attacks.removeFirst();
                }
            }
        }
    }




    public void newMessage(String from, String message){
        synchronized(userLock){
            for(Map.Entry<String, Connexion> entry : connexions.entrySet()){
                if(!entry.getKey().equals(from)){
                    entry.getValue().sendNewMessage(message);
                }
            }
        }
    }   

    public void newPrivateMessage(String from, String to, String message){
        synchronized(userLock){
            Connexion c  = connexions.get(to);
            if(c != null){
                c.sendNewPrivateMessage(message, from);       
            }
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
        String obstacles_coords = "";
        synchronized(userLock){
            synchronized(phaseLock){
                if(this.phase.equals("ingame")){
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
                    i = obstacles.size();
                    for(Obstacle o : obstacles){
                        obstacles_coords += "X"+o.getX()+"Y"+o.getY();
                        i--;
                        if(i > 0){
                            obstacles_coords += "|";
                        }
                    }
                }
            }
        }

        
        c.sendConnectionAccepted(this.phase, scores, coord, obstacles_coords);
    }

    private void resetToValidPosition(Player newplayer) {
        boolean placementOK = false; // Check that the objectif is not inside an obstacle
        while (!placementOK) {
            newplayer.reset();
            placementOK = true;
            for (Obstacle o : this.obstacles) {
                if (o.isInCollisionWith(newplayer)) {
                    placementOK = false;
                    System.out.println("RETRY");
                    break;
                }
            }
            if (placementOK) {
                for (Player otherp : this.players.values()) {
                    if (!newplayer.equals(otherp)) {
                        if (newplayer.isInCollisionWith(otherp)) {
                            placementOK = false;
                            break;
                        }
                    }
                }
            }
        }
    }


    private void createObjectifToValidPosition() {
        boolean placementOK = false; // Check that the objectif is not inside an obstacle
        while (!placementOK) {
            this.objectif = new Objectif();
            placementOK = true;
            for (Obstacle o : this.obstacles) {
                if (o.isInCollisionWith(this.objectif.getX(), this.objectif.getY(), this.objectif.getRadius())) {
                    placementOK = false;
                    break;
                }
            }
        }
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