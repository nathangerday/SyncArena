package server;

import java.text.DecimalFormat;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import constants.Constants;
import game_elements.Attack;
import game_elements.Objectif;
import game_elements.Obstacle;
import game_elements.Player;

/**
 * Classe permettant de créer des String respectant le protocoles à partir des données brutes du jeux
 */
public class ProtocolManager{
    
    
    private static DecimalFormat sixdecimals = new DecimalFormat("#.######"); // Permet de limiter le nombre de decimal pour les double

    private ProtocolManager(){}

    public static String createObjectivesString(String phase, Objectif objectif, List<Objectif> race_objectives, Player p) {
        String coord = "";
        if(phase.equals("jeu")){
            coord = "X" + objectif.getX() + "Y" + objectif.getY();
        }else if(phase.equals("ingame_race")){
            coord = "X" + race_objectives.get(p.getScore()).getX() + "Y" + race_objectives.get(p.getScore()).getY();
            if(p.getScore() < Constants.WIN_CAP - 1){
                coord += "|";
                coord += "X" + race_objectives.get(p.getScore()+1).getX() + "Y" + race_objectives.get(p.getScore()+1).getY();
            }
        }
        return coord;
    }

    public static String createPlayerCoords(Map<String, Player> players){
        String coords = "";
        int i = players.size();
        for (Player p : players.values()) {
            i--;
            Double xformat = Double.valueOf(sixdecimals.format(p.getX()));
            Double yformat = Double.valueOf(sixdecimals.format(p.getY()));
            coords += p.getUsername() + ":X" + xformat + "Y" + yformat;
            if (i > 0) {
                coords += "|";
            }
        }
        return coords;
    }

    public static String createPlayerVCoords(Map<String, Player> players){
        String vcoords = "";
        int i = players.size();
        for(Player p : players.values()){
            i--;
            Double xformat = Double.valueOf(sixdecimals.format(p.getX()));
            Double yformat = Double.valueOf(sixdecimals.format(p.getY()));
            Double vectorxformat = Double.valueOf(sixdecimals.format(p.getVectorX()));
            Double vectoryformat = Double.valueOf(sixdecimals.format(p.getVectorY()));
            Double directionformat = Double.valueOf(sixdecimals.format(p.getDirection()));
            vcoords += p.getUsername() + ":X" + xformat + "Y" + yformat + "VX" + vectorxformat + "VY" + vectoryformat + "T" + directionformat;
    
            if (i > 0) {
                vcoords += "|";
            }
        }

        return vcoords;
        
    }

    public static String createAttackString(Deque<Attack> attacks){
        String attCoords = "";
        int i = attacks.size();
        for(Attack a : attacks){
            i--;
            attCoords += "X" + a.getX() + "Y" + a.getY() + "VX" + a.getVectorX() + "VY" + a.getVectorY() + "T" + a.getDirection();
            if(i > 0){
                attCoords += "|";
            }
        }
        return attCoords;
    }

    public static String createObstaclesString(List<Obstacle> obstacles){
        String obstacles_coords = "";
        int i = obstacles.size();
        for (Obstacle o : obstacles) {
            obstacles_coords += "X" + o.getX() + "Y" + o.getY();
            i--;
            if (i > 0) {
                obstacles_coords += "|";
            }
        }

        return obstacles_coords;
    }

    public static String createScoresString(Map<String, Player> players){
        String scores = "";
        int i = players.size();
        for(Player player : players.values()){
            i--;
            scores += player.getUsername() + ":" + player.getScore();
            if(i>0){
                scores += "|";
            }
        }
        return scores;
    }

}