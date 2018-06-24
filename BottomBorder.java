package com.example.yadav.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Yadav on 24-Jun-18.
 */

public class BottomBorder extends GameObject
{
    private Bitmap image;

    public BottomBorder(Bitmap res, int x, int y)
    {
        height = 200;
        width = 20;

        this.x = x;
        this.y = y;

        dy = GameView.MOVESPEED;

        image = Bitmap.createBitmap(res, 0, 0, width, height);
    }

    public void update()
    {
        x += dx;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y,null);
    }
}
