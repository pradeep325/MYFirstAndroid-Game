package com.example.yadav.myfirstgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Yadav on 24-Jun-18.
 */

public class SmokePuff extends GameObject
{
    public int radius;
    public SmokePuff(int x, int y)
    {
        radius = 5;
        super.x = x;
        super.y = y;
    }

    public void update()
    {
        x-=10;
    }

    public void draw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x-radius, y-radius, radius,paint);
        canvas.drawCircle(x-radius+2, y-radius-2, radius, paint);
        canvas.drawCircle(x-radius+4, y-radius-4, radius, paint);

    }

}
