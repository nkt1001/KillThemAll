package com.projects.nikita.killthemall;

/**
 * Created by Nikita on 11/22/2015.
 */
public class MyTimer extends Thread {
    private GameView gameView;
    int sleepTime = 1000;
    boolean running;
    boolean suspended = true;
    public MyTimer(GameView gv) {
        gameView = gv;
        running = true;
    }
    public void refresh(){sleepTime = 1000;}
    public void suspendCreation(){suspended = true;}
    public synchronized void startCreation(){suspended = false; notify();}

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        try {
            while(running){
                synchronized (this) {
                    while (suspended) wait();
                }
                Thread.sleep(sleepTime);
                gameView.createNewSprite();
                if(sleepTime >= 610)
                    sleepTime -= 30;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
