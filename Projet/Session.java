import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Session{
    private Map<String, Player> players = new HashMap<>();
    private Map<String, Connexion> connexions = new HashMap<>();
    private Object userLock = new Object();

    private String phase = "inactive";
    private int delayBeforeStart = 20;


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
            c.sendConnectionAccepted();
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
        //TODO Check is there is at least one player
        synchronized(this.phase){
            if(this.phase.equals("waiting")){
                this.phase = "ingame";
                System.out.println("THE GAME IS STARTING");
                //Start the game
            }            
        }
    }

	public void disconnect(String username) {
        synchronized(players){
            this.players.remove(username);
            if(this.players.size() == 0){
                synchronized(phase){
                    this.phase = "inactive";
                }
            }
        }

	}
}