package com.projects.nikita.killthemall;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nikita on 11/19/2015.
 */
public class GameView extends SurfaceView {
    public interface GameOverListener{
        void Fatality(int score, int result);
    }

    GameOverListener gol;
    private Activity activity;
    private List<Sprite> sprites = new ArrayList<>();
    private List<TempSprite> temps = new ArrayList<>();
    private final SurfaceHolder holder;
    private Bitmap bloodBmp;
    private Bitmap background;
    private GameLoopThread gameLoop;
    private MyTimer mt;
    private long lastClick;

    public int getScore() {

        return score;
    }

    private int score = 0;


    public GameView(Activity activity) {
        super(activity);
        this.activity = activity;
        gameLoop = new GameLoopThread(this);
        mt = new MyTimer(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                createSprites();
                gameLoop.setRunning(true);
                gameLoop.start();
                mt.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoop.setRunning(false);
                mt.setRunning(false);
                while (retry) {
                    try {
                        gameLoop.join();
                        mt.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        bloodBmp = BitmapFactory.decodeResource(getResources(), R.drawable.blood1);
        gol = (GameOverListener) activity;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null) {

            canvas.drawBitmap(background, 0, 0, null);
            synchronized (sprites) {
                checkGame();
                for (int i = 0; i < temps.size(); i++) {
                    temps.get(i).onDraw(canvas);
                }
                for (Sprite s : sprites) {
                    s.onDraw(canvas);
                }
            }
        }
    }

    private void checkGame() {
        Log.d("mylog", "checkgame thread = " + Thread.currentThread().getName());
        if (sprites.size() > 21) gameOver(getScore(), -1);
        else if(sprites.size() == 0) gameOver(getScore(), getScore());
    }

    public Sprite createSprite(int res, boolean isGood) {
        return new Sprite(this, BitmapFactory.decodeResource(getResources(), res), isGood);
    }

    private void createSprites() {

        for (int i = 0; i < Sprite.badRes.length && i < Sprite.goodRes.length; i++) {
            sprites.add(createSprite(Sprite.badRes[i], false));
            sprites.add(createSprite(Sprite.goodRes[i], true));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (System.currentTimeMillis() - lastClick > 20) {
            lastClick = System.currentTimeMillis();
            Log.d("mylog", "onTouchEvent thread = " + Thread.currentThread().getName());
            synchronized (getHolder()) {
                for (int i = sprites.size() - 1; i >= 0; i--) {
                    Sprite sprite = sprites.get(i);
                    if (sprite.isCollision(event.getX(), event.getY())) {
                        temps.add(new TempSprite(temps, this, event.getX(), event.getY(), bloodBmp));
                        sprites.remove(sprite);
                        score++;
                       // break;
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    public void createNewSprite() {
        Random rnd = new Random();
        boolean isGood = rnd.nextBoolean();
        int rndChar = isGood ? Sprite.goodRes[rnd.nextInt(6)] : Sprite.badRes[rnd.nextInt(6)];
        synchronized (sprites) {
            sprites.add(createSprite(rndChar, isGood));

        }
    }

    public void gameOver(final int score, final int res) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gol.Fatality(score, res > 0 ? res : -1);
            }
        });

    }

    public void suspendGame(){
        gameLoop.suspendGame();
        mt.suspendCreation();
    }
    public void startGame(){
        gameLoop.startGame();
        mt.startCreation();
    }
    public void stopGame(){
        boolean retry = true;
        gameLoop.setRunning(false);
        mt.setRunning(false);
        while (retry) {
            try {
                gameLoop.join();
                mt.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void repeatGame(){
        score = 0;
        temps = new ArrayList<>();
        sprites = new ArrayList<>();
        createSprites();
        mt.refresh();
        startGame();
    }

}
