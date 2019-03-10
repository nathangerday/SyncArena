import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Session{
    private Map<String, Player> players = new HashMap<>();
    private Map<String, Connexion> connexions = new HashMap<>();
    private Objectif objectif = null;
    private Object userLock = new Object();
    private String phase = "inactive";
    private int delayBeforeStart = 10;


    public boolean addUser(String name, Connexion c){
        synchronized(userLock){
            if(!players.containsKey(name)){
                players.put(name, new Player(name));
                connexions.put(name, c);
                return true;
            }
            return false;
        }
    }

    public boolean connect(String name, Connexion c){
        if(addUser(name, c)){
            synchronized(this.phase){
                if(this.phase.equals("inactive")){
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

    private void scheduleStart(){
        ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();
                    
        Runnable task = new Runnable(){
            public void run() {
                start();
            }
        };

        sch.schedule(task, delayBeforeStart, TimeUnit.SECONDS);
    }


    public void start(){
        synchronized(this.phase){
            if(this.phase.equals("waiting")){
                //Start the game
                this.phase = "ingame";
                this.objectif = new Objectif();
            }else{
                return;
            }
        }
        synchronized(userLock){
            String coords = "";
            String coord = "";
            int i = players.size();
            DecimalFormat  sixdecimals = new DecimalFormat("#.######");

            for(Player p : players.values()){
                i--;
                Double xformat = Double.valueOf(sixdecimals.format(p.getX())); 
                Double yformat = Double.valueOf(sixdecimals.format(p.getY())); 
                coords += p.getUsername() + ":X" + xformat +"Y"+ yformat;
                if(i>0){
                    coords += "|";
                }
            }
            coord = "X"+this.objectif.getX()+"Y"+this.objectif.getY();
            for(Map.Entry<String, Connexion> entry : connexions.entrySet()){
                entry.getValue().sendStartSession(coords, coord);
            }
        }
    }

    

	public void disconnect(String username) {
        synchronized(userLock){
            this.players.remove(username);
            if(this.players.size() == 0){
                synchronized(phase){
                    this.phase = "inactive";
                    //TODO End Sessions if ingame
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
    

    private void connectionAccepted(Connexion c){
        String scores = "";
        String coord = "";
        synchronized(userLock){
            synchronized(phase){
                if(!this.phase.equals("ingame")){
                    scores = "";
                    coord = "";
                }else{
                    int i = players.size();
                    for(Player p : players.values()){
                        i--;
                        scores += p.getUsername() + ":" + p.getScore();
        
                        // Double xformat = Double.valueOf(sixdecimals.format(p.getX())); 
                        // Double yformat = Double.valueOf(sixdecimals.format(p.getY())); 
                        // coords += p.getUsername() + ":X" + xformat +"Y"+ yformat;
                        if(i>0){
                            scores += "|";
                        }
                    }
                    coord = "X"+this.objectif.getX()+"Y"+this.objectif.getY();
                }
            }
            
        }

        c.sendConnectionAccepted(this.phase, scores, coord);
    }

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