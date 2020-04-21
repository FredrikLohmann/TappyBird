package com.example.tappybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background {

    private int x, y;
    private Bitmap background;

    Background(Resources r, int screenX, int screenY){
        this.x = 0;
        this.y = 0;
        background = BitmapFactory.decodeResource(r, R.drawable.background);
        background = Bitmap.createScaledBitmap(background, screenX, screenY,false);
    }

    /**
     * Returnerar värdet på variabeln x
     */
    public int getX(){
        return x;
    }
    /**
     * Ändrar värdet på variabeln x
     */
    public void setX(int x){
        this.x = x;
    }
    /**
     * Returnerar värdet på variabeln y
     */
    public int getY() {
        return y;
    }

    /**
     * Returnerar värdet på variabeln bitmap
     */
    public Bitmap getBitmap() {
        return background;
    }
}
