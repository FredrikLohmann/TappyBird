package com.example.tappybird;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

public class GameView extends SurfaceView implements Runnable{

    private static final int FPS = 60;
    private int targetWaitTime = 1000/FPS;

    // skärmens storlek
    private int screenWidth;
    private int screenHeight;

    // variabler som håller reda på speltes tillstånd
    private boolean isRunning;
    private boolean isPlaying;
    private boolean isPaused;
    private boolean hasCollided;

    // Används för att avgöra om spelaren krockat med tak eller golv.
    private int gameRoof;
    private int gameFloor;

    // Spelarens poäng
    private int score = 0;
    private int highScore;

    private Thread thread;
    private SaveLoadManager slm;
    private ScrollingBackground background;
    private Obstacle[] obstacles;
    private Bird player;
    private Paint whitePaint;

    // Ljud
    private MediaPlayer flap;
    private MediaPlayer crash;
    private MediaPlayer fall;

    // för options
    private boolean vibrationOn;
    private boolean soundOn;
    private ToggleImageButton vibrationsOnBtn;
    private ToggleImageButton soundOnBtn;

    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        slm = new SaveLoadManager(context);
        highScore = slm.getSavedHighScore();
        vibrationOn = slm.isVibrationOn();
        soundOn = slm.isSoundOn();

        initObjects();
        initPaints();

