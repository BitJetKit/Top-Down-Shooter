package com.williamrobertwalker.topdownshooter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.williamrobertwalker.topdownshooter.Weapons.Weapon;

import java.util.Random;


public abstract class Soldier {
    private         Bitmap[]    spriteSheet;
    protected       Animation   animation;
    public final    PointF      location;
    protected       Matrix      matrix; //you can use android.openGL.Matrix I wonder if it's faster.
                    float       facingAngle;
    public          int         radius;
    protected       PointF      velocity;
    int                         health;
    public          int         maxHealth;
    public          boolean     killed;
    protected       boolean     hitFlicker;
    protected       int         hitFlickerCount;
                    short       walkDirection;
                    float       turnDirection;
                    float       moveSpeed;
                    boolean     isFiring;
                    float       turnSpeed;
    public          Weapon      currentWeapon;
    protected       short       numFrames;
    private         Paint       paint;


    Soldier(Bitmap rawImage, PointF location, short numFrames) {


        this.location = location;
        facingAngle = 0;
        hitFlicker = false;
        killed = false;
        walkDirection = 0;
        turnDirection = 0;
        moveSpeed = 0.2f;
        isFiring = false;
        velocity = new PointF();
        matrix = new Matrix();
        turnSpeed = 1;
        this.numFrames = numFrames;
        boolean rawImageFlipped = false;
        paint = new Paint();
        paint.setFilterBitmap(true);


        if (rawImage.getWidth() < rawImage.getHeight()) {
            radius = rawImage.getWidth() / 2;
        } else {
            radius = rawImage.getWidth() / 2;
            rawImageFlipped = true;
        }

        animation = new Animation();
        spriteSheet = new Bitmap[numFrames];

        if (!rawImageFlipped) {
            for (int i = 0; i < numFrames; i++) {
                spriteSheet[i] = Bitmap.createBitmap(rawImage, 0, i * (rawImage.getHeight() / numFrames), rawImage.getWidth(), rawImage.getHeight() / numFrames);
            }
        } else {
            for (int i = 0; i < numFrames; i++) {
                spriteSheet[i] = Bitmap.createBitmap(rawImage, i * (rawImage.getWidth() / numFrames), 0, rawImage.getWidth() / numFrames, rawImage.getHeight());
            }
        }

        animation.setDelay(80);
        animation.setFrames(spriteSheet);


    }

//    public abstract void draw(Canvas canvas);

    public void pointTowardWithVarience(PointF point, int variance) {

        float deltaX = this.location.x - point.x;
        float deltaY = this.location.y - point.y;

        double angle = Math.atan2((double) deltaY, (double) deltaX);
        Random r = new Random();
        this.facingAngle = (173) + r.nextInt(variance) + (float) (Math.toDegrees(angle));
    }


    public void pointToward(PointF point) {

        float deltaX = this.location.x - point.x;
        float deltaY = this.location.y - point.y;

        double angle = Math.atan2((double) deltaY, (double) deltaX);
        this.facingAngle = 180 + (float) (Math.toDegrees(angle));
    }


    public void draw(Canvas canvas) {
        if (!hitFlicker && !killed) {

            matrix.postTranslate(-radius, -radius);//move matrix to center of image
            matrix.postRotate(facingAngle); //rotate
            matrix.postTranslate(location.x - GameView.viewOffset.x, location.y - GameView.viewOffset.y);

            canvas.drawBitmap(animation.getImage(), matrix, paint);
            currentWeapon.draw(canvas);
            matrix.reset(); //clear matrix
            //draw current weapon
        }
    }

    protected void die() {
        killed = true;
        //image.recycle();
        spriteSheet = null;
    }

    protected void move() {
        location.x += velocity.x;
        location.y += velocity.y;
    }

    public void takeDamage(int damage) {
        health -= damage;
        hitFlickerCount = 4;
    }

    public void dropWeapon() {
        if(this.getClass() == Player.class && GameView.player.weaponInventory.size() > 0)
        {


            float xOffset = (float) Math.cos(facingAngle / 180f * Math.PI) * 200f; //* 200f
            float yOffset = (float) Math.sin(facingAngle / 180f * Math.PI) * 200f;

            //Set the currentWeapon's x and y location to the Soldier's hands.
            currentWeapon.location.x = location.x + xOffset * -0.7f;
            currentWeapon.location.y = location.y + yOffset * -0.7f;

            //Set the currentWeapon's facingAngle to this soldier's facingAngle.
            currentWeapon.facingAngle = facingAngle + 45f;

            //Update the currentWeapon.
            //currentWeapon.update(time);

            currentWeapon.onGround = true;
            GameView.weaponList.add(currentWeapon);
            GameView.player.currentWeapon = null;
            GameView.player.nextWeapon();
        }
        //currentWeaponToDrop.location.x += 100;
        //currentWeaponToDrop.location.y += 100;

        // Create two floats called xOffset and yOffset used to put the gun in the Soldier's hands.
        //Use sin and cos to fill xOffset and yOffset.
        else {
            float xOffset = (float) Math.cos(facingAngle / 180f * Math.PI) * 200f;
            float yOffset = (float) Math.sin(facingAngle / 180f * Math.PI) * 200f;

            //Set the currentWeapon's x and y location to the Soldier's hands.
            currentWeapon.location.x = location.x + xOffset * -0.7f;
            currentWeapon.location.y = location.y + yOffset * -0.7f;

            //Set the currentWeapon's facingAngle to this soldier's facingAngle.
            currentWeapon.facingAngle = facingAngle + 45f;

            //Update the currentWeapon.
            //currentWeaponToDrop.update(time);

            currentWeapon.onGround = true;
        }
    }


    public void update() {
        if (!killed) {
            if (hitFlickerCount > 0) {
                hitFlickerCount--;
                hitFlicker = !hitFlicker; //possible speed up by reversing these.
            } else {
                hitFlicker = false;
            }

            if (health <= 0) {
                die();
            }

            velocity.x = (float) Math.cos(facingAngle / 180 * Math.PI) * walkDirection * moveSpeed;// getting a distance from an angle.
            velocity.y = (float) Math.sin(facingAngle / 180 * Math.PI) * walkDirection * moveSpeed;

            facingAngle = facingAngle + turnSpeed * turnDirection;

            currentWeapon.update(facingAngle, this.location);
            if (walkDirection != 0) {
                animation.update();
            }
//            if (animation.getFrame()<animation.getFrames().length) {
//            }
            move();


            int size = GameView.wallList.size();
            for(int i = 0; i < size ; i++)
            {
                double distanceFromSoldier = Math.sqrt((GameView.wallList.get(i).location.x - this.location.x) * (GameView.wallList.get(i).location.x - this.location.x) +
                        (GameView.wallList.get(i).location.y - this.location.y) * (GameView.wallList.get(i).location.y - this.location.y));

                if (distanceFromSoldier < (/*FIXME wall.getWidth()*/150 * 2)) {
                    if (this.isTouchingWall(GameView.wallList.get(i))) {
                        pushFrom(GameView.wallList.get(i).pointNearestTo(this.location));
                    }
                }
            }
            //DONE: Get rid of this !ing foreach loop and make it a proper loop.
//            for (Wall wall : GameView.wallList) {
//                double distanceFromSoldier = Math.sqrt((wall.location.x - this.location.x) * (wall.location.x - this.location.x) +
//                        (wall.location.y - this.location.y) * (wall.location.y - this.location.y));
//
//                if (distanceFromSoldier < (/*TDO:FIXME wall.getWidth()*/150 + 50)) {
//                    if (this.isTouchingWall(wall)) {
//                        pushFrom(wall.pointNearestTo(this.location));
//                    }
//                }
//            }


            if (isFiring && currentWeapon.timeSinceLastShot > currentWeapon.fireDelay) {
                currentWeapon.fireWithInaccuracy(this);
            }
        }
    }

    PointF nearestPoint = new PointF();
    public boolean isTouchingWall(Wall wall) {

        // First we need the nearest point on the wall to this.
        nearestPoint = wall.pointNearestTo(this.location);

        // Now see if the nearestPoint is touching the wall using Pythagorean Theorem
        float distance = (float) Math.sqrt(
                (nearestPoint.x - location.x) * (nearestPoint.x - location.x)
                        + (nearestPoint.y - location.y) * (nearestPoint.y - location.y));


        // If the distance is less than the radius, that point is in the circle
        // So set the touchPoint to nearestPoint and return true to indicate that we
        // found overlap.  Else just return false.
        return distance < this.radius;

    }

    PointF move = new PointF();
    public void pushFrom(PointF point) {
        // Calculate the actualDistance between the points point and this soldiers location, using Pythagorean Theorem.
        float actualDistance = (float) Math.sqrt((point.x - this.location.x) * (point.x - this.location.x)
                + (point.y - this.location.y) * (point.y - this.location.y));

        //Just leave this function if the actualDistance is 0 (somehow this solider is on the point point)
        if (actualDistance == 0) {
            return;
        }

        //A float called desiredDistance is set to the radius of this soldier plus 1, to push him slightly off the wall.
        float desiredDistance = this.radius;

        // The amount to multiply against the move direction.
        float proportion = desiredDistance / actualDistance;

        //Initialize a new PointF vector called move and set the x and y coordinates to
        //the distance between this Soldier and point’s x and y coordinates.  Then adjust its
        //length by multiplying it by the scalar above.
        move.set(this.location.x - point.x, this.location.y - point.y);
        move.x *= proportion;
        move.y *= proportion;

        // Move this object away from point
        this.location.x = point.x + move.x;
        this.location.y = point.y + move.y;
    }


    //Setters:
    public void setWalkDirection(short walkDirection) {
        this.walkDirection = walkDirection;
    }

    public void setTurnDirection(short turnDirection) {
        this.turnDirection = turnDirection;
    }
}