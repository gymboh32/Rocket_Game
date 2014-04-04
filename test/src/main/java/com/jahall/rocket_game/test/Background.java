package com.jahall.rocket_game.test;

/**
 * Created by jahall on 3/2/14.
 */

import java.util.ArrayList;

import java.util.List;

import java.util.Random;

import android.app.DialogFragment;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Camera;
import android.graphics.Color;

import android.graphics.Matrix;
import android.graphics.Paint;

import android.graphics.Point;

import android.graphics.Rect;
import android.util.AttributeSet;

import android.view.View;
import android.webkit.WebHistoryItem;

public class Background extends View
{
    private Paint p;
    private Paint g;
    private List<Point> starField = null;
    private int starAlpha = 200;
    private Rect shipBounds = new Rect(0,0,0,0);
    private Point ship;
    private Bitmap bmShip = null;
    private Matrix mMatrix = null;
    private static final int NUM_OF_STARS = 50;

    public Background (Context context, AttributeSet aSet)
    {
        super(context, aSet);

        //it's best not to create any new objects in the on draw
        //initialize them as class variables here
        p = new Paint();
        g = new Paint();

        ship = new Point(-1, -1);

        //point to ship image
        bmShip = BitmapFactory.decodeResource(getResources(), R.drawable.spaceship1);
        //scale ship size
        bmShip = Bitmap.createScaledBitmap(bmShip, bmShip.getWidth() / 2, bmShip.getHeight() / 2, false);
        //create boundaries for ship
        shipBounds = new Rect(0, 0, bmShip.getWidth(), bmShip.getHeight());

        mMatrix = new Matrix();

    }

    private void initializeStars(int maxX, int maxY)
    {
        //create array of stars coordinates
        starField = new ArrayList<Point>();
        //fill array with random points
        for (int i=0; i<NUM_OF_STARS; i++)
        {
            Random r = new Random();
            int x = r.nextInt(maxX-5+1)+5;
            int y = r.nextInt(maxY-5+1)+5;
            starField.add(new Point (x,y));
        }
    }

    @Override
    synchronized public void onDraw(Canvas canvas)
    {
         //create a black canvas
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);

        g.setColor(Color.GRAY);

        //initialize the starfield if needed
        if (starField==null)
        {
            initializeStars(canvas.getWidth(), canvas.getHeight()*4);
        }

        //draw the stars
        p.setColor(Color.CYAN);
        //p.setAlpha(starAlpha+=starFade);
        p.setAlpha(starAlpha);

        if(ship.y <= getHeight()/2)
           canvas.translate(0, 0);
        else if(ship.y - getHeight()/2 >= getHeight()*3.125)
           canvas.translate(0,(float) (-getHeight()*3.125));
        else
           canvas.translate(0, getHeight()/2 - ship.y);

        p.setStrokeWidth(8);
        for (int i=0; i<NUM_OF_STARS; i++)
        {
            canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
        }

        if (ship.y >= 0)
        {
            mMatrix.reset();
            mMatrix.postTranslate((float) (ship.x), (float) (ship.y));
            canvas.drawBitmap(bmShip, mMatrix, null);
        }

        canvas.drawRect(0, -getHeight()/8, getWidth(), 0, g);
        g.setColor(Color.GREEN);
        canvas.drawRect(0, getHeight()*4, getWidth(), (float) (getHeight()*4.25), g);


        //canvas.restore();
    }

    synchronized public void setShip(int x, int y)
    {
        ship = new Point(x, y);
    }

    synchronized public int getShipX()
    {
        return ship.x;
    }

    synchronized public int getShipY()
    {
        return ship.y;
    }

    synchronized public void resetStarField()
    {
        starField = null;
    }

    synchronized public int getShipWidth()
    {
        return shipBounds.width();
    }

    synchronized public int getShipHeight()
    {
        return shipBounds.height();
    }

 /*   synchronized public void gameOver()
    {
        Paint p = null;
        Canvas canvas = null;
        p.setColor(Color.WHITE);
        canvas.drawText("GAME OVER!", getWidth()/2, getHeight()/2, p);
    }*/
}