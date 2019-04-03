package com.example.andrew.game1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView emoji;
    private ImageView bomb;
    private ImageView apple;
    private ImageView banana;

    //size
    private int frameHeigth;
    private int emojiSize;
    private int screenWidth;
    private int screenHeigth;


    //position
    private int emojiY;



    private int bombX;
    private int bombY;

    private int appleX;
    private int appleY;

    private int bananaX;
    private int bananaY;

    //speed
    private int emojiSpeed;
    private int bombSpeed;
    private int appleSpeed;
    private int bananaSpeed;


    //score
    private int score = 0;


    //Initialize Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    // Status Check
    private boolean action_flg = false;
    private boolean start_flg = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);

        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);

        emoji = (ImageView) findViewById(R.id.emoji);
        bomb = (ImageView) findViewById(R.id.bomb);
        apple = (ImageView) findViewById(R.id.apple);
        banana = (ImageView) findViewById(R.id.banana);


        // Get screen size

        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeigth = size.y;

        //Now
        //Nexus4 width 768 heigth 1184


        emojiSpeed = Math.round(screenHeigth / 60F);   // 1184/60 = 19,7333 => 20

        bombSpeed = Math.round(screenWidth / 45F);  // 768/45 = 17.06 =>17
        appleSpeed = Math.round(screenWidth/50F);
        bananaSpeed = Math.round(screenWidth/40F);

        //Log.v("SPEED_EMOJI",emojiSpeed+"");

        //Log.v("SPEED_bomb",bombSpeed+"");

        bomb.setX(-80.0f);
        bomb.setY(-80.0f);
        apple.setX(-80.0f);
        apple.setY(-80.0f);
        banana.setX(-80.0f);
        banana.setY(-80.0f);

        scoreLabel.setText("Score : " + score);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void changePos() {

        hitCheck();


        bombX -= bombSpeed;
        if (bombX < 0) {

            bombX = screenWidth + 10;
            bombY = (int) Math.floor(Math.random() * (frameHeigth - bomb.getHeight()));

        }
        bomb.setX(bombX);
        bomb.setY(bombY);




        appleX -= appleSpeed;
        if (appleX < 0) {

            appleX = screenWidth + 3000;
            appleY = (int) Math.floor(Math.random() * (frameHeigth - apple.getHeight()));

        }
        apple.setX(appleX);
        apple.setY(appleY);

        bananaX -= bananaSpeed;
        if (bananaX < 0) {

            bananaX = screenWidth + 6000;
            bananaY = (int) Math.floor(Math.random() * (frameHeigth - banana.getHeight()));

        }
        banana.setX(bananaX);
        banana.setY(bananaY);





        // move Emoji
        if (action_flg == true) {
            //Touching
            emojiY -= emojiSpeed;
        } else {
            emojiY += emojiSpeed;
        }
        if (emojiY < 0) emojiY = 0;


        if (emojiY > frameHeigth - emojiSize) emojiY = frameHeigth - emojiSize;


        emoji.setY(emojiY);
        scoreLabel.setText("Score : " + score);
    }

    public void hitCheck() {

        if (score>100) {
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);

        }

        // if the center of the ball is in the emoji it counts as a hit


        int appleCenterX = appleX + apple.getWidth() / 2;
        int appleCenterY = appleY + apple.getHeight() / 2;

        if (0 <= appleCenterX && appleCenterX <= emojiSize && emojiY <= appleCenterY && appleCenterY <= emojiY + emojiSize) {
            score += 10;
            appleX = -10;
            sound.playHitSound();
        }

        int bananaCenterX = bananaX + banana.getWidth() / 2;
        int bananaCenterY = bananaY + banana.getHeight() / 2;

        if (0 <= bananaCenterX && bananaCenterX <= emojiSize && emojiY <= bananaCenterY && bananaCenterY <= emojiY + emojiSize) {
            score += 10;
            bananaX = -10;
            sound.playHitSound();
        }


        int bombCenterX = bombX + bomb.getWidth() / 2;
        int bombCenterY = bombY + bomb.getHeight() / 2;

        if (0 <= bombCenterX && bombCenterX <= emojiSize && emojiY <= bombCenterY && bombCenterY <= emojiY + emojiSize) {

            bombX = -10;
            sound.playOverSound();

            //stop timer
            timer.cancel();
            timer = null;

            //show result

            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);


        }


    }

    public boolean onTouchEvent(MotionEvent me) {

        if (start_flg == false) {

            start_flg = true;

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeigth = frame.getHeight();

            emojiY = (int) emoji.getY();
            emojiSize = emoji.getHeight();


            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);

        } else {
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (me.getAction() == MotionEvent.ACTION_UP) {

                action_flg = false;
            }
            emoji.setY(emojiY);

        }


        return true;
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
