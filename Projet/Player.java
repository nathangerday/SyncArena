import java.util.Random;

public class Player{
    private String username;
    private double x,y;

    public Player(String username){
        Random r = new Random();
        this.username = username;
        this.x = r.nextInt(200)/100 -1;
        this.y = r.nextInt(200)/100 -1;
    }

    public String getUsername(){
        return this.username;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

}