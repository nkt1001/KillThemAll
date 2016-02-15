package com.projects.nikita.killthemall;

import android.graphics.Canvas;

/**
 * Created by Nikita on 11/19/2015.
 */
public class GameLoopThread extends Thread {
    private GameView gameView;
    private boolean running;
    private final int FPS = 10;
    boolean suspended = true;

    public void suspendGame(){suspended = true;}
    public synchronized void startGame(){suspended = false; notify();}

    public GameLoopThread(GameView gv){

        gameView = gv;
    }

    public void setRunning(boolean running) {

        this.running = running;
    }

    @Override
    public void run() {

        long ticksPS = 1000/FPS;
        long startTime;
        long sleepTime;

        while (running){

            Canvas c = null;
            startTime = System.currentTimeMillis();
            try{
            c = gameView.getHolder().lockCanvas();
            synchronized (gameView.getHolder()) {
                gameView.onDraw(c);
            }} finally {
                if(c != null) gameView.getHolder().unlockCanvasAndPost(c);
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
                try {
                if (sleepTime > 0) {
                    sleep(sleepTime);
                } else {
                    sleep(10);
                }
                    synchronized (this) {
                        while (suspended) wait();
                    }
            }
            catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
    }
}
