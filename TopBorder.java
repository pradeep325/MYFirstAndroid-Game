package com.example.yadav.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * Created by Yadav on 24-Jun-18.
 */

public class TopBorder extends GameObject
{
    private Bitmap image;

    public TopBorder(Bitmap res, int x, int y, int h)
    {
        height = h;
        width = 20;

        this.x = x;
        this.y = y;

        dx = GameView.MOVESPEED;
        image = Bitmap.createBitmap(res, 0, 0, width, height);
    }

    public void update()
    {
        x+=dx;
    }

    public void draw(Canvas canvas)
    {
        try {
            canvas.drawBitmap(image, x, y, null);
        }catch (Exception e){}
    }

}