        gameRoof = 100;
        gameFloor = screenHeight-100;
    }

    /**
     * Metod som initierar objekt
     */
    private void initObjects() {
        background = new ScrollingBackground(getResources(), screenWidth, screenHeight, 5);
        obstacles = new Obstacle[1];
        obstacles[0] = new Obstacle(getResources(), screenWidth, screenHeight, screenWidth, 13);
        //obstacles[1] = new Obstacle(getResources(), screenWidth, screenHeight, screenWidth, 13);
        player = new Bird(getResources(),screenWidth/3, screenHeight/2);

        vibrationsOnBtn = new ToggleImageButton(getResources(), 0,0,screenWidth/10,screenWidth/10, vibrationOn,
                R.drawable.ic_vibration_on,R.drawable.ic_vibration_off);
        soundOnBtn = new ToggleImageButton(getResources(), (screenWidth/10) * 9,0,screenWidth/10,screenWidth/10, soundOn,
                R.drawable.ic_sound_on,R.drawable.ic_sound_off);
    }

    /**
     * Metod som initierar färger
     */
    private void initPaints() {
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextAlign(Paint.Align.CENTER);
        whitePaint.setTextSize(128);
    }

    /**
     * Initierar alla ljudklipp
     */
    private void initMediaPlayers(){
        flap = MediaPlayer.create(getContext(), R.raw.flap_sound);
        crash = MediaPlayer.create(getContext(), R.raw.crash_sound);
        fall = MediaPlayer.create(getContext(), R.raw.fall_sound);
        crash.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // om spelaren inte nuddar golvet ska ljudet för fallet spelas
                if (player.getY() < gameFloor)
                    playMedia(fall);
            }
        });
    }

    /**
     * Releasar alla mediaplayers, anropas vid onPause
     */
    private void releaseMediaPlayers(){
        flap.release();
        crash.release();
        fall.release();
    }

    /**
     * Denna metod innehåller själva spelloopen och anropar update och draw
     * sedan räknas tiden ut som tråden ska sova för att metoderna ska anropas inom targetfps
     */
    @Override
    public void run() {
        while (isRunning){
            // Kollar systemklockan för att senare kunna räkna ut hur länge tråden ska sova
            long startTime = System.nanoTime();
            update();
            draw();
            // Kollar hur lång tid update och draw tagit
            long timeMillis = (System.nanoTime() - startTime) / 1000000;
            long waitTime = targetWaitTime - timeMillis;
            sleep(waitTime);
        }
    }

    /**
     * Uppdaterar alla objekt i spelet.
     * kontrollerar även om spelaren ska få poäng eller om den krashat
     */
    private void update() {
        if (!isPaused){
            background.update();
            if (isPlaying){
                for (Obstacle o : obstacles)
                    o.update();
            }
            player.update();

            // om spelaren inte spelar så håller sig fågeln vid mitten
            if(!isPlaying){
                if (player.getY() > screenHeight /2){
                    player.jump();
                }
            }
        }

        checkScore();
        checkCollisions();
    }

    /**
     * Metoden kollar om spelaren ska få poäng eller inte
     */
    private void checkScore() {
        for (Obstacle o : obstacles){
            if (o.canGiveScore() && o.getX() + o.getWidth() < player.getX()){
                score++;
                o.setCanGiveScore(false);
            }
        }
    }

    /**
     * Metoden kollar om spelaren krockar och anropar andra metoder beroende på
     */
    private void checkCollisions() {
        if (isColliding()){
            gameOver();
        }
        if (hasCollided){
            if (player.getY() < gameFloor)
                player.fallToGround();
        }
    }

    /**
     * Metoden kontrollerar om spelaren kolliderar med något
     *
     * @return true om spelaren kolliderar med antingen taket, golvet eller ett hinder
     */
    private boolean isColliding() {
        boolean isTouchingRoof = player.getY() < gameRoof;
        boolean isTouchingFloor = player.getY() > gameFloor;
        boolean isTouchingObstacle = false;
        for (Obstacle o : obstacles){
            if (o.isColliding(player.getX()-5, player.getY() + 10, 55)){
                isTouchingObstacle = true;
            }
        }
        return isTouchingObstacle || isTouchingRoof || isTouchingFloor;
    }

    /**
     * Anropas när spelet är över
     */
    private void gameOver() {
        if (!hasCollided){
            if (vibrationOn)
                vibrate();
            if (soundOn)
                playMedia(crash);
        }
        isPaused = true;
        hasCollided = true;
        saveHighScore();
    }

    /**
     * Får enheten att vibrera, tar hänsyn till äldre versioner av android
     */
    private void vibrate() {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
        else{
            v.vibrate(250);
        }
    }

    /**
     * Kontrollerar om spelaren slagit nytt rekord. Om så är fallet sparas det nya rekordet.
     */
    private void saveHighScore() {
        if (score > highScore){
            highScore = score;
            slm.saveHighScore(highScore);
        }
    }

    /**
     * Metod som anropas i själva spel-loopen.
     * Ser till att rita alla komponenter på canvasen.
     */
    private void draw() {
        if (getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();

            drawGameObjects(canvas);
            drawTexts(canvas);
            drawUI(canvas);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Metoden ritar alla spelobjekt på en kanvas
     *
     * @param canvas den canvas som spelobjekten ska ritas på
     */
    private void drawGameObjects(Canvas canvas) {
        background.draw(canvas);
        for (Obstacle o : obstacles){
            o.draw(canvas);
        }
        player.draw(canvas);
    }

    /**
     * Metoden ritar de textelement som ska visas på canvasen
     *
     * @param canvas den canvas textelementen ska ritas på
     */
    private void drawTexts(Canvas canvas) {
        if (isPlaying && !hasCollided)
            canvas.drawText("" + score, screenWidth/2f, screenHeight / 4f, whitePaint);
        if(!isPlaying){
            canvas.drawText("Highscore: " + highScore, screenWidth/2f, screenHeight / 4f, whitePaint);
            canvas.drawText("Tap to start", screenWidth/2f, (screenHeight / 3f)*2, whitePaint);
        }
        if (hasCollided && player.getY() >= gameFloor){
            canvas.drawText("Your score is: " + score, screenWidth/2f, screenHeight / 4f, whitePaint);
            canvas.drawText("Tap to play again", screenWidth/2f, (screenHeight / 2f), whitePaint);
        }
    }

    /**
     * Ritar alla UI element på en canvas
     *
     * @param canvas den canvas UI elementen ska ritas på
     */
    private void drawUI(Canvas canvas){
        if(!isPlaying){
            vibrationsOnBtn.draw(canvas);
            soundOnBtn.draw(canvas);
        }
    }

    /**
     * Anropas när användaren rör skärmen och anropar andra metoder beroende på vilket tillstånd spelet befinner sig i
     * och beroende på var på skärmen användaren klickar
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (event.getY() > screenHeight / 9 && !pointerOnUI(event)){
                    if ( !isPaused){ // om Spelaren spelar
                        handlePlayerInput();
                    }
                    if (!hasCollided){ // spelet har pausats och ska nu återupptas
                        isPaused = false;
                        isPlaying = true;
                    } else if(player.getY() >= gameFloor) // spelaren är död och vill spela igen
                        reset();
                }else if (pointerOnUI(event)){
                    // spelaren trycker på vibrationsknappen
                    if(vibrationsOnBtn.isPressed(event)){
                        vibrationsOnBtn.toggle();
                        vibrationOn = vibrationsOnBtn.isToggled();
                    }
                    // spelaren trycker på ljudknappen
                    if(soundOnBtn.isPressed(event)){
                        soundOnBtn.toggle();
                        soundOn = soundOnBtn.isToggled();
                    }
                    // spelarens preferenser sparas
                    slm.saveOptionPrefs(vibrationOn,soundOn);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Kontrolleran om spelaren tryckt på ett UI element
     *
     * @param event Används för att se var spelaren tryckt
     * @return true om spelaren tryckt på ett UI element
     */
    private boolean pointerOnUI(MotionEvent event) {
        return !isPlaying && vibrationsOnBtn.isPressed(event) || soundOnBtn.isPressed(event);
    }

    /**
     * Återställer spelet så att spelaren kan spela igen
     */
    private void reset() { // bör kanske skapa respawn metod i klasserna
        background = new ScrollingBackground(getResources(), screenWidth, screenHeight, 5);
        for (Obstacle o : obstacles){
            o.reset();
        }
        player.reset();
        isPaused = false;
        isPlaying = false;
        hasCollided = false;
        score = 0;
    }

    /**
     * Metoden anropar spelarobjektets jumo funktion och spelar ljudet för vingslag
     */
    private void handlePlayerInput() {
        player.jump();
        playMedia(flap);
    }

    /**
     * Spelar upp ljud från mediaspelare,
     * om ljud redan spelas så stoppas det och spelas från början
     * @param mp medianspelaren som ska spela
     */
    private void playMedia(MediaPlayer mp){
        if (soundOn){
            if (mp.isPlaying()){
                mp.stop();
            }
            mp.start();
        }
    }

    /**
     * Metod som får tråden att sova för att nå targetWaitTime
     *
     * @param sleepTime den tid tråden ska sova
     */
    private void sleep(long sleepTime) {
        if (sleepTime < 0){
            return;
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Del av android applikationers livscykel. Anropas när användaren går ifrån appen
     * spelet pausas då och tråden joinas
     */
    public void pause() {
        try {
            if (isPlaying){
                isPaused = true;
                isPlaying = false;
            }
            isRunning = false;
            releaseMediaPlayers();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Del av android applikationers livscykel. Anropas när användaren öppnar appen igen och när
     * aktiviteten startas
     *
     * om spelaren kolliderat så återställs spelet sen skapas en ny tråd och programmet körs
     */
    public void resume() {
        if(hasCollided){
            reset();
        }
        isRunning = true;

        initMediaPlayers();
        thread = new Thread(this);
        thread.start();
    }
}
