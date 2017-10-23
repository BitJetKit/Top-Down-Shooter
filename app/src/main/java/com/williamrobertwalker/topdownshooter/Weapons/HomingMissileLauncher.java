package com.williamrobertwalker.topdownshooter.Weapons;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import com.williamrobertwalker.topdownshooter.Bullet;
import com.williamrobertwalker.topdownshooter.GameView;
import com.williamrobertwalker.topdownshooter.HomingMissile;
import com.williamrobertwalker.topdownshooter.MainActivity;
import com.williamrobertwalker.topdownshooter.Soldier;

import java.util.Random;

/**
 * Created by TheWo_000 on 11/28/2015.
 */
public class HomingMissileLauncher extends Weapon {

    //Unused but mandatory constructor
    HomingMissileLauncher() {
        super(null, null);
        throw new UnsupportedOperationException();
    }

    //Actually useful constructor
    public HomingMissileLauncher(PointF location) {
        super(GameView.imageMap.get("homingMissileLauncherImage"), location);
        this.bulletSpeed = 11f;
        this.weaponDistance = 30;
        this.bulletStartDistance = 100f;
        this.fireDelay = 30;
        this.inaccuracy = 5;
        this.hasInfiniteAmmo = true;
    }

    @Override
    public void fireWithInaccuracy(Soldier personFiring) {
        if(ammo > 0 && !GameView.enemyList.isEmpty() && (closestTargetDistance() < 600)) {
            // Reset the shot timer.
            timeSinceLastShot = 0;

            Random r = new Random();
            final float xComponent = (float) Math.cos((((-inaccuracy / 2) + r.nextInt(inaccuracy) + facingAngle) / 180f) * Math.PI);
            final float yComponent = (float) Math.sin((((-inaccuracy / 2) + r.nextInt(inaccuracy) + facingAngle) / 180f) * Math.PI);

            HomingMissile homingMissile = createHomingMissile(personFiring);

            homingMissile.location.x = this.location.x + (xComponent * bulletStartDistance);
            homingMissile.location.y = this.location.y + (yComponent * bulletStartDistance);

            homingMissile.velocity.x = xComponent * bulletSpeed;
            homingMissile.velocity.y = yComponent * bulletSpeed;
            if(personFiring == GameView.player && !hasInfiniteAmmo)
            {
                this.ammo --;
                Log.i("Ammo", "You have " + ammo + " bullets left");
            }

            Log.i("Fire", "Homing Missile fired successfully");
        }
    }

    @Override
    public void fire(Soldier personFiring) {
        if(ammo > 0 && !GameView.enemyList.isEmpty() && (closestTargetDistance() < MainActivity.gameView1.getViewDimensions().x)) {
            // Reset the shot timer.
            timeSinceLastShot = 0;

            final float xComponent = (float) Math.cos(facingAngle / 180f * Math.PI);
            final float yComponent = (float) Math.sin(facingAngle / 180f * Math.PI);

            HomingMissile homingMissile = createHomingMissile(personFiring);

            homingMissile.location.x = this.location.x + (xComponent * bulletStartDistance);
            homingMissile.location.y = this.location.y + (yComponent * bulletStartDistance);

            homingMissile.velocity.x = xComponent * bulletSpeed;
            homingMissile.velocity.y = yComponent * bulletSpeed;

            if(personFiring == GameView.player && !hasInfiniteAmmo)
            {
                this.ammo --;
                Log.i("Ammo", "You have " + ammo + " bullets left");
            }
            Log.i("Fire", "Homing Missile fired successfully");
        }
    }

    public double closestTargetDistance()
    {
        //Detect which enemy is the closest.
        //Start with thinking the first enemy in the list being the closest. Because this makes cents.
        double closestDistance = Math.sqrt((GameView.enemyList.get(1).location.x - this.location.x) * (GameView.enemyList.get(1).location.x - this.location.x) +
                (GameView.enemyList.get(1).location.y - this.location.y) * (GameView.enemyList.get(1).location.y - this.location.y));

        //Now test each and every !@#$ing enemy in the list and see if its closer than the last. This will kill performance dammit. Isn't there a better way to do this?
        //This loop is clean.
        for (Soldier target : GameView.enemyList)
        {
            //Calculate the distance between this and the current target soldier.
            double distance = Math.sqrt((target.location.x - this.location.x) * (target.location.x - this.location.x) +
                    (target.location.y - this.location.y) * (target.location.y - this.location.y));

            //If it is closer than that of the previous closest distance, then make the current closest one that one.
            if(distance < closestDistance)
            {
                closestDistance = distance;
            }
        }
        return closestDistance;
    }

    @Override
    protected Bullet createBullet(Soldier personFiring) {
        return null;
    }

    protected HomingMissile createHomingMissile(Soldier personFiring)
    {
        HomingMissile homingMissile;
        homingMissile = new HomingMissile(GameView.imageMap.get("rawHomingMissileImage"), personFiring, new PointF(), 13);
//        homingMissile.bounces = true;
        return homingMissile;
    }
}
