package com.projects.nikita.killthemall;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by Nikita on 11/20/2015.
 */
public class Sprite {
    public static final int[] goodRes = {R.drawable.good1,R.drawable.good2,R.drawable.good3,
            R.drawable.good4,R.drawable.good5,R.drawable.good6};
    public static final int[] badRes = {R.drawable.bad1,R.drawable.bad2,R.drawable.bad3,
            R.drawable.bad4,R.drawable.bad5,R.drawable.bad6};
    //0 - down, 1 - left, 2 - right, 3 - up
    // animation = 3 back, 1 left, 0 front, 2 right
    int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };
    private final int BMP_COLUMNS = 3;
    private final int BMP_ROWS = 4;
    private final int MAX_SPEED = 5;
    private int x;
    private int xSpeed;
    private int y;
    private int ySpeed;
    private GameView gameView;
    private Bitmap image;
    private boolean isGood;

    private int width;
    private int height;
    private int currentBMP = 0;

    public boolean isGood() {
        return isGood;
    }

    public Sprite(GameView view, Bitmap bmp, boolean isGood){
        gameView = view;
        image = bmp;
        this.isGood = isGood;

        this.width = image.getWidth() / BMP_COLUMNS;
        this.height = image.getHeight() / BMP_ROWS;

        Random rnd = new Random();
        xSpeed = rnd.nextInt(2 * MAX_SPEED) - MAX_SPEED;
        ySpeed = rnd.nextInt(2 * MAX_SPEED) - MAX_SPEED;
        if(xSpeed == 0 && ySpeed == 0){
            xSpeed = rnd.nextInt(2 * MAX_SPEED) - MAX_SPEED;
            ySpeed = rnd.nextInt(2 * MAX_SPEED) - MAX_SPEED;
        }

        x = rnd.nextInt(view.getWidth() - width);
        y = rnd.nextInt(view.getHeight() - height);
    }

    private void update(){
        if (x > (gameView.getWidth() - width - xSpeed) || x + xSpeed <0){
            xSpeed = -xSpeed;
        }
        if(y > (gameView.getHeight() - height - ySpeed) || y + ySpeed < 0){
            ySpeed = -ySpeed;
        }


        currentBMP = ++currentBMP % BMP_COLUMNS;
        x+=xSpeed;
        y+=ySpeed;
    }

    public void onDraw(Canvas canvas){
        if (canvas != null){

            update();
            int srcX = currentBMP * width;
            int srcY = getAnimationRow() * height;
            Rect src = new Rect(srcX, srcY, srcX+width, srcY+height);
            Rect dst = new Rect(x, y, x+width, y+height);

            canvas.drawBitmap(image, src, dst, null);
        }
    }

    private int getAnimationRow(){
        double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
        int direction = (int) Math.round(dirDouble) % BMP_ROWS;
        return DIRECTION_TO_ANIMATION_MAP[direction];
    }

    public boolean isCollision(float x2, float y2){

        return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
    }
}
