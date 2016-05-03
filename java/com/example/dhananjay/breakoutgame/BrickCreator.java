package com.example.dhananjay.breakoutgame;
/*
This class creates the three rows of bricks. Each brick has a random color. from the set {black,blue,red,green,white}
1> The first row has bricks ,1/9 of screen width each
2> The second row, brick width = 1/8 of screen width
3> The third row, brick width = 1/7 of screen width
4> No two consecutive bricks in the same row has the same color.
 */
/**
 * Created by Dhananjay on 11/24/2015.
 * UTD id : 2021250625
 * Net Id : dxs145530
 */
import android.graphics.Color;
import android.graphics.RectF;
public class BrickCreator
{
    int c;
    int flag=0;
    int hits;
    int color;
    RectF brick;
    private boolean isVisible;
    public BrickCreator(int row, int column,int screenX,int c)
    {
        isVisible=true;
        if(row==1)
        {
            brick=new RectF(column*(screenX/9),0,column*(screenX/9)+screenX/9,0+screenX/9);
            setColor(c);
        }
        else
        if(row==2)
        {
            brick=new RectF(column*(screenX/8),screenX/9,column*(screenX/8)+screenX/8,screenX/9+screenX/8);
            setColor(c);
        }
        else
        if(row==3)
        {
            brick=new RectF(column*(screenX/7),(17*screenX)/72,column*(screenX/7)+screenX/7,(17*screenX)/72+screenX/7);
            setColor(c);
        }
    }
    public RectF getRect(){
        return this.brick;
    }
    public void setInvisible(){
        isVisible=false;
    }
    public boolean getVisibility(){
        return isVisible;
    }
    public int getColor()
    {
        return color;
    }
    public void setColor(int c)
    {
        /*
        Color                  Hits Required to break it       Color Id (c)
        WHITE                      1                               0
        BLUE                       2                               1
        GREEN                      3                               2
        RED                        4                               3
        BLACK                      5                               4

         */
        if(c==0)
        {
            color=Color.WHITE;
            hits=1;
        }
        if(c==1)
        {
            color=Color.BLUE;
            hits=2;
        }
        if(c==2)
        {
            color=Color.GREEN;
            hits=3;
        }
        if(c==3)
        {
            color=Color.RED;
            hits=4;
        }
        if(c==4)
        {
            color=Color.BLACK;
            hits=5;
        }
    }
    public int getHits()
    {
        return hits;
    }
}
//End of the brick creating class//
