package com.williamrobertwalker.topdownshooter.Weapons;

import android.graphics.PointF;

import com.williamrobertwalker.topdownshooter.Bullet;
import com.williamrobertwalker.topdownshooter.GameView;
import com.williamrobertwalker.topdownshooter.Soldier;

public class Pistol extends Weapon {


    public Pistol(PointF location) {

        super(GameView.imageMap.get("pistolImage"), location);

        this.bulletSpeed = 11f;
        this.weaponDistance = 30;
        this.bulletStartDistance = 75f;
        this.fireDelay = 30;
        this.inaccuracy = 5;
        this.hasInfiniteAmmo = true;
    }

    @Override
    public Bullet createBullet(Soldier personFiring) {
        return new Bullet(GameView.imageMap.get("rawBulletImage"), personFiring, new PointF(), 4);
    }
}