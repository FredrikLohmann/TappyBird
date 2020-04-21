package com.example.tappybird;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveLoadManager {

    private SharedPreferences sp;

    public SaveLoadManager(Context c) {
        sp = c.getSharedPreferences("tappyBirdPrefs", Context.MODE_PRIVATE);
    }

    /**
     * Denna metod returnerar en int som representerar den sparade highscoren
     */
    public int getSavedHighScore() {
        int result = sp.getInt("highScore", 0);
        return result;
    }

    /**
     * Denna metod sparar en int i en fil på enhetens lagringsutrymme
     */
    public  void saveHighScore(int highScore){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("highScore",highScore);
        editor.apply();
    }

    /**
     * Denna metod sparar två boolska värden i en fil
     * Dessa värden används för options så programmet ska veta om ljud
     * eller vibrationer ska användas
     */
    public void saveOptionPrefs(boolean vibrations, boolean sounds){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("vibrations",vibrations);
        editor.putBoolean("sounds",sounds);
        editor.apply();
    }

    /**
     * Denna metod returnerar det boolska värde för vibration som sparats i filen
     * om inget värde sparats tidigare så returneras true
     */
    public boolean isVibrationOn() {
        return sp.getBoolean("vibrations", true);
    }
    /**
     * Denna metod returnerar det boolska värde för ljudet som sparats i filen
     * om inget värde sparats tidigare så returneras true
     */
    public boolean isSoundOn() {
        return sp.getBoolean("sounds", true);
    }
}
