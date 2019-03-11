package game_elements;

import java.util.Random;

public class Player{
    private String username;
    private double x,y;
    private int score;
    private Random r = new Random();

    public Player(String username){
        this.username = username;
        this.x = (r.nextInt(200)/100.0) - 1;
        this.y = (r.nextInt(200)/100.0) - 1;
        this.score = 0;
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

    public void reset(){
        this.x = (r.nextInt(200)/100.0) - 1;
        this.y = (r.nextInt(200)/100.0) - 1;
        this.score = 0;
    }

}