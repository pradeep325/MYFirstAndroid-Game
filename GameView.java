package com.example.yadav.myfirstgame;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Yadav on 10-Jun-18.
 */



public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;
    private long smokeStartTimer;
    private long missilesStartTime;
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    private int progressDenom = 20;
    private  boolean newGameCreated;

    private Player player;
    private MainThread thread;
    private Background bg;
    private ArrayList<SmokePuff> smoke;
    private ArrayList<Missile> missiles;
    private ArrayList<TopBorder> topborder;
    private ArrayList<BottomBorder> bottomborder;
    Random rand = new Random();
    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean disappear;
    private boolean started;
    private int bestScore;

    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        bg = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.helicopter), 65, 35, 3);
        smoke = new ArrayList<SmokePuff>();
        missiles = new ArrayList<Missile>();
        topborder = new ArrayList<TopBorder>();
        bottomborder = new ArrayList<BottomBorder>();

        thread = new MainThread(getHolder(), this);

        smokeStartTimer = System.nanoTime();
        missilesStartTime = System.nanoTime();

        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000)
        {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction()==MotionEvent.ACTION_DOWN)
        {
            if (!player.getPlaying() && newGameCreated && reset ) {
                player.setPlaying(true);
                player.setUp(true);
            }
            if (player.getPlaying())
            {
                if (!started)started = true;
                reset = false;
                player.setUp(true);
            }
            return true;
        }


        if (event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);

    }


    @Override
    public void draw(Canvas canvas)
    {
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);
        if (canvas != null)
        {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX,scaleFactorY);
            bg.draw(canvas);
            if (!disappear)
            {
                player.draw(canvas);
            }
            player.draw(canvas);

            for (SmokePuff sp: smoke)
            {
                sp.draw(canvas);
            }

            for (Missile m: missiles)
            {
                m.draw(canvas);
            }

            for (TopBorder tb: topborder)
            {
                tb.draw(canvas);
            }

            for (BottomBorder bb: bottomborder)
            {
                bb.draw(canvas);
            }
            if (started)
            {
                explosion.draw(canvas);
            }
            drawText(canvas);
            canvas.restoreToCount(savedState);

        }

    }

    public boolean collision(GameObject objA, GameObject ObjB)
    {
        if (Rect.intersects(objA.getRectangle(), ObjB.getRectangle()))
        {
            return true;
        }
        return false;
    }

    public void update()
    {
        if (player.getPlaying())
        {
            if (bottomborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }

            if (topborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }

            bg.update();
            player.update();

            maxBorderHeight = 30+player.getScore()/progressDenom;
            if (maxBorderHeight > HEIGHT/4)maxBorderHeight = HEIGHT/4;
            minBorderHeight = 5+player.getScore()/progressDenom;


            for (int i = 0; i < topborder.size(); i++)
            {
                if (collision(topborder.get(i), player))
                {
                    player.setPlaying(false);
                }
            }


            for (int i = 0; i < bottomborder.size(); i++)
            {
                if (collision(bottomborder.get(i), player))
                {
                    player.setPlaying(false);
                }
            }

            this.updateBottomBorder();
            this.updateTopBorder();

            long missilesElapsed = (System.nanoTime()-missilesStartTime)/1000000;
            if (missilesElapsed > (2000 - player.getScore()/4))
            {
                if (missiles.size()==0)
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, HEIGHT/2, 45,15, player.getScore(),13));
                }
                else
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (int) (rand.nextDouble()*(HEIGHT - (maxBorderHeight*2)) + maxBorderHeight), 45,15, player.getScore(),13));
                }

                missilesStartTime = System.nanoTime();
            }

            for (int i = 0; i < missiles.size(); i++)
            {
                missiles.get(i).update();
                if (collision(missiles.get(i), player))
                {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }

                if (missiles.get(i).getX() < -100)
                {
                    missiles.remove(i);
                    break;
                }
            }


            long elapsed = (System.nanoTime()-smokeStartTimer)/1000000;
            if (elapsed > 120)
            {
                smoke.add(new SmokePuff(player.getX(), player.getY()+10));
                smokeStartTimer = System.nanoTime();
            }
            for (int i=0; i < smoke.size(); i++)
            {
                smoke.get(i).update();
                if (smoke.get(i).getX() < -10)
                {
                    smoke.remove(i);
                }
            }
        }
        else
        {
            player.resetDY();
            if (!reset)
            {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                disappear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion), player.getX(),
                        player.getY()- 30, 100, 100, 25);
            }

            explosion.update();
            long resetElapsed = (System.nanoTime() - startReset)/1000000;

            if (resetElapsed > 2500 && !newGameCreated)
            {
                newGame();
            }

        }
    }

    public void updateTopBorder()
    {
        if(player.getScore()%50 == 0)
        {
            topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                    , topborder.get(topborder.size()-1).getX()+20, 0, (int) ((rand.nextDouble()*(maxBorderHeight ))+1)));
        }

        for(int i = 0; i < topborder.size(); i++)
        {
            topborder.get(i).update();
            if (topborder.get(i).getX() < -20)
            {
                topborder.remove(i);
                if (topborder.get(topborder.size()-1).getHeight()>=maxBorderHeight)
                {
                    topDown = false;
                }

                if (topborder.get(topborder.size()-1).getHeight() <= minBorderHeight)
                {
                    topDown = true;
                }

                if (topDown)
                {
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick), topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()-1).getHeight()+1));
                }
                else
                {
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()-1).getHeight()-1));
                }



            }
        }

    }

    public void updateBottomBorder()
    {
        if (player.getScore()%40 == 0)
        {
            bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    bottomborder.get(bottomborder.size() - 1).getX() + 20,
                    (int) ((rand.nextDouble() * (maxBorderHeight)) + (HEIGHT - maxBorderHeight))));
        }

        //update bottom border
        for(int i = 0; i<bottomborder.size(); i++) {
            bottomborder.get(i).update();

            //if border is moving off screen, remove it and add a corresponding new one
            if (bottomborder.get(i).getX() < -20) {
                bottomborder.remove(i);


                //determine if border will be moving up or down
                if (bottomborder.get(bottomborder.size() - 1).getY() <= HEIGHT - maxBorderHeight) {
                    botDown = true;
                }
                if (bottomborder.get(bottomborder.size() - 1).getY() >= HEIGHT - minBorderHeight) {
                    botDown = false;
                }

                if (botDown) {
                    bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), bottomborder.get(bottomborder.size() - 1).getX() + 20, bottomborder.get(bottomborder.size() - 1
                    ).getY() + 1));
                } else {
                    bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), bottomborder.get(bottomborder.size() - 1).getX() + 20, bottomborder.get(bottomborder.size() - 1
                    ).getY() - 1));
                }
            }
        }
    }

    public void newGame()
    {
        disappear = false;
        bottomborder.clear();
        topborder.clear();
        missiles.clear();
        smoke.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.resetDY();

        player.setY(HEIGHT/2);

        if (player.getScore() > bestScore)
        {
            bestScore = player.getScore();
        }

        player.resetScore();

        for (int i = 0; i*20 < WIDTH+40; i++)
        {
            if (i==0)
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        , i*20,0,10));
            }
            else
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        , i*20,0,topborder.get(i-1).getHeight()+1));
            }
        }

        for (int i = 0; i*20 < WIDTH +40; i++)
        {
            if (i ==0)
            {
                bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        ,i*20,HEIGHT-minBorderHeight));
            }
            else
            {
                bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        ,i*20, bottomborder.get(i-1).getY()-1));
            }

            newGameCreated = true;
        }
    }

    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + (player.getScore()*3), 10, HEIGHT-10, paint);
        canvas.drawText("BEST: " + bestScore, WIDTH - 215, HEIGHT -10,paint);

        if (!player.getPlaying()&& newGameCreated && reset)
        {
            Paint paint1 = new Paint();
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH/2-50, HEIGHT/2,paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2+20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2+40, paint1);
        }
    }
}