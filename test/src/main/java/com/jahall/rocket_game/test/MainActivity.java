package com.jahall.rocket_game.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import com.jahall.rocket_game.test.Background;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Properties;
import java.util.Random;
import android.graphics.Camera;

import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;


public class MainActivity extends Activity implements OnClickListener
{
    private Handler frame = new Handler();
    private Point shipVelocity;
    private int shipMaxX;
    private int shipMaxY;
    private boolean isAccelerating = false;
    private int fuel = 100;
    private String title;
    private Camera mCamera;
    private Matrix shipMatrix;
    private double GRAVITY;
    private double THRUST;
    private int LEVEL;
    private String VIEWER;

    //Divide the frame by 1000 to calculate how many times per second the screen will update.
    private static final int FRAME_RATE = 20; //50 frames per second

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        LEVEL = 0;
        Handler h = new Handler();

        switch (LEVEL)
        {
            case 0:
                //We can't initialize the graphics immediately because the layout manager
                //needs to run first, thus we call back in a sec.
                h.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        initGfx();
                    }
                }, 1000);
                break;
           /* case 1:
                h.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        initLOne();
                    }
                }, 1000);
                break;*/
        }
    }

    synchronized public void initGfx()
    {
        ((Background)findViewById(R.id.the_canvas)).resetStarField();

        //Set our boundaries for the sprites
        shipMaxX = findViewById(R.id.the_canvas).getWidth() - ((Background)findViewById(R.id.the_canvas)).getShipWidth();
        shipMaxY = findViewById(R.id.the_canvas).getHeight()*4 - ((Background)findViewById(R.id.the_canvas)).getShipHeight();

        //draw the ship to the screen
        ((Background)findViewById(R.id.the_canvas)).setShip((shipMaxX / 2), findViewById(R.id.the_canvas).getHeight()/2);
        //initial velocity of ship
        shipVelocity = new Point (0, 1);
        GRAVITY = 1.1;
        THRUST = 3.1;
        title = "1-" + LEVEL;

        mCamera = new Camera();
        shipMatrix = new Matrix();

        //((Button)findViewById(R.id.the_button)).setEnabled(true);
            //It's a good idea to remove any existing callbacks to keep
            //them from inadvertently stacking up.
        frame.removeCallbacks(frameUpdate);
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    @Override
    synchronized public void onClick(View v)
    {
            initGfx();
    }

    private Runnable frameUpdate = new Runnable()
    {
            @Override
            synchronized public void run()
            {
                frame.removeCallbacks(frameUpdate);

                Point ship = new Point (((Background)findViewById(R.id.the_canvas)).getShipX(), ((Background)findViewById(R.id.the_canvas)).getShipY()) ;

                ship.y = ship.y + shipVelocity.y;

                updateVelocity(ship);

                shipThrust();

                drawStuff(ship);

                //frame.postDelayed(frameUpdate, FRAME_RATE);
            }
        };

    @Override
    synchronized public boolean onTouchEvent(MotionEvent ev)
    {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                isAccelerating = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                isAccelerating = false;
                break;
        }
        return true;
    }

    synchronized public void drawStuff(Point ship)
    {
        ((TextView)findViewById(R.id.the_world)).setText(title);
        //display ship current velocity
        ((TextView)findViewById(R.id.the_speed)).setText("Speed: " + Integer.toString(shipVelocity.y));
        //display ship fuel left
        ((TextView)findViewById(R.id.the_fuel)).setText("Fuel: " + Integer.toString(fuel));
        //draw ship to new location
        ((Background)findViewById(R.id.the_canvas)).setShip(ship.x, ship.y);
        ((Background)findViewById(R.id.the_canvas)).invalidate();
    }

    synchronized public void updateVelocity(Point ship)
    {
        if (ship.y >= shipMaxY && shipVelocity.y > 30)
        {
            //((TextView)findViewById(R.id.the_speed)).setText("Speed: CRASH!");
            ship.y = shipMaxY;
            title = "CRASH!";
            //onCreateDialog(1);
            //endGame(title);
        }
        else if (ship.y >= shipMaxY && shipVelocity.y < 30)
        {
            //((TextView)findViewById(R.id.the_speed)).setText("Speed: WIN!");
            ship.y = shipMaxY;
            title = "WIN!";
            onCreateDialog(1);
        }
        else
        {
            shipVelocity.y+=GRAVITY;
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    }

    synchronized public void shipThrust()
    {
        //check user input
        if (isAccelerating && fuel > 0)
        {
            shipVelocity.y-=THRUST;
            fuel--;
        }
    }

    protected Dialog onCreateDialog(int id)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(title);
        builder.setCancelable(false);
        if (title == "WIN!")
        {
            //((Background)findViewById(R.id.the_canvas)).gameOver();
            builder.setPositiveButton("Next Level?", new OkOnClickListener());
        }
        builder.setNegativeButton("Replay", new CancelOnClickListener());
        AlertDialog dialog = builder.create();
        dialog.show();
        return super.onCreateDialog(id);
    }

    private final class OkOnClickListener implements DialogInterface.OnClickListener
    {
        public void onClick(DialogInterface dialog, int which)
        {
            fuel = 100;
            LEVEL++;
            initGfx();
        }
    }

    private final class CancelOnClickListener implements DialogInterface.OnClickListener
    {
        public void onClick(DialogInterface dialog, int which)
        {
            fuel = 100;
            MainActivity.this.initGfx();
        }
    }
}