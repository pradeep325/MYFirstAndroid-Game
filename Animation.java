package com.example.yadav.myfirstgame;

import android.graphics.Bitmap;

/**
 * Created by Yadav on 23-Jun-18.
 */

public class Animation
{
    private Bitmap[] m_frames;
    private int m_currentFrames;
    private long m_startTime;
    private long m_delay;
    private boolean m_playedOnce;

    public void setFrames(Bitmap[] frames)
    {
        this.m_frames = frames;
        m_currentFrames = 0;
        m_startTime  = System.nanoTime();
    }

    public void  setDelay(long delay)
    {
        m_delay = delay;
    }

    public void setCurrentFrames(int currentFrames)
    {
        m_currentFrames = currentFrames;
    }

    public void update()
    {
        long elapsed = (System.nanoTime() - m_startTime)/1000000;

        if (elapsed > m_delay)
        {
            m_currentFrames++;
            m_startTime = System.nanoTime();
        }

        if (m_currentFrames == m_frames.length)
        {
            m_currentFrames = 0;
            m_playedOnce = true;
        }
    }

    public Bitmap getImage()
    {
        return m_frames[m_currentFrames];
    }

    public int getFrame()
    {
        return m_currentFrames;
    }

    public boolean playedOnce()
    {
        return m_playedOnce;
    }
}
