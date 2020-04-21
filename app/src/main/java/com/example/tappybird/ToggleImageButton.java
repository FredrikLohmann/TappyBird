package com.example.tappybird;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class ToggleImageButton {


    private int x;
    private int y;
    private int width;
    private int height;
    private boolean toggled;

    private Bitmap toggledBitmap;
    private Bitmap notToggledBitmap;

    public ToggleImageButton(Resources r, int x, int y, int width, int height, boolean toggled, int toggledAsset, int notToggledAsset){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.toggled = toggled;

        toggledBitmap = BitmapFactory.decodeResource(r, toggledAsset);
        toggledBitmap = Bitmap.createScaledBitmap(toggledBitmap, width, height,false);

        notToggledBitmap = BitmapFactory.decodeResource(r, notToggledAsset);
        notToggledBitmap = Bitmap.createScaledBitmap(notToggledBitmap, width, height,false);
    }

    /**
     * Denna metod ritar knappen på dess position.
     * Beroende på om knappen är togglad eller inte så visas olika bilder
     */
    public void draw(Canvas canvas){
        if(toggled){
            canvas.drawBitmap(toggledBitmap,x,y,null);
        }
        else{
            canvas.drawBitmap(notToggledBitmap,x,y,null);
        }
    }

    /**
     * Get metod för toggled. Returnerar värdet på toggled
     */
    public boolean isToggled(){
        return toggled;
    }

    /**
     * Denna metod ändrar värdet på toggled till det värde det inte har
     * Är toggle true innan metoden anropas kommer den ha värdet false efteråt
     */
    public void toggle(){
        toggled = !toggled;
    }

    /**
     * Denna metod kontrollerar om det x och y värde i det medskickade argumentet event
     * befinner sig inne i knappen.
     * Om värdena är utanför knappen returneras false men om knappen trycks så returneras true
     */
    public boolean isPressed(MotionEvent event) {
        if(event.getX() > x && event.getY() > y && event.getX() < x+width && event.getY() < y+height){
            return true;
        }
        return false;
    }
}
