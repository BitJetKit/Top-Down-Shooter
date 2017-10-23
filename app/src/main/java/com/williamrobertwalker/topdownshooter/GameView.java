package com.williamrobertwalker.topdownshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.williamrobertwalker.topdownshooter.Weapons.Pistol;
import com.williamrobertwalker.topdownshooter.Weapons.Weapon;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {


    public static   int level = 1;

    public static   UpdateThread updateThread;
    public static   DrawThread drawThread;
    private static  LoopingBackground background;
    public static   PointF viewOffset;

    public static   Player player;
    public static   NextLevelBackgroundTile nextLevelBackgroundTile;

    public static   List<Soldier> enemyList;
    public static   List<BackgroundTile> backgroundTileList;
    public static   List<Bullet> bulletList;
    public static   List<HomingMissile> homingMissileList;
    public static   List<Weapon> weaponList;
    public static   List<Wall> wallList;
    public static   List<Explosion> explosionList;

    public static   Map<String, Bitmap> imageMap;
    public static   Map<String, Bitmap> backgroundImageMap;
    public static   Map<String, Bitmap> wallImageMap;

    public static final Object syncLock = new Object();
    private boolean surfaceWasDestroyed = false;


    public GameView(Context context) {

        super(context);


        //Add callback to the surfaceHolder to intercept events
        getHolder().addCallback(this);

        //Make gameView focusable so it can handle events
        this.setFocusable(true);
    }

    public GameView(Context context, android.util.AttributeSet attributeSet) {

        super(context, attributeSet);
        //Add callback to the surfaceHolder to intercept events
        getHolder().addCallback(this);
        //Make gameView focusable so it can handle events
        this.setFocusable(true);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) { //Only whether the screen has been touched or not and where is sensed, because all you do with
        //the main screen is touch to fire and aim at where it was touched.

        player.pointToward(new PointF(event.getX() + viewOffset.x, event.getY() + viewOffset.y));

        if(event.getAction() == MotionEvent.ACTION_DOWN) //FIRE
        {
            player.isFiring = true; //Makes the player fire a bullet.

            return true;
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) // Finger Leaves screen
        {
            player.walkDirection = 0;
            player.isFiring = false;

            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update() {
        if(!surfaceWasDestroyed) {
            viewOffset.x = player.location.x - this.getWidth() / 2;
            viewOffset.y = player.location.y - this.getHeight() / 2;

            background.update();

            if(nextLevelBackgroundTile != null) {
                nextLevelBackgroundTile.update();
            }

            player.update();

            for(int i = 0; i < bulletList.size() ; i++) {
                bulletList.get(i).update();
            }

            for(int i = 0; i < homingMissileList.size() ; i++) {
                homingMissileList.get(i).update();
            }

            for(int i = 0; i < enemyList.size(); i++) {
                enemyList.get(i).update();
            }

            for(int i = 0; i < explosionList.size() ; i++) {
                explosionList.get(i).update();
            }

            if(bulletList.size() > 0 || homingMissileList.size() > 0) {
                for(int i = 0; i < wallList.size() ; i++) {
                    wallList.get(i).update();
                }
            }
        }
    }
    public void draw(Canvas canvas) { //Draw Method with "THE CANVAS" (Canvas used to draw from mainThread)

        super.draw(canvas);

        background.draw(canvas);// Background is drawn first.

        if(nextLevelBackgroundTile != null) {
            nextLevelBackgroundTile.draw(canvas);
        }

        //Draw all items in lists
        for(int i = 0; i < backgroundTileList.size() ; i++) {
            backgroundTileList.get(i).draw(canvas);
        }

        for(int i = 0; i < wallList.size() ; i++) {
            wallList.get(i).draw(canvas);
        }

        for(int i = 0; i < bulletList.size(); i++) {
            bulletList.get(i).draw(canvas);
        }

        for(int i = 0; i < homingMissileList.size(); i++) {
            homingMissileList.get(i).draw(canvas);
        }

        for(int i = 0; i < weaponList.size(); i++) {
            weaponList.get(i).draw(canvas);
        }

        player.draw(canvas);

        for(int i = 0; i < enemyList.size(); i++) {
            enemyList.get(i).draw(canvas);
        }

        for(int i = 0; i < explosionList.size(); i++) {
            explosionList.get(i).draw(canvas);
        }
    }


    public static Bitmap getImageFromID(int imageID, Map<String, Bitmap> map) {
        Bitmap image = null;
        try {
            image = map.get("" + imageID);
        } catch(Exception e) {
            Log.i("ERROR", "No image in imageMap with that ID.");
            e.printStackTrace();
        }

        return image;
    }

    private PointF screenDimensions = new PointF();
    public PointF getViewDimensions()
    {
        screenDimensions.set(getWidth(), getHeight());
        return screenDimensions;
    }

    private void decodeImages() {
        //Game Object images
        imageMap.put("rawBulletImage", BitmapFactory.decodeResource(getResources(), R.drawable.bullet_multicolor_animation_4));
        imageMap.put("rawHomingMissileImage", BitmapFactory.decodeResource(getResources(), R.drawable.missile_animation_13));

        imageMap.put("pistolImage", BitmapFactory.decodeResource(getResources(), R.drawable.pistol));
        imageMap.put("homingMissileLauncherImage", BitmapFactory.decodeResource(getResources(), R.drawable.superballlauncher));
        imageMap.put("rapidGun", BitmapFactory.decodeResource(getResources(), R.drawable.rapidgun_compact));

        imageMap.put("sniperImage", BitmapFactory.decodeResource(getResources(), R.drawable.sniperrifle3));

        imageMap.put("rawPlayerImage", BitmapFactory.decodeResource(getResources(), R.drawable.bubble_solder_grey_animation_128x128_6));
        imageMap.put("rawEnemySoldierImage", BitmapFactory.decodeResource(getResources(), R.drawable.enemy1_animation_4));
        imageMap.put("rawTargetSolderImage", BitmapFactory.decodeResource(getResources(), R.drawable.enemy3_animation_4));
        imageMap.put("rawExplosionImage", BitmapFactory.decodeResource(getResources(), R.drawable.explosion_animation_6));

        //Background Images
        backgroundImageMap.put("backgroundImage", BitmapFactory.decodeResource(getResources(), R.drawable.background_sand_light_400x400));
        backgroundImageMap.put("1", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        backgroundImageMap.put("2", BitmapFactory.decodeResource(getResources(), R.drawable.background_red_150x150));
        backgroundImageMap.put("3", BitmapFactory.decodeResource(getResources(), R.drawable.background_white_50x50));
        backgroundImageMap.put("4", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        backgroundImageMap.put("5", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        backgroundImageMap.put("6", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        backgroundImageMap.put("7", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        backgroundImageMap.put("8", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        backgroundImageMap.put("9", BitmapFactory.decodeResource(getResources(), R.drawable.background_white_150x150));

        //Wall images
        wallImageMap.put("wallImage", BitmapFactory.decodeResource(getResources(), R.drawable.wall_sandstone_220x500));
        wallImageMap.put("1", BitmapFactory.decodeResource(getResources(), R.drawable.background_white_150x150));
        wallImageMap.put("2", BitmapFactory.decodeResource(getResources(), R.drawable.background_red_150x150));
        wallImageMap.put("3", BitmapFactory.decodeResource(getResources(), R.drawable.background_white_50x50));
        wallImageMap.put("4", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        wallImageMap.put("5", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        wallImageMap.put("6", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        wallImageMap.put("7", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        wallImageMap.put("8", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
        wallImageMap.put("9", BitmapFactory.decodeResource(getResources(), R.drawable.background_grey_150x150));
    }

    public void generateLevel(int level) {
        synchronized(syncLock) {
            //nullify everything
            nextLevelBackgroundTile = null;

//            enemyList = null;
            backgroundTileList = null;
//            bulletList = null;
//            weaponList = null;
//            wallList = null;
//            explosionList = null;

            //Create Lists
            bulletList = new ArrayList<>();
            homingMissileList = new ArrayList<>();
            weaponList = new ArrayList<>();
            enemyList = new ArrayList<>();
            wallList = new ArrayList<>();
            explosionList = new ArrayList<>();

            viewOffset = new PointF();

            player = new Player(imageMap.get("rawPlayerImage"), new PointF(getWidth() / 2, getHeight() / 2), (short) 6);
            Log.i("Player", "Location: X:" + player.location.x + " Y:" + player.location.y);


            createBackground(level);
            createWalls(level);
            createEnemies(level);
            createWeapons(level); //TODO: Add a load file for this and a load method in Load.java
        }
    }

    private void createWalls(int level) {

        try {
            Load.walls(level, this.getContext());
        } catch(IOException e) {
            e.printStackTrace();
            Log.i("ERROR", "Error loading file. Wrong Filename?");
        }
    }

    private void createBackground(int level) {

        backgroundTileList = new ArrayList<>();
        background = new LoopingBackground(backgroundImageMap.get("backgroundImage"), 7);//TODO: Make this number dependant on screen size
        try {
            Load.background(level, this.getContext());
        } catch(IOException e) {
            e.printStackTrace();
            Log.i("ERROR", "Error loading file. Wrong Filename?");
        }
    }

    private void createWeapons() {

        Pistol pistol1 = new Pistol(new PointF(player.location.x + 300, player.location.y));
        pistol1.onGround = true;
        weaponList.add(pistol1);
    }

    private void createWeapons(int level) {

        try {
            Load.weapons(level, this.getContext());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void createEnemies(int level) {

        try {
            Load.enemies(level, this.getContext());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized(syncLock) {
            surfaceWasDestroyed = false;
            imageMap = new ArrayMap<>();
            backgroundImageMap = new ArrayMap<>();
            wallImageMap = new ArrayMap<>();

            decodeImages();

            generateLevel(level);


            if(!MainActivity.alreadyStarted) {
                updateThread = new UpdateThread(this);
                updateThread.setRunning(true); //start game loop
                updateThread.start();

                drawThread = new DrawThread(getHolder(), this);
                drawThread.setRunning(true); //start game loop
                drawThread.start();
                MainActivity.alreadyStarted = true;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized(syncLock) {
            imageMap = null;
            backgroundImageMap = null;
            wallImageMap = null;

            background = null;
            viewOffset = null;
            player = null;
            enemyList = null;
            backgroundTileList = null;
            bulletList = null;
            wallList = null;
            surfaceWasDestroyed = true;
        }
    }
}