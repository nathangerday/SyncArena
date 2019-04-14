package game_elements;

import java.util.Collection;
import java.util.Random;
import constants.Constants;
public class Player{
    private Random r = new Random();
    private String username;
    private double x, y, vectorx, vectory;
    private int score;
    private double direction;
    private double radius = Constants.VE_RADIUS;

    private double oldX, oldY;
    private boolean isInCollision = false;

    public Player(String username){
        this.username = username;
        this.reset();
    }

    public void receiveAngleCommand(double angle){
        this.direction += angle;
    }

    public void receiveThrustCommand(int nb_thrust){
        this.vectorx = this.vectorx + (Constants.THRUSTIT * Math.cos(this.direction) * nb_thrust);
        this.vectory = this.vectory + (Constants.THRUSTIT * Math.sin(this.direction) * nb_thrust);

        this.vectorx = Math.min(Math.max(this.vectorx, -Constants.MAX_THRUST), Constants.MAX_THRUST);
        this.vectory = Math.min(Math.max(this.vectory, -Constants.MAX_THRUST), Constants.MAX_THRUST);


    }

    public void update(){
        this.oldX = x;
        this.oldY = y;
        double scale_to_client_rate =  Constants.REFRESH_TICKRATE / Constants.SERVER_TICKRATE;
        
        double newx = this.x + 1 + this.vectorx * scale_to_client_rate;
        double newy = this.y + 1 + this.vectory * scale_to_client_rate;
        if(newx < 0){
            this.x = 2 + (newx % 2) - 1;
        }else{
            this.x = newx % 2 - 1;
        }

        if(newy < 0){
            this.y = 2 + (newy % 2) - 1;
        }else{
            this.y = newy % 2 - 1;
        }
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

    public double getRadius(){
        return this.radius;
    }

    public double getVectorX(){
        return this.vectorx;
    }
    public double getVectorY(){
        return this.vectory;
    }

    public double getDirection(){
        return this.direction;
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int s){
        this.score = s;
    }

    public void moveTo(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void inverseVector(){
        this.vectorx = -this.vectorx;
        this.vectory = -this.vectory;
    }


    public void checkCollision(Collection<Player> players, Collection<Obstacle> obstacles){
        for(Obstacle o : obstacles){
            if(o.isInCollisionWith(this)){
                this.isInCollision = true;
                return;
            }
        }

        for(Player otherp : players){
            if(!otherp.equals(this)){
                if(this.isInCollisionWith(otherp)){
                    this.isInCollision = true;
                    return;
                }
            }
        }
    }

    public void reactToCollision(){
        if(isInCollision){
            this.moveTo(oldX, oldY);
            this.inverseVector();
            isInCollision = false;
        }
    }

    public boolean isInCollisionWith(Player p){
        return  isInCollisionWith(p.getX(), p.getY(), p.getRadius());
    }


    public boolean isInCollisionWith(double otherx, double othery, double otherradius){
        return  Math.sqrt(Math.pow((x - otherx), 2) + Math.pow(y - othery, 2))  <  (this.radius + otherradius);
    }


    public void reactToAttack(){
        this.vectorx = 0;
        this.vectory = 0;
    }

    public void reset(){
        this.x = (r.nextInt(200)/100.0) - 1;
        this.y = (r.nextInt(200)/100.0) - 1;
        this.oldX = x;
        this.oldY = y;
        this.score = 0;
        this.direction = r.nextFloat()*2*Math.PI;
        this.vectorx = 0;
        this.vectory = 0;
    }

}