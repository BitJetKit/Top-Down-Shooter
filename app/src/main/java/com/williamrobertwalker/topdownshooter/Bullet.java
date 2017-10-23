package com.williamrobertwalker.topdownshooter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.ArrayList;

public class Bullet {
    public final PointF location;
    private Animation animation;
    public final PointF velocity;
    public boolean bounces = false;
    private float life = 2.0f;
    protected final int damage = 1;  //default damage is 1
    private boolean rawImageFlipped = false;

    // A reference to the Soldier that fired us, the bullet (An instance of this class).
    //This gets changed later on to become the soldier that fired the buttet. (An instance of this class)
    private final Soldier parent;

    private final int radius;

    public Bullet(Bitmap rawImage, Soldier soldier, PointF location, int numFrames) //DO NOT INHERIT THIS CLASS. SIMPLY MODIFY A BULLET INSTANCE PROPERTIES.
    {
        parent = soldier;
        velocity = new PointF();
        this.location = location;

        Bitmap[] spritesheet = new Bitmap[numFrames];


        if (rawImage.getWidth() < rawImage.getHeight()) {
            radius = rawImage.getWidth() / 2;
        } else {
            radius = rawImage.getWidth() / 2;
            rawImageFlipped = true;
        }

        animation = new Animation();
//        spritesheet = new Bitmap[numFrames];

        if (!rawImageFlipped) {
            for (int i = 0; i < numFrames; i++) {
                spritesheet[i] = Bitmap.createBitmap(rawImage, 0, i * (rawImage.getHeight() / numFrames), rawImage.getWidth(), rawImage.getHeight() / numFrames);
            }
        } else {
            for (int i = 0; i < numFrames; i++) {
                spritesheet[i] = Bitmap.createBitmap(rawImage, i * (rawImage.getWidth() / numFrames), 0, rawImage.getWidth() / numFrames, rawImage.getHeight());
            }
        }

        animation.setDelay(80);
        animation.setFrames(spritesheet);


        GameView.bulletList.add(this);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), location.x + -GameView.viewOffset.x, location.y + -GameView.viewOffset.y, null);
        //draw current weapon
    }

    public void update() //OVERRIDE IN
    {
        move();
        animation.update();

        life -= 1 / 30f;

        if (life <= 0) {
            die();
        }

        if (this.parent == GameView.player) //This makes sure that the stupid enemies don't kill eachother.
        {

            for (int i = 0; i < GameView.enemyList.size(); i++) {

                if (this.isTouching(GameView.enemyList.get(i))) {
                    GameView.enemyList.get(i).takeDamage(this.damage);
                    die();
                }
            }

        }

        // All bullets can hit the player, unless the player is dead.
        if (this.isTouching(GameView.player) && !GameView.player.killed) {
            GameView.player.takeDamage(this.damage);
            die();
        }

    }

    private boolean isTouching(Soldier soldier) {
        double distance = Math.sqrt((soldier.location.x - this.location.x) * (soldier.location.x - this.location.x) +
                (soldier.location.y - this.location.y) * (soldier.location.y - this.location.y));

        return distance < (this.radius + soldier.radius);
    }

    public void die() {
        ArrayList<Bullet> tempBullets = new ArrayList<>(GameView.bulletList);

        tempBullets.remove(this);
        for(int i = 0; i < animation.getFrames().length; i++) {
            animation.getFrames()[i].recycle();
        }

        GameView.bulletList = tempBullets;
    }

    private void move() {
        location.x += velocity.x;
        location.y += velocity.y;
    }

    public int getRadius() {
        return radius;
    }

    public void setLife(int life) {

        this.life = life;
    }
}