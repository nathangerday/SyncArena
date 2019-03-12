package game_elements;

import java.util.Random;
import java.lang.Math;

public class Objectif{
    private double x, y;
    private double radius;

    public Objectif(){
        Random r = new Random();
        this.x = (r.nextInt(200)/100.0) - 1;
        this.y = (r.nextInt(200)/100.0) - 1;
        this.radius = 0.05;
    }
    public Objectif(double x, double y){
        this.x = x;
        this.y = y;
        this.radius = 0.05;
    }

    public Objectif(double x, double y, double radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
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
}