package com.example.tappybird;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ScrollingBackground {
    private int speed;
    private int screenWidth;
    private int screenHeight;
    private Background background1, background2;
    ScrollingBackground(Resources r, int screenWidth, int screenHeight, int speed) {
        this.speed = speed;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        background1 = new Background(r, screenWidth,screenHeight);
        background2 = new Background(r, screenWidth,screenHeight);
        background2.setX(screenWidth);
    }

    /**
     * Denna metod anropas i spelets update metod.
     * Den flyttar de båda bilderna i scrolling background åt vänster för att simulera att
     * spelaren rör sig
     */
    public void update(){
        background1.setX(background1.getX() - speed);
        background2.setX(background2.getX() - speed);

        checkAndMoveBackBackgrounds();
    }

    /**
     * Denna metod används för att kontrollera om någon av bilderna flyttats utanför skärmen
     * (när deras x värde och dess bredd är lägre än 0)
     * när det häner så flyttas bilden tillbaka till andra sidan av skärmen för att
     * den ska fortsätta scrolla
     */
    private void checkAndMoveBackBackgrounds() {
        if(background1.getX() + background1.getBitmap().getWidth() <= 0){
            background1.setX(screenWidth);
        }
        if(background2.getX() + background2.getBitmap().getWidth() <= 0){
            background2.setX(screenWidth);
        }
    }

    /**
     * Denna metod ritar de två bakgrunderna på en canvas
     */
    public void draw(Canvas canvas) {
        canvas.drawBitmap(background1.getBitmap(),background1.getX(),background1.getY(), null);
        canvas.drawBitmap(background2.getBitmap(),background2.getX(),background2.getY(), null);
    }
}
