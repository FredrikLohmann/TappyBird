package com.example.tappybird;
/**
 * Denna applikation är en kopia på spelet flappy bird
 */

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Skapar en point där skärmens bredd och höjd ska sparas i
        Point screen = new Point();
        getWindowManager().getDefaultDisplay().getSize(screen); // Sparar skärmens dimensioner i screen
        // skapar en gameView
        gameView = new GameView(this, screen.x,screen.y);
        // lägger till gameViewn i denna view
        setContentView(gameView);
    }

    /**
     * anropas när appen öppnas igen
     */
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    /**
     * anropas när appen stängs
     */
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}
