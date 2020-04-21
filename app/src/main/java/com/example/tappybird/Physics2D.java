package com.example.tappybird;

public class Physics2D {

    private int acceleration;
    private int velocity = 55;

    /**
     * Denna metod anropas i spelets update metod och den
     * räknar ut spelarens hastighet baserat på dess acceleration
     */
    public void update(){
        velocity -= acceleration;

        velocity += 2;
        if (velocity > 35)
            velocity = 35;
        acceleration = 0;
    }

    /**
     * Denna metod räknar ut ett nytt värde på y baserat på gamla y värdet och hastigheten
     */
    public int calculateY(int oldY){
        int y = oldY + velocity;
        return y;
    }

    /**
     * Denna metod a återställer spelarens hastighet till 0
     * och sedan ökar accelerationen till värdet på force
     */
    public void addForce(int force){
        velocity = 0;
        acceleration = force;
    }

    /**
     * Get metod för velocity, returnerar värdet på velocity
     */
    public int getVelocity(){
        return velocity;
    }
}
