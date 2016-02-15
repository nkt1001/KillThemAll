package com.projects.nikita.killthemall;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by Nikita on 11/22/2015.
 */
public class TempSprite {
    private float x;
    private float y;
    private Bitmap bmp;
    private int life=15;
    List<TempSprite> temps;

    public TempSprite(List<TempSprite> list, GameView gv, float x, float y, Bitmap bmp) {
        this.x = Math.min(Math.max(x - bmp.getWidth() / 2, 0),
                gv.getWidth() - bmp.getWidth());
        this.y = Math.min(Math.max(y - bmp.getHeight() / 2, 0),
                gv.getHeight() - bmp.getHeight());
        this.bmp = bmp;
        this.temps = list;
    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(bmp, x, y, null);
    }

    private void update() {
        if (--life < 1) {
            temps.remove(this);
        }
    }
}
