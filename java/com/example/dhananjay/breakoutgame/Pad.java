package com.example.dhananjay.breakoutgame;
/*
This is the Pad class.
1. The attributes like hiegth,widht of the pad is set via the constructor.
2. The pad is a rectangle of 1/6 of screen width and 20 pixels of height.
 */
import android.graphics.RectF;
/**
 * Created by Dhananjay on 11/24/2015.
 * UTD id : 2021250625
 * Net id : dxs145530
 */
public class Pad {
    private RectF rect;
    private float length;
    private float height;
    private float x;
    private float y;
    private float padSpeed;

    //The pad can move only left and right. And the only other state is the pad to be stationary.//
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public int screenX = 0;
    private int padMove = STOPPED;

    public Pad(int screenX, int screenY) {
        length = screenX / 6;
        this.screenX = screenX;
        height = 20;
        x = screenX / 2;
        y = screenY - 20;
        rect = new RectF(x, y, x + length, y + height);
        // How fast is the paddle in pixels per second
        padSpeed = 350;
    }

    public RectF getRect() {
        return rect;
    }

    public void setMoveState(int moveState) {
        padMove = moveState;
    }

    //The pad cannot move outside the left wall
    public void update(long fps) {
        if (padMove == LEFT) {
            if (rect.left <= 0) {
                x = x;
            } else {
                x = x - padSpeed / fps;
            }
        }
        //The pad should also not move outside the right wall
        if (padMove == RIGHT) {
            if (rect.right >= screenX) {
                x = x;
            } else {
                x = x + padSpeed / fps;
            }
        }
            rect.left = x;
            rect.right = x + length;
    }
}

//End of Pad class//