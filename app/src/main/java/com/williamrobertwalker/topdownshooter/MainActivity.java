package com.williamrobertwalker.topdownshooter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {


    public static       GameView        gameView1;
    public static       TextView        ammoTextView;
//    public static       TextView        healthTextView;
    public static       ImageView       currentWeaponImageView;
    public static       Button          previousWeaponButton;
    public static       Button          nextWeaponButton;
    public static       Button          pauseButton;
    public static       ProgressBar     healthBar;
    public static       boolean         alreadyStarted = false;
    public static       boolean         screenIsOff = false; //Created because having your game running while the phone is off is creepy.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager    powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(powerManager.isScreenOn()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            screenIsOff = false;
            setContentView(R.layout.activity_main);

            gameView1 =                 (GameView)      findViewById(R.id.gameView1);
            ammoTextView =              (TextView)      findViewById(R.id.ammoTextView);
            currentWeaponImageView =    (ImageView)     findViewById(R.id.currentWeaponImageView);
            previousWeaponButton =      (Button)        findViewById(R.id.previousWeaponButton);
            nextWeaponButton =          (Button)        findViewById(R.id.nextWeaponButton);
            pauseButton =               (Button)        findViewById(R.id.pauseButton);
            healthBar =                 (ProgressBar)   findViewById(R.id.healthBar);

            healthBar.getProgressDrawable().setColorFilter(0xFFAD0000, PorterDuff.Mode.SRC_IN);

            previousWeaponButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GameView.player.previousWeapon();
                }
            });
            nextWeaponButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GameView.player.nextWeapon();
                }
            });
            pauseButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Start a popup.
                    pauseGame();
                    startActivity(new Intent(MainActivity.this, SettingsPopup.class));
                }
            });
        }
        else
        {
            //What the !#$@ are you doing? Why are you on while the phone is off? This gets called every time the phone shuts down.
            screenIsOff = true;
        }
    }

    public static void autoPauseGame() {

        if(GameView.updateThread != null && GameView.drawThread != null) {
            GameView.player.setTurnDirection((short) 0);
            GameView.player.setWalkDirection((short) 0);

            try {
                GameView.updateThread.setRunning(false);
                GameView.updateThread.join();
                GameView.updateThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                try {
                    throw e;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            try {
                GameView.drawThread.setRunning(false);
                GameView.drawThread.join();
                GameView.drawThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                try {
                    throw e;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            alreadyStarted = false;
        }
    }
    public static void pauseGame() {

        if(GameView.updateThread != null && GameView.drawThread != null && !screenIsOff) {
            GameView.player.setTurnDirection((short) 0);
            GameView.player.setWalkDirection((short) 0);

            try {
                GameView.updateThread.setRunning(false);
                GameView.updateThread.join();
                GameView.updateThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                try {
                    throw e;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            try {
                GameView.drawThread.setRunning(false);
                GameView.drawThread.join();
                GameView.drawThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                try {
                    throw e;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            alreadyStarted = true;
        }
    }

    public static void resumeGame() {

        if((GameView.updateThread == null) && (GameView.drawThread == null) && alreadyStarted) {

            Log.i("TEST", "Yes, I was called.");
            GameView.updateThread = new UpdateThread(gameView1);
            GameView.updateThread.setRunning(true); //start game loop
            GameView.updateThread.start();

            GameView.drawThread = new DrawThread(gameView1.getHolder(), gameView1);
            GameView.drawThread.setRunning(true); //start game loop
            GameView.drawThread.start();
        }
    }

    @Override
    protected void onPause() {
        autoPauseGame();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i("TEST", "On resume!");
        resumeGame();
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        pauseGame();
        super.onDestroy();
    }
}