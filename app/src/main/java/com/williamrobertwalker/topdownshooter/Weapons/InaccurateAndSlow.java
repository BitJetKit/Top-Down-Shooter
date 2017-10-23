package com.williamrobertwalker.topdownshooter.Weapons;

import android.graphics.PointF;

import com.williamrobertwalker.topdownshooter.Bullet;
import com.williamrobertwalker.topdownshooter.GameView;
import com.williamrobertwalker.topdownshooter.Soldier;

public class InaccurateAndSlow extends Weapon {


    public InaccurateAndSlow(PointF location) {

        super(GameView.imageMap.get("rapidGun"), location);

        this.bulletSpeed = 5;
        this.weaponDistance = 28;
        this.bulletStartDistance = 100f;
        this.fireDelay = 150;
        this.inaccuracy = 30;
        this.ammo = 3;
    }

    @Override
    public Bullet createBullet(Soldier personFiring) {

        Bullet bullet;
        bullet = new Bullet(GameView.imageMap.get("rawBulletImage"), personFiring, new PointF(), 4);
        bullet.setLife(4);
        bullet.bounces = true;
        return bullet;
    }
}