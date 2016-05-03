package com.example.dhananjay.breakoutgame;
/*
This activity consists of the game loop.
1. The game runs on a thread wherein the canvas and surfaceHolder are used to draw and update the objects on the screen.
2. We also mke sure to cap the Frame Rate so as to make the game run smoothly.
3. The ball, pad and bricks are drawn every time a frame is created via the update() method.
4. Also, this activity is where the game logic resides in terms of collision detection, score maintenance, ball and pad movement and
hit counting on various colored bricks.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Dhananjay on 11/24/2015.
 * UTD id : 2021250625
 * net id : dxs145530
 */
public class BreakOutGame extends Activity implements SensorEventListener
{
    // breakView will be the view of the game and will implement the game logic and respond to touch events
    BreakView breakView;
    //We need the accelerometer to change the x-velocity of the ball//
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    //These values will be used to measure and optimize the change in orientation detected//
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    //We also need to track selected speed//
    int speed=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //We need the screen to be without margins,full screen and in landscape//
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //We register the accelerometer here//
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //We receive the speed as set in the home screen using the slider//
        Bundle extras=getIntent().getExtras();
        String speedy=extras.getString("KEY");
        speed=Integer.parseInt(speedy);

        // Initialize breakView and set it as the view
        breakView = new BreakView(this);
        setContentView(breakView);
    }

