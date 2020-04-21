package com.example.tappybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bird {


    private static final int LOOK_UP_SPEED = 15;
    private static final int LOOK_DOWN_SPEED = 5;
    private int x;
    private int y;
    private int startX;
    private int startY;

    private int angle = 0;
    private Physics2D physics;
    private Bitmap bird;
    private Matrix rotator;

    public Bird(Resources r, int x, int y){
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.physics = new Physics2D();
        bird = BitmapFactory.decodeResource(r, R.drawable.bird);
        bird = Bitmap.createScaledBitmap(bird, 150, 132,false);
        rotator = new Matrix();
    }

    /**
     * Denna metod anropas i spelets update metod och den andropar fågelns fysikobjekts update
     * sedan räknas fågelns y värde ut och sedan fågelns rotation
     */
    public void update(){
        physics.update();
        y = physics.calculateY(y);
        calculateRotation();
    }

    /**
     * Denna metod räknar ut fågelns rotation och ändrar rotationen baserat på fågelns
     * y-hastighet.
     */
    private void calculateRotation() {
        if(physics.getVelocity() < 15){
            angle -= LOOK_UP_SPEED;
            if(angle <= -45){
                angle = -45;
            }
        } else{
            angle += LOOK_DOWN_SPEED;
            if(angle >= 45){
                angle = 45;
            }
        }
        rotator.setRotate(angle,bird.getWidth()/2f, bird.getHeight()/2f);
        rotator.postTranslate(x-bird.getWidth()/2f, y - bird.getHeight()/2f);
    }

    /**
     * Denna metod ritar fågeln med dess rotation på en canvas
     */
    public void draw(Canvas canvas){
        canvas.drawBitmap(bird, rotator, null);
    }

    /**
     * denna metod gör att fågeln hoppar genom att lägga till kraft till fågelns fysikmotor
     */
    public void jump(){
        physics.addForce(35);
    }

    /**
     * återställer variablerna x och y till deras startvärden
     */
    public void reset(){
        x = startX;
        y = startY;
    }

    /**
     * Returnerar värdet på variabeln x
     */
    public int getX(){
        return x;
    }

    /**
     * Returnerar värdet på variabeln y
     */
    public int getY(){
        return y;
    }

    /**
     * metod som anropar update för att fågeln ska falla till marken om
     * spelet är slut och fågeln inte befinner sig på marken
     */
    public void fallToGround() {
            this.update();
    }
}
