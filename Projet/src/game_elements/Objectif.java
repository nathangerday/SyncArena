package game_elements;

import java.util.List;
import java.util.Random;
import java.lang.Math;
import constants.Constants;

public class Objectif{
    private double x, y;
    private double radius = Constants.OBJ_RADIUS;
    private Random r = new Random();


    public Objectif(){
        moveToRandomPos();
    }
    public Objectif(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Objectif(double x, double y, double radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Objectif(List<Obstacle> obstacles){
        // Check that the objectif is not inside an obstacle
        boolean placementOK = false; 
        while (!placementOK) {
            moveToRandomPos();
            placementOK = true;
            for (Obstacle o : obstacles) {
                if (o.isInCollisionWith(getX(), getY(), getRadius())) {
                    placementOK = false;
                    break;
                }
            }
        }
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

    public boolean isCollectableBy(Player p){
        return  Math.sqrt(Math.pow((x - p.getX()), 2) + Math.pow(y - p.getY(), 2))   < this.radius;
    }

    private void moveToRandomPos(){
        this.x = (r.nextInt(200)/100.0) - 1;
        this.y = (r.nextInt(200)/100.0) - 1;
    }
}