// implementation of BreakView as an inner class.
// We implement runnable so we can override the run method. Basically we will be creating a game loop.
    class BreakView extends SurfaceView implements Runnable
    {
        //we instantiate ball,pad ,surfaceHolder and the bricks here.//
        public Pad pad;
        public Ball ball;
        Thread gThread = null;
        SurfaceHolder holder;
        boolean play;
        boolean pause = true;
        Canvas canvas;
        Paint paint;
        long FPS;
        long totalTime;
        int screenX;
        int screenY;
        BrickCreator bCreator[]=new BrickCreator[200];
        int numBricks=0;
        // For sound FX
        SoundPool soundPool;

        //To increase on the user experience we use different sounds for brick hits, wall hits and pad hits//
        int beep1ID=-1;
        int beep2ID=-1;
        int beep3ID=-1;
        int loseLifeID=-1;
        int explodeID=-1;
        // The score
        int score = 0;
        //The time recorder
        long timeStarted=0;
        long timeTaken=0;
        //The constructor //
        public BreakView(Context context)
        {
            super(context);
            timeStarted=System.currentTimeMillis();
            holder = getHolder();
            paint = new Paint();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y;
            pad=new Pad(screenX,screenY);
            ball=new Ball(screenX,screenY,speed);

            //Though deprecated we still use soundPool because of various capabilities not present with the media player
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
            try{
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;
                // Load our sounds in memory ready for use
                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);
                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);
                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);
                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);
                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);
            }catch(Exception e){
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }
            createBricksAndRestart();
        }

        //to change direction of ball on tilt
        //since Ball is an independent object, we have to send this attribute to the Ball class to associate the selected speed with it//
        public void setXVel(int x)
        {
            ball.setXVel(x);
        }

        //Now we create the bricks //
        public void createBricksAndRestart() {
            // Put the ball back to the start
            ball.reset(screenX, screenY);

            //create the bricks//
            int flag=0;
            /*
            Each row, dependent on the brick width will have, 9,8 and 7 bricks.
            So we divide, each row into 9, 8 and 7 columns (t) respectively.
             */
            int t=9;
            int x=0;
            for(int row=1;row<=3;row++)
            {
                //The following rule will make sure that each brick in a row gets a different color and no two consecutive bricks are of the same color.//
                int c=0+(int)(Math.random()*((4-0)+1));
                int adder=0+(int)(Math.random()*((4-0)+1));
                for(int column=0;column<t;column++)
                {
                    while(c==adder)
                    {
                        adder=0+(int)(Math.random()*((4-0)+1));
                    }
                    if(c!=adder)
                    c=adder;
                    bCreator[x]=new BrickCreator(row,column,screenX,c);
                    x++;
                }
                t--;
            }
            //We keep tracks of the bricks created so as to be able to update their color as well as the broken bricks//
            numBricks=x;
            score = 0;
        }
    @Override
    //This is where our game loop starts and we create all assets on the screen//
    public void run() {
        while (play) {
            long startTime = System.currentTimeMillis();
            if (!pause) {
                update();
            }
            draw();
            totalTime = System.currentTimeMillis()-startTime;
            if (totalTime >= 1) {
                FPS = 1000/totalTime;
            }
        }
    }

        //Update position of ball, bricks and pad frame by frame//
        //Collision detection also implemented here//
    public void update()
    {
        pad.update(FPS);
        ball.update(FPS);
        //ball collides with a brick
        for(int i = 0; i < numBricks; i++){

            if (bCreator[i].getVisibility()){
                if(ball.getRect().intersect(bCreator[i].getRect()))
                {
                    if(bCreator[i].getHits()>0)
                    {
                        ball.reverseYVel();
                        //We set the number of hits required to break the brick to one less each time the ball hits it//
                        //The color of the brick also changes accordingly and the y-velocity of the ball is reversed ie. it starts moving
                        //towards the pad or towards another brick on top, depending on the direction of the hit.
                        bCreator[i].hits--;
                        bCreator[i].setColor(bCreator[i].getHits()-1);
                        if(bCreator[i].getHits()<=0)
                        {
                            bCreator[i].setInvisible();
                            score = score + 10;
                            soundPool.play(explodeID, 1, 1, 0, 0, 1);
                        }

                    }
                }
            }
        }
        //If colliding with the pad
        if(ball.getRect().bottom>pad.getRect().top-screenX/12&&ball.getRect().left>=pad.getRect().left-screenX/12&&ball.getRect().right<=pad.getRect().right+screenX/12) {
            ball.setRandomXVelocity();
            ball.reverseYVel();
            ball.clearY(pad.getRect().top + screenX / 12);
            soundPool.play(beep1ID, 1, 1, 0, 0, 1);
        }
        //If colliding with the right wall
        //The ball bounces towards the left
        if(ball.getRect().right>screenX)
        {
            ball.clearX(screenX-screenX/12);
            ball.reverseXVel();
            soundPool.play(beep3ID, 1, 1, 0, 0, 1);
        }
        //If colliding with the left wall
        //Ball bounces towards the right
        if(ball.getRect().left<0)
        {
            ball.clearX(0);
            ball.reverseXVel();
            soundPool.play(beep3ID, 1, 1, 0, 0, 1);
        }
        //If colliding with bottom game will be lost
        if(ball.getRect().bottom>screenY+screenX/12)
        {
            pause = true;
            //We show the game lost screen now//
            Intent intent = new Intent(BreakOutGame.this, LossScreen.class);
            startActivity(intent);
        }

        //If ball hits the top, the player wins
        //We show the score screen
        if(ball.getRect().top < 0){
            pause = true;
            Intent intent = new Intent(BreakOutGame.this, ScoreClass.class);
            Bundle bundle=new Bundle();
            bundle.putString("KEY",""+timeTaken);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        //Or if all the bricks are down
        //we show the score screen
        if(score == numBricks * 10){
            pause = true;
            Intent intent = new Intent(BreakOutGame.this, ScoreClass.class);
            Bundle bundle=new Bundle();
            bundle.putString("KEY",""+timeTaken);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    public void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            //draw background
            canvas.drawColor(Color.GRAY);
            //set canvas to paint the ball and the pad
            paint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawRect(pad.getRect(), paint);
            canvas.drawCircle(ball.getRect().centerX(), ball.getRect().centerY(), screenX / 24, paint);

            //Draw the bricks if they still have hits left//
            for(int i=0;i<numBricks;i++)
            {
                paint.setColor(bCreator[i].getColor());
                if (bCreator[i].getVisibility()) {
                    canvas.drawRect(bCreator[i].getRect(), paint);
                }
            }
            paint.setColor(Color.CYAN);
            paint.setTextSize(40);
            //we draw the number of broken bricks as well as time taken on the top right corner of the screen//
            timeTaken=(System.currentTimeMillis()-(timeStarted))/1000;
            canvas.drawText("Score: " + score + "   Time Taken: " +timeTaken, 10,50, paint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

        //Initially the ball is stable. It starts moving only when the player toches the screen//
    public void pause() {
        play = false;
        try {
            gThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        play = true;
        gThread = new Thread(this);
        gThread.start();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        //Touching on the left half moves the pad to the left and vice versa.
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                pause=false;
                if(motionEvent.getX()>screenX/2){
                    pad.setMoveState(pad.RIGHT);
                }
                else{
                    pad.setMoveState(pad.LEFT);
                }
                break;
            case MotionEvent.ACTION_UP:
                pad.setMoveState(pad.STOPPED);
                break;
        }
        return true;
    }
}
    //Ball direction change on Tilting
    public void setXVal(int x)
    {
        breakView.setXVel(x);
    }
    @Override
    protected void onResume() {
        super.onResume();
        breakView.resume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        breakView.pause();
        senSensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long curTime = System.currentTimeMillis();
            if(y>last_y)
            {
                setXVal(20);
            }
            else
            if(y<last_y)
            {
                setXVal(-20);
            }
            last_y=y;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
// End of main Game activity//