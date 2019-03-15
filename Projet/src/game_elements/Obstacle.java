package game_elements;

import java.util.Random;

import constants.Constants;

public class Obstacle{
    private double x, y;
    private double radius = Constants.OB_RADIUS;

    public Obstacle(){
        Random r = new Random();
        this.x = (r.nextInt(200)/100.0) - 1;
        this.y = (r.nextInt(200)/100.0) - 1;
    }

    public Obstacle(double x, double y){
        this.x = x;
        this.y = y;
    }
    

    public double getX(){
        return this.x;
    }
    
    public double getY(){
        return this.y;
    }

    public boolean isInCollisionWith(Player p){
        return  Math.sqrt(Math.pow((x - p.getX()), 2) + Math.pow(y - p.getY(), 2))  <  (this.radius + p.getRadius());
    }
    
    public boolean isInCollisionWith(double otherx, double othery, double otherradius){
        return  Math.sqrt(Math.pow((x - otherx), 2) + Math.pow(y - othery, 2))  <  (this.radius + otherradius);
    }
}