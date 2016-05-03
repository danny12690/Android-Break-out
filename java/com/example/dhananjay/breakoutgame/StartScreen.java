/*
This is the class for the home screen of the game.
1> The player can select the speed of the ball using a slider.
2> The player can start a new game by clicking on the NEW GAME button.
3> High Scores can be viewed by clicking the HIGh SCORES button.
4> The ball, the pad and the bricks aare implemented as separate classes. Hence, the object oriented programming s realized//
 */
package com.example.dhananjay.breakoutgame;

//author :Dhananjay Singh
// utd id : 2021250625
// net id : dxs145530

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class StartScreen extends Activity
{
    int progress=0; //To track the speed as on the SeekBar
    SeekBar speed;
    MediaPlayer mMediaPlayer; //An opening sound track is played. The audio file is kept in the raw folder as it is in mp3 format//
    Button newGame;
    Button highScores;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //We want the window to be without margins, full screen and in landscape view //
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_start_screen);

        //we want an intro music to be played so we instantiate a media player when the activity starts//
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.sound1);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //The intro music must not loop. Kind of annoying if it does, isn't it? //
        mMediaPlayer.setLooping(false);
        mMediaPlayer.start();

        //Get size of the display to map the background/ using a bitmap factory//
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.breakout2),size.x,size.y,true);

    /* fill the background ImageView with the resized image */
        ImageView iv_background = (ImageView) findViewById(R.id.iv_background);
        iv_background.setImageBitmap(bmp);

        //Let us make the buttons usable//
        newGame =(Button)findViewById(R.id.startGame);
        highScores=(Button)findViewById(R.id.scores);

        //Let us make the seekBar usable
        speed=(SeekBar)findViewById(R.id.seekBar);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue/15;
                System.out.println(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        //To start a new Game //
        //Bundle will be used to send the selected speed to the actual game activity.//
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                Intent intent = new Intent(StartScreen.this, BreakOutGame.class);
                Bundle bundle = new Bundle();
                bundle.putString("KEY", "" + progress);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //To view High Scores //
        //Bundle will be used to send only 0 as we need to differentiate whether th activity is reached via game win or directly from the start screen////
        highScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StartScreen.this, ScoreClass.class);
                Bundle bundle=new Bundle();
                bundle.putString("KEY",""+0);
                intent.putExtras(bundle);
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
//End of starting activity//