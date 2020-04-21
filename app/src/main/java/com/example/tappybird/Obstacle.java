package com.example.tappybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Random;

public class Obstacle {

    private int screenHeight;
    private int x;
    private int y;
    private int speed;
    private int startX;

    private boolean canGiveScore;

    private int obstacleWidth;
    private int obstacleHeight;
    private int gapDistance;
    private int totalHeight;
    private int difference;

    private Bitmap upperObstacle;
    private Bitmap lowerObstacle;

    public Obstacle(Resources r, int screenWidth, int screenHeight, int startX, int speed){
        this.screenHeight = screenHeight;
         this.x = startX;
         this.startX = startX;
         this.y = 0;
         this.speed = speed;
         this.canGiveScore = true;
         obstacleWidth = screenWidth / 6;
         obstacleHeight = screenHeight / 2;
         gapDistance = screenHeight /4;
         totalHeight = obstacleHeight * 2 + gapDistance;
         difference = generateDifference();

        upperObstacle = BitmapFactory.decodeResource(r, R.drawable.upper_obstacle);
        upperObstacle = Bitmap.createScaledBitmap(upperObstacle, obstacleWidth, obstacleHeight,false);

        lowerObstacle = BitmapFactory.decodeResource(r, R.drawable.lower_obstacle);
        lowerObstacle = Bitmap.createScaledBitmap(lowerObstacle, obstacleWidth, obstacleHeight,false);
    }

    /**
     * Metod som räknar ut hindrets y-värde
     * Antingen uppe i mitten eller nere.
     */
    private int generateDifference() {
        Random rnd = new Random();
        int d = totalHeight - screenHeight;
        switch (rnd.nextInt(3)){
            case 0:
                return d;
            case 1:
                return (d /= 2);
            default:
                break;
        }
        return 0;
    }

    /**
     * Denna metod uppdaterar hindrens x-värde
     * om de befinner sig utanför skärmen så återställs de till sitt ursprungliga läge
     */
    public void update(){
        x -= speed;

        if(x < 0-obstacleWidth){
            reset();
        }
    }

    /**
     * Återställer hindrets värden till de den skapades med
     */
    public void reset() {
        x = startX;
        canGiveScore = true;
        difference = generateDifference();
    }

    /**
     * Ritar båda hindren på en canvas
     *
     * @param canvas canvasen som ska ritas på
     */
    public void draw(Canvas canvas){
        // Ritar övre hindret
        canvas.drawBitmap(upperObstacle ,x,y - difference,null);
        // Ritar undre hindret
        canvas.drawBitmap(lowerObstacle, x,obstacleHeight + gapDistance - difference,null);
    }

    /**
     * Kontrollerar ifall någon av hindren kolliderar med en cirkel. Returnerar true om de överlappar
     * och false ifall de inte gör det.
     *
     * @param cx cirkelns x koordinat
     * @param cy cirkelns y koordinat
     * @param radius cirkelns radie
     * @return returnerar true ifall cirkeln överlappar med någon av hindren
     */
    public boolean isColliding(int cx, int cy, int radius){

        int lowerObstacleTop = this.y + gapDistance + upperObstacle.getHeight()- difference;
        int deltaX = cx - Math.max(this.x, Math.min(cx,this.x + upperObstacle.getWidth()));
        int deltaY;

        // om cy är mindre än undre hindrets y - halva gapdistance så kontrolleras det övre hindret
        if (cy < lowerObstacleTop - gapDistance/2){
            deltaY = cy - Math.max(this.y, Math.min(cy,this.y - difference + upperObstacle.getHeight()));
        } else{ // annars kontrolleras det undre
            deltaY = cy - Math.max(lowerObstacleTop, Math.min(cy,lowerObstacleTop + lowerObstacle.getHeight()));
        }
        // returnerar ifall cirkeln överlappar
        return (deltaX * deltaX + deltaY * deltaY) < (radius * radius);
    }

    /**
     * Returnerar canGiveScore
     * @return returnerar canGiveScore
     */
    public boolean canGiveScore() {
        return canGiveScore;
    }

    /**
     * Set metod för canGiveScore
     * @param canGiveScore värdet canGiveScore ska ha
     */
    public void setCanGiveScore(boolean canGiveScore){
        this.canGiveScore = canGiveScore;
    }

    /**
     * Get metod för x
     * @return hindrets x position
     */
    public int getX(){
        return x;
    }

    /**
     * Get metod för obstacleWidth
     * @return hindrets bredd
     */
    public int getWidth(){
        return obstacleWidth;
    }
}
