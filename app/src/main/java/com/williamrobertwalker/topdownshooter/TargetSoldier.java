package com.williamrobertwalker.topdownshooter;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import com.williamrobertwalker.topdownshooter.Weapons.InaccurateAndSlow;

import java.util.ArrayList;
import java.util.Random;

public class TargetSoldier extends Soldier {

    private int directionChangeCount = 0;
    private int nextDirectionChange = 0;
    Random r = new Random();

    TargetSoldier(Bitmap rawImage, PointF location, short numFrames) {
        super(rawImage, location, numFrames);
        health = 3;
        GameView.enemyList.add(this);
        isFiring = false;
        moveSpeed = 2;
        walkDirection = 0;
        currentWeapon = new InaccurateAndSlow(location);
        GameView.weaponList.add(currentWeapon);

        //Set currentWeapon to a new instance of EnemyPistol and provide a starting location
        //this.currentWeapon = new EnemyPistol(this.location);
    }

    //DONE: make it so that this AI is better and doesn't run over the player and insta-kill him.
    @Override
    public void update() {
        double distanceFromPlayer = Math.sqrt((GameView.player.location.x - this.location.x) * (GameView.player.location.x - this.location.x) +
                (GameView.player.location.y - this.location.y) * (GameView.player.location.y - this.location.y));



        if (distanceFromPlayer < (MainActivity.gameView1.getViewDimensions().x + 100)) {
            if(this.health <= 0) {
                dropWeapon();
                die();
            }


            if(distanceFromPlayer < 100) {
                isFiring = true;
                walkDirection = 0;
            }
            else if(distanceFromPlayer < 300 && distanceFromPlayer > 100) {
                super.update();
                isFiring = true;
                pointToward(GameView.player.location);
            }
            else if(distanceFromPlayer < (MainActivity.gameView1.getViewDimensions().x + 100)) {
                isFiring = false;
                super.update();
                directionChangeCount++;

                if(directionChangeCount >= nextDirectionChange) {
                    facingAngle = r.nextInt(360);
                    walkDirection = (short) r.nextInt(2);

                    directionChangeCount = 0;

                    nextDirectionChange = r.nextInt(60 + 120);
                }
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