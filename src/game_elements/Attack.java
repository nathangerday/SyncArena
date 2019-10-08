package game_elements;

import java.util.Collection;

import constants.*;

public class Attack{
    private double x, y, vectorx, vectory;
    private double direction;
    private boolean toRemove;
    private Player origin;

    public Attack(Player origin){
        this.origin = origin;
        this.x = origin.getX();
        this.y = origin.getY();
        this.direction = origin.getDirection();
        this.vectorx = Constants.SHOOT_SPEED * Math.cos(direction);
        this.vectory = Constants.SHOOT_SPEED * Math.sin(direction);
        this.toRemove = false;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getDirection(){
        return this.direction;
    }

    public double getVectorX(){
        return this.vectorx;
    }

    public double getVectorY(){
        return this.vectory;
    }

    public boolean toRemove(){
        return this.toRemove;
    }

    public void update(){
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

    
    public void checkCollision(Collection<Player> players, Collection<Obstacle> obstacles){
        if(toRemove){
            return;
        }
        for(Obstacle o : obstacles){
            if(o.isInCollisionWith(this.x, this.y, Constants.SHOOT_RADIUS)){
                this.toRemove = true; // Collision d'une attaque avec un obstacle fait disparaitre l'attaque
                return;
            }
        }

        for(Player p : players){
            if(!p.equals(origin)){
                if(p.isInCollisionWith(x, y, Constants.SHOOT_RADIUS)){
                    // Si on est en collision avec un joueur, on stop le joueur et on disparait
                    this.toRemove= true;
                    p.reactToAttack();
                    return;
                }
            }
        }
    }
    
}