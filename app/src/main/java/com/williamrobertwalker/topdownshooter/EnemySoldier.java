package com.williamrobertwalker.topdownshooter;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;
import com.williamrobertwalker.topdownshooter.Weapons.Pistol;
import java.util.ArrayList;
import java.util.Random;


public class EnemySoldier extends Soldier {

    private int directionChangeCount = 0;
    private int nextDirectionChange = 0;
    Random r = new Random();


    public EnemySoldier(Bitmap image, PointF location, short numFrames) {

        super(image, location, numFrames);
        health = 3;
        GameView.enemyList.add(this);
        isFiring = true;
        moveSpeed = 2;
        walkDirection = 1;
        currentWeapon = new Pistol(location);
        GameView.weaponList.add(currentWeapon);

        //Set currentWeapon to a new instance of EnemyPistol and provide a starting location
        //this.currentWeapon = new EnemyPistol(this.location);

        Random r = new Random((int) location.x);
        nextDirectionChange = r.nextInt((100) + 200);

    }

    @Override
    public void update() {
        if (this.health <= 0) {
            dropWeapon();
            die();
        }

        if (Math.sqrt((GameView.player.location.x - this.location.x) * (GameView.player.location.x - this.location.x) +
                (GameView.player.location.y - this.location.y) * (GameView.player.location.y - this.location.y)) < (MainActivity.gameView1.getViewDimensions().x + 100)) {
            super.update();
            directionChangeCount++;

            if (directionChangeCount >= nextDirectionChange) {
                walkDirection = (short) r.nextInt(1);
                facingAngle = r.nextInt(360);

                directionChangeCount = 0;

                nextDirectionChange = r.nextInt(60 + 120);
            }
        }
    }

    @Override
    protected void die() {
        new Explosion(GameView.imageMap.get("rawExplosionImage"), new PointF(this.location.x, this.location.y), (short) 6);
        ArrayList<Soldier> tempSoldiers = new ArrayList<>(GameView.enemyList);

        tempSoldiers.remove(this);
        GameView.enemyList = tempSoldiers;

        Log.i("Enemies", "Number of enemies left: " + GameView.enemyList.size());
    }
}