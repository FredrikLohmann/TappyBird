package com.example.tappybird;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2500;
    private Animation animation;
    private ImageView birdImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // initirar variabler
        birdImage = findViewById(R.id.birdImage);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bird_anim);

        animateSplashScreen();
        startMainActivity();
    }

    /**
     * startar animationen för fågelbilden
     */
    private void animateSplashScreen() {
        birdImage.startAnimation(animation);
    }

    /**
     * Metod som startar spelaktiviteten efter en viss tid
     */
    private void startMainActivity() {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(),GameActivity.class);
                startActivity(intent);
                finish();

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
