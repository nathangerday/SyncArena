package constants;

public final class Constants{
    private Constants(){}

    // ======= SAFE TO CHANGE =======

    public static final int PORT = 45678;
    public static final int DELAY_BEFORE_START = 15; // Attente en secondes avant le lancement de la partie.
    public static final int MAX_SIMULTANEOUS_ATTACKS = 30;
    public static final int NB_OBSTACLES = 15;

    //  ======= MUST BE IDENTICAL TO SERVER =======
    public static final int REFRESH_TICKRATE = 30;
    public static final int SERVER_TICKRATE = 10;
    public static final double MAX_THRUST = 0.01;
    public static final double TURNIT = 0.06;
    public static final double THRUSTIT = 0.002;
    public static final double OBJ_RADIUS = 0.05;
    public static final double SHOOT_RADIUS = 0.01;
    public static final double SHOOT_SPEED = 0.01;
    public static final double VE_RADIUS = 0.04;
    public static final double OB_RADIUS = 0.08;
    public static final int WIN_CAP = 3;
    

}
