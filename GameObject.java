package com.example.yadav.myfirstgame;

import android.graphics.Rect;

/**
 * Created by Yadav on 23-Jun-18.
 */

public abstract class GameObject
{
    protected int x;
    protected int y;
    protected int dx;
    protected int dy;
    protected int width;
    protected int height;

    public void setX(int x)
    {
        this.x = x;
    }
    public void setY(int x)
    {
        this.y = y;
    }

    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public int getWidth()
    {
        return width;
    }
    public int getHeight()
    {
        return height;
    }
    public Rect getRectangle()
    {
        return new Rect (x, y, x+width, y+height);
    }

}
