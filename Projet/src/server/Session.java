package server;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import game_elements.*;
import game_elements.Attack;
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
    private List<Objectif> race_objectives = null;
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
                synchronized(userLock){
                    for(Map.Entry<String, Connexion> entry : connexions.entrySet()){
                        if(entry.getKey().equals(name)){
                            continue;
                        }
                        entry.getValue().sendNewPlayer(name);
                    }
                }
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
                        if (!phase.equals("ingame") && !phase.equals("ingame_race")) {
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
        synchronized (phaseLock) {
            this.obstacles.clear();
            synchronized(attacksLock){
                this.attacks.clear();
            }
            int nbObstacles = Constants.NB_OBSTACLES;
            for (int i = 0; i < nbObstacles; i++) {
                this.obstacles.add(new Obstacle());
            }
            synchronized (objectifLock) {
	            if (this.phase.equals("waiting")) {
	                this.phase = "ingame";
                    this.objectif = new Objectif(obstacles);
	            }else if(this.phase.equals("waiting_race")){
	                this.phase = "ingame_race";
	                this.race_objectives = new ArrayList<Objectif>();
	                for(int i = 0; i < Constants.WIN_CAP; i++){
	                    this.race_objectives.add(new Objectif(obstacles));
	                }
	            } else {
	                return;
	            }
            }
        }
        synchronized (userLock) {
            for (Player p : players.values()) {
                resetToValidPosition(p);
            }
            String coords = ProtocolManager.createPlayerCoords(players);
            String obstacles_coords = ProtocolManager.createObstaclesString(obstacles);
            synchronized (objectifLock) {
                for (Player p : players.values()){
                    String coord = ProtocolManager.createObjectivesString(phase, objectif, race_objectives, p);
                    connexions.get(p.getUsername()).sendStartSession(coords, coord, obstacles_coords);
                }
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

                    // Checking collision in 3 steps in necessary to have a precise output (we don't want to check with a player which is not yet updated)
                    for (Player p : players.values()) {
                        p.update();
                    }
                    for (Player p : players.values()){
                        p.checkCollision(this.players.values(), this.obstacles);
                    }
                    for(Player p : players.values()){
                        p.reactToCollision();
                        
                        boolean isOnObjectif = false;
                        if(this.phase.equals("ingame")){
                            isOnObjectif = this.objectif.isCollectableBy(p);
                        }else if(this.phase.equals("ingame_race")){
                            isOnObjectif = this.race_objectives.get(p.getScore()).isCollectableBy(p);
                        }

                        if(isOnObjectif){
                            p.setScore(p.getScore() + 1);
                            if(p.getScore() >= Constants.WIN_CAP){
                                endSession();
                                return;
                            }else{
                                changeObjectif();
                            }
                        }
                    }

                    String vcoords = ProtocolManager.createPlayerVCoords(players);
                    
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
                        String attCoords = ProtocolManager.createAttackString(attacks);
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
        synchronized(userLock){
            synchronized(objectifLock){
                String scores = ProtocolManager.createScoresString(players);
                if(this.phase.equals("ingame")){
                    this.objectif = new Objectif(obstacles);
                }
                for(Player player : players.values()){
                    String coord = ProtocolManager.createObjectivesString(phase, objectif, race_objectives, player);
                    connexions.get(player.getUsername()).sendNewObjectif(coord, scores);
                }
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
                if(this.phase.equals("ingame") || this.phase.equals("ingame_race")){
                    if(this.players.size() == 0){
                        this.phase = "inactive";
                    }else{
                        String scores = ProtocolManager.createScoresString(players);
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

    public void createRace(){
        synchronized(phaseLock){
            if(this.phase.equals("waiting")){
                this.phase = "waiting_race";
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
        synchronized(userLock){
            synchronized(phaseLock){
                if(this.phase.equals("ingame") || this.phase.equals("ingame_race")){
                    String scores = ProtocolManager.createScoresString(players);
                    String coord = "";
                    synchronized(objectifLock){
                        Player player = players.get(c.getUsername());
                        coord = ProtocolManager.createObjectivesString(phase, objectif, race_objectives, player);
                    }
                    String obstacles_coords = ProtocolManager.createObstaclesString(obstacles);
                    c.sendConnectionAccepted(this.phase, scores, coord, obstacles_coords);
                }
            }
        }
    }

    private void resetToValidPosition(Player newplayer) {
        boolean placementOK = false; // Check that the objectif is not inside an obstacle
        while (!placementOK) {
            newplayer.reset();
            placementOK = true;
            for (Obstacle o : this.obstacles) {
                if (o.isInCollisionWith(newplayer)) {
                    placementOK = false;
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
}