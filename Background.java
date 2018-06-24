package com.example.yadav.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.midi.MidiOutputPort;

/**
 * Created by Yadav on 23-Jun-18.
 */

public class Background
{
    private Bitmap image;
    private int x, y, dx;

    public Background(Bitmap res)
    {
        image = res;
        dx = GameView.MOVESPEED;
    }

    public void update()
    {
        x+=dx;
        if (x < -GameView.WIDTH)
        {
            x=0;
        }

    }
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
        if (x < 0)
        {
            canvas.drawBitmap(image, x+GameView.WIDTH,y,null);
        }

    }
}
