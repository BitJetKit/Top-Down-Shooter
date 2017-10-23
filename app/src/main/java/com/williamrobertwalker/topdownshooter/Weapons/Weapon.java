package com.williamrobertwalker.topdownshooter.Weapons;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.williamrobertwalker.topdownshooter.Bullet;
import com.williamrobertwalker.topdownshooter.GameView;
import com.williamrobertwalker.topdownshooter.Soldier;
import java.util.Random;

public abstract class Weapon {

    protected       float       weaponDistance;
    public          PointF      location;
                    float       bulletSpeed;
    public          int         fireDelay;
                    float       bulletStartDistance;
    public          float       facingAngle;
    public          int         timeSinceLastShot;
    private         int         radius;
    public          boolean     onGround = false;
    public  final   Bitmap      image;
    private         Matrix      matrix;
    public          int         inaccuracy = 0;
    public          int         ammo = 1;
    public          boolean     hasInfiniteAmmo = false;
    private         Paint       paint = new Paint();
    private final   Random      r = new Random();


    Weapon(Bitmap image, PointF location) {

        this.image = image;
        this.location = new PointF(location.x, location.y); //Weapon is being created repeatedly. This is bad.
        this.radius = image.getWidth() / 2;
        this.timeSinceLastShot = this.fireDelay;
        matrix = new Matrix();
        paint.setFilterBitmap(true);
    }

    public boolean isTouching(Soldier soldier) {

        double distance = Math.sqrt((soldier.location.x - this.location.x) * (soldier.location.x - this.location.x) +
                (soldier.location.y - this.location.y) * (soldier.location.y - this.location.y)); //Pythagorean Theorem.

        return distance < (this.radius + soldier.radius);
    }

    public void draw(Canvas canvas) {

        matrix.postTranslate(-radius, -radius);//move matrix to center of image
        matrix.postRotate(facingAngle); //rotate
        matrix.postTranslate(location.x - GameView.viewOffset.x + (float) Math.cos(facingAngle / 180f * Math.PI) * weaponDistance, location.y - GameView.viewOffset.y + (float) Math.sin(facingAngle / 180f * Math.PI) * weaponDistance);
        // The location.y that was just used is Solder's location memory space. This is because it was passed down through the ether.
//        Log.i("TAG", "Weapon Location: " + location); //I called it! To solve this, use the "new <Object>(...)" often.
//        matrix.postTranslate(canvas.getWidth() / 2, canvas.getHeight()/2);


        canvas.drawBitmap(image, matrix, paint); //draw
        matrix.reset(); //clear matrix
    }

    public void fire(Soldier personFiring) {

        if(ammo > 0) {
            // Reset the shot timer.
            timeSinceLastShot = 0;

            final float xComponent = (float) Math.cos(facingAngle / 180f * Math.PI);
            final float yComponent = (float) Math.sin(facingAngle / 180f * Math.PI);

            Bullet bullet = createBullet(personFiring);

            bullet.location.x = this.location.x + (xComponent * bulletStartDistance);
            bullet.location.y = this.location.y + (yComponent * bulletStartDistance);

            bullet.velocity.x = xComponent * bulletSpeed;
            bullet.velocity.y = yComponent * bulletSpeed;

            if(personFiring == GameView.player && !hasInfiniteAmmo)
            {
                this.ammo --;
                Log.i("Ammo", "You have " + ammo + " bullets left");
            }
        }
    }

    Bullet bullet;
    public void fireWithInaccuracy(Soldier personFiring) {

        if(ammo > 0) {
            // Reset the shot timer.
            timeSinceLastShot = 0;


            final float xComponent = (float) Math.cos((((-inaccuracy / 2) + r.nextInt(inaccuracy) + facingAngle) / 180f) * Math.PI);
            final float yComponent = (float) Math.sin((((-inaccuracy / 2) + r.nextInt(inaccuracy) + facingAngle) / 180f) * Math.PI);

            bullet = createBullet(personFiring);

            bullet.location.x = this.location.x + (xComponent * bulletStartDistance);
            bullet.location.y = this.location.y + (yComponent * bulletStartDistance);

            bullet.velocity.x = xComponent * bulletSpeed;
            bullet.velocity.y = yComponent * bulletSpeed;
            if(personFiring == GameView.player && !hasInfiniteAmmo)
            {
                this.ammo --;
                Log.i("Ammo", "You have " + ammo + " bullets left");
            }
        }
    }

    public void update(float facingAngle, PointF location) {

        if(timeSinceLastShot < 1000) {
            timeSinceLastShot++;
        }
        this.facingAngle = facingAngle;
        this.location.set(location.x, location.y);

//        location.x = (float)Math.cos(facingAngle / 180f * Math.PI) * weaponDistance;
//        location.y = (float)Math.sin(facingAngle / 180f * Math.PI) * weaponDistance;

        //If this weapon is on the ground and it's touching the player
        //pick up the gun (onGround = false;)
        //Do not remove the weapon from the list because then you don't drop it.
        //set the player's current weapon to this weapon.
        if(this.onGround && this.isTouching(GameView.player)) {
            GameView.player.dropWeapon();
            this.onGround = false;
            GameView.player.currentWeapon = this;
        }
    }

//    public void update() {
//
//        //If this weapon is on the ground and it's touching the player
//        //pick up the gun (onGround = false;)
//        //Do not remove the weapon from the list because then you don't drop it.
//        //set the player's current weapon to this weapon.
//        if(this.onGround && this.isTouching(GameView.player)) {
//            GameView.player.dropWeapon(GameView.player.currentWeapon);
//            this.onGround = false;
//            GameView.player.currentWeapon = this;
//            GameView.player.checkAmmo();
//        }
//    }

    protected abstract Bullet createBullet(Soldier personFiring);
}