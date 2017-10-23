package com.williamrobertwalker.topdownshooter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;

import com.williamrobertwalker.topdownshooter.Weapons.HomingMissileLauncher;
import com.williamrobertwalker.topdownshooter.Weapons.Pistol;
import com.williamrobertwalker.topdownshooter.Weapons.Weapon;

import java.util.ArrayList;
import java.util.List;


public class Player extends Soldier {

    public List<Weapon> weaponInventory = new ArrayList<>();
    Class weaponClass;
    boolean alreadyHasThisWeapon;
    Weapon weapon;
    Weapon weapon2;
    Paint paint = new Paint();
    List<Weapon> decoupler; //A list that acts as a buffer between the real list and stops
    //ConcurrentModificationExceptions.
    //Perhaps making the List a stack will allow you to take out and put on items at will.

    public Player(Bitmap image, PointF location, short numFrames) {
        super(image, location, numFrames);
        maxHealth = 100;
        health = maxHealth;
        turnSpeed = 1f;
        moveSpeed = 5f;
        turnDirection = 0;
        facingAngle = 0;
        paint.setFilterBitmap(true);

        currentWeapon = new Pistol(new PointF(this.location.x, this.location.y)); //What is so special about this.location? OH! IT PASSES THE ACTUAL MEMORY SPACE VARIABLE THING FROM PLAYER. //TODO: LOOK AT THIS.
        weaponInventory.add(new HomingMissileLauncher(new PointF(this.location.x, this.location.y)));
//        Log.i("TAG", "LOCATION: " + this.location); //This should be called many times, but it isn't.

        checkAmmo();
        checkHealth();
        checkWeapon();
    }



    @Override
    public void update() {
        if (!killed) {
            if(hitFlickerCount > 0) {
                checkHealth();
                hitFlickerCount--;
                hitFlicker = !hitFlicker; //possible speed up by reversing these.
            } else {
                hitFlicker = false;
            }

            if(health <= 0) {
                die();
            }

            facingAngle = facingAngle + turnSpeed * turnDirection;

            if(currentWeapon != null) {
                currentWeapon.update(facingAngle, this.location);
            }
            if(velocity.x != 0 || velocity.y != 0) {
                animation.update();
            }
            move();

            for(int i = 0; i < GameView.wallList.size(); i++) //TODO: FIXME
            {
                if(this.isTouchingWall(GameView.wallList.get(i))) {
                    pushFrom(GameView.wallList.get(i).pointNearestTo(this.location));
                }
            }


            if(isFiring && currentWeapon.timeSinceLastShot > currentWeapon.fireDelay) {
                currentWeapon.fireWithInaccuracy(this);
                checkAmmo();
            }

            if(!GameView.weaponList.isEmpty()) { //Checks for and picks up weapons and puts them in inventory or picks up ammo and destroys the weapon.

                for(int i = 0; i < GameView.weaponList.size(); i++)
                {
                    weapon = GameView.weaponList.get(i);
                    if(weapon.isTouching(GameView.player) && weapon.onGround) {

                        weaponClass = weapon.getClass();
                        alreadyHasThisWeapon = false;
                        for(int j = 0; j < GameView.weaponList.size(); j++)
                        {
                            weapon2 = GameView.weaponList.get(j);
                            if(weapon2.getClass() == weaponClass ) {
                                if(!weapon2.hasInfiniteAmmo) {
                                    GameView.weaponList.get(j).ammo += weapon.ammo;
                                }
                                //Stops ConcurrentModificationException by cloning and setting.
                                decoupler = new ArrayList<>(GameView.weaponList); //TODO: Stop creating new things!
                                decoupler.remove(weapon);
                                GameView.weaponList = decoupler;

                                alreadyHasThisWeapon = true;
                                break;
                            }
                        }
//                        for(Weapon weapon2 : weaponInventory) { //one of the inventory weapons is the same
//
//                        }
                        if(currentWeapon.getClass() == weaponClass) { //Current Weapon is the same
                            if(!currentWeapon.hasInfiniteAmmo) {
                                currentWeapon.ammo += weapon.ammo;
                                checkAmmo();
                            }
                            //Stops ConcurrentModificationException by cloning and setting.
                            decoupler = new ArrayList<>(GameView.weaponList); //TODO: Stop creating new things!
                            decoupler.remove(weapon);
                            GameView.weaponList = decoupler;

                            alreadyHasThisWeapon = true;
                        }
                        if(!alreadyHasThisWeapon && weaponInventory.size() <= 1) { //If none of them are the same
                            GameView.weaponList.get(i).onGround = false;
                            weaponInventory.add(weapon);

                            //Stops ConcurrentModificationException by cloning and setting.
                            decoupler = new ArrayList<>(GameView.weaponList); //TODO: Stop creating new things!
                            decoupler.remove(weapon);
                            GameView.weaponList = decoupler;
                        }
//                        Log.i("AlreadyWeapon", "InventoryWeaponList size: " + weaponInventory.size());
//                        Log.i("AlreadyWeapon", "alreadyHasThisWeapon: " + alreadyHasThisWeapon);
                    }
                }
//                for(Weapon weapon : GameView.weaponList) {
//
//                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) //Override to make sure the player doesn't move relative to the screen.
    {
        if (!hitFlicker && !killed) {
            currentWeapon.draw(canvas);

            matrix.postTranslate(-radius + 20, -radius);//move matrix to center of image
            matrix.postRotate(facingAngle); //rotate
            matrix.postTranslate(canvas.getWidth() / 2, canvas.getHeight() / 2); //Makes sure the player doesn't move relative to the screen.
            canvas.drawBitmap(animation.getImage(), matrix, paint);
            matrix.reset(); //clear matrix
            //draw current weapon
        }
    }

    public void previousWeapon()
    {
        if(this.weaponInventory.size() > 0)
        {
            this.weaponInventory.add(GameView.player.currentWeapon);
            GameView.player.currentWeapon = this.weaponInventory.get(this.weaponInventory.size() - 2); //The end of the list
            this.weaponInventory.remove(this.weaponInventory.size() - 2); //The item in the list behind the weapon that you just put in the list.
            GameView.player.checkAmmo();
            GameView.player.checkWeapon();
        }
    }

    public void nextWeapon()
    {
        if(weaponInventory.size() > 0)
        {
            if(currentWeapon != null) {
                this.weaponInventory.add(GameView.player.currentWeapon);
            }

            GameView.player.currentWeapon = this.weaponInventory.get(0);
            this.weaponInventory.remove(0);

            GameView.player.checkAmmo();
            GameView.player.checkWeapon();
        }
    }

    public void checkAmmo() {

        if(!currentWeapon.hasInfiniteAmmo) {
            MainActivity.ammoTextView.post(new Runnable() {
                public void run() {

                    MainActivity.ammoTextView.setText(String.format("Ammo: %s", currentWeapon.ammo));
                }
            });
        } else {
            MainActivity.ammoTextView.post(new Runnable() {
                public void run() {

                    MainActivity.ammoTextView.setText("Ammo: \u221e");
                }
            });
        }
    }
    public void checkHealth() {

        MainActivity.healthBar.post(new Runnable() {
            public void run() {

//                MainActivity.healthTextView.setText(String.format("Health: %s", GameView.player.health));
                //Use this crazy set of numbers to convert the ratio to the player's ratio of speed from -75 - 75 to -6 - 6.
                float oldMin = 0;
                float oldMax = GameView.player.maxHealth;
                float newMin = 0;
                float newMax = MainActivity.healthBar.getMax();

                float oldValue = GameView.player.health;
                oldValue = (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;

                MainActivity.healthBar.setProgress(Math.round(oldValue));
            }
        });
    }
    public void checkWeapon() {

        MainActivity.currentWeaponImageView.post(new Runnable() {
            public void run() {

                BitmapDrawable draw = new BitmapDrawable(currentWeapon.image);
                draw.setFilterBitmap(false);

                MainActivity.currentWeaponImageView.setImageDrawable(draw);
            }
        });
    }
}