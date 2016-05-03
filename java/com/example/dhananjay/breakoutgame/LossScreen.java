package com.example.dhananjay.breakoutgame;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

/*
This activity is triggered when the user loses the game that is when the ball misses the pad.
This just has one button that redirects the player back to the starting screen of the game.
 */
/**
 * Created by Dhananjay on 11/29/2015.
 * UTD id: 2021250625
 * Net id : dxs145530
 */
public class LossScreen extends Activity {
    Button restart;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //We want the window to be landscaped, full screen and without margins//
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.loss_screen);

        //BackGround
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.breakout_loss), size.x, size.y, true);

    /* fill the background ImageView with the resized image */
        ImageView iv_background = (ImageView) findViewById(R.id.iv_background1);
        iv_background.setImageBitmap(bmp);

        //Make button usable
        restart=(Button)findViewById(R.id.restart1);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lead user to the starting screen.
                Intent intent = new Intent(LossScreen.this, StartScreen.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
//End of LossScreen.java