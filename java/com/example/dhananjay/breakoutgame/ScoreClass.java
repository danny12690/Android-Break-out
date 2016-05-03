package com.example.dhananjay.breakoutgame;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
/*
The User can view top 10 high scores or record his/her score.
This activity can be reached from the start screen itself or after beating the game.
 */

/**
 * Created by Dhananjay on 11/25/2015.
 * UTD id: 2021250625
 * Net Id : dxs145530
 */
public class ScoreClass extends Activity {
    int score=0;
    Button save;
    Button restart;
    EditText scoreTag;
    EditText nameTag;
    Bundle extras;
    String scorer;

    //To display top 10 scores in a List
    ListView scoreList;
    String scores[];
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;

    //to enable player to record his/her score
    TextView name;
    TextView score1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Window should be full screen, without margins and in landscape
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.score_screen);

        //BackGround
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.breakout2), size.x, size.y, true);

    /* fill the background ImageView with the resized image */
        ImageView iv_background = (ImageView) findViewById(R.id.iv_background1);
        iv_background.setImageBitmap(bmp);

        //Create references to the texFields and buttons//
        save=(Button)findViewById(R.id.save);
        nameTag=(EditText)findViewById(R.id.nameTag);
        scoreTag=(EditText)findViewById(R.id.scoreTag);
        scoreList=(ListView)findViewById(R.id.scoreList);
        restart=(Button)findViewById(R.id.restart);
        name=(TextView)findViewById(R.id.textView3);
        score1=(TextView)findViewById(R.id.textView2);
        scores=new String[10];

        //Create a dynamic list adapter//
        listItems=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItems);
        scoreList.setAdapter(adapter);

        //Receiving the score/extra values sent from the previous activities
        extras=getIntent().getExtras();
        scorer=extras.getString("KEY");
        score=Integer.parseInt(scorer);
        //this is to check if score screen is reached through the home screen or when the game is won//
        if(score==0) //Reached from the main screen
        {
           score1.setVisibility(View.INVISIBLE);
           name.setVisibility(View.INVISIBLE);
           save.setVisibility(View.INVISIBLE);
           nameTag.setVisibility(View.INVISIBLE);
           scoreTag.setVisibility(View.INVISIBLE);
           scoreList.setVisibility(View.VISIBLE);
           restart.setVisibility(View.VISIBLE);
           readPopulate();
        }
        else {
            score1.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            nameTag.setVisibility(View.VISIBLE);
            restart.setVisibility(View.INVISIBLE);
            scoreTag.setVisibility(View.VISIBLE);
            //The Score should be inversely proportional to the time taken. the lower it is the better score the player made//
            scoreTag.setText("" + (score));
            scoreList.setVisibility(View.INVISIBLE);
        }
        //Making the save button workable
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write();
                Intent intent = new Intent(ScoreClass.this, StartScreen.class);
                startActivity(intent);
            }
            });
        //Making the restart button workable//
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreClass.this, StartScreen.class);
                startActivity(intent);
            }
        });
    }
    public void readPopulate()
    {
        int count=0; //keep track of number of lines. We need only 10 as only top 10 scores are to be displayed//
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "Score.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = reader.readLine()) != null&&count!=10)
            {
                scores[count]=line;
                listItems.add(scores[count]);
                count++;
            }
            reader.close();
            }
        catch(Exception e){}
    }
    public void write()
    {
        ArrayList<String> rows=new ArrayList<String>();
        try
        {
            File file=new File(Environment.getExternalStorageDirectory(),"Score.txt");
            FileWriter writer = new FileWriter(file,true);
            writer.append(nameTag.getText().toString()+","+scoreTag.getText().toString()+"\r\n");
            writer.close();

            //Now we sort the entries as per score//
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Map<String, String> map=new TreeMap<String, String>();
            String line="";
            while((line=reader.readLine())!=null){
                map.put(""+getField(line),line);
            }
            reader.close();
            file=new File(Environment.getExternalStorageDirectory(),"Score.txt");
            writer = new FileWriter(file);
            for(String val : map.values()){
                writer.write(val);
                writer.write('\n');
            }
            writer.close();
        }
        catch(Exception e){}
    }

    private static int getField(String line) {
        return Integer.parseInt(line.split(",")[1]);//extract value you want to sort on
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
//End of ScoreClass//