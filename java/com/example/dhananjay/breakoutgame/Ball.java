package com.example.dhananjay.breakoutgame;
/*
This is the ball class.
1. The ball is implemented as a white circle, whose diameter is 1/12 of screen width.
2. The attributes like ball speed and initial position are set using the constructor.
3. The ball movements are emulated using the methods, reverseXVel,reverseYVel,setXVel,setYVel,clearY and clearX.
4. The ball's position at a particular in-game time is managed by the method update() and reset().
 */

/**
 * Created by Dhananjay on 11/24/2015.
 * UTD id : 2021250625
 * Net Id : dxs145530
 */
import android.graphics.Point;
import android.graphics.RectF;
import android.view.Display;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

import java.util.Random;
public class Ball
{
    RectF ball;
    float xVel;
    float yVel;
    float ballWidth;
    float ballHeight;
    public Ball(int screenX, int screenY,int speed)
    {
        xVel=70+5*speed;
        yVel=-140;
        ballWidth=screenX/12;
        ballHeight=screenX/12;
        ball=new RectF();
    }
    public void setXVel(int x)
    {
       xVel=xVel+x;
    }
    public RectF getRect(){
        return ball;
    }
    public void update(long fps)
    {
        ball.left=ball.left+(xVel/fps);
        ball.top=ball.top+(yVel/fps);
        ball.right=ball.left+ballWidth;
        ball.bottom=ball.top-ballHeight;
    }
    //if ball touches any of the side walls//
    public void reverseYVel(){
        yVel=-yVel;
    }

    //if ball touches a brick//
    public void reverseXVel(){
        xVel=-xVel;
    }

    //To adjust the angle of ball reflection onc it touches the pad or the brick//
    public void setRandomXVelocity(){
        Random gen = new Random();
        int answer = gen.nextInt(7)-7;
            xVel=xVel+10*answer;
    }
    //The next two methods are more like error correction in terms of co-ordinates on the screen//

    //To avoid the ball getting stuck between two bricks in the same row.
    public void clearY(float y){
        ball.bottom=y;
        ball.top=y-ballHeight;
    }

    //To avoid the ball getting stuck between te bottom of screen and paddle.
    public void clearX(float x){
        ball.left=x;
        ball.right=x+ballWidth;
    }

    //To set the ball to its original position when a new game starts//
    public void reset(int x, int y){
        ball.left=x/2;
        ball.top=y-20;
        ball.right=x/2+ballWidth;
        ball.bottom=y-20-ballHeight;
    }
}

//End of the ball class//
