package com.williamrobertwalker.topdownshooter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

public class HomingMissile {
    private         Animation           animation;
    protected       Matrix              matrix; //you can use android.openGL.Matrix I wonder if it's faster.
    public          boolean             bounces = false;
    private         float               life = 4.0f;
    protected final int                 damage = 1;  //default damage is 1
    private         boolean             rawImageFlipped = false;
    public          double              facingAngle;
    private         double              closestDistance;
    private         double              distance;
    private         PointF              desired = new PointF();

    //For forces version of homing missiles.
    public          PointF              location;
    private         PointF              acceleration;
    public          PointF              velocity;
    public          float               totalVelocity;
    private         float               maxSpeed = 14f, maxForce = 0.7f;
    private         Paint               paint;


    // A reference to the Soldier that fired us, the bullet (An instance of this class).
    //This gets changed later on to become the soldier that fired the buttet. (An instance of this class)
    private final Soldier parent;
    private PointF target = new PointF();

    private final int radius;

    public HomingMissile(Bitmap rawImage, Soldier soldier, PointF location, int numFrames) //DO NOT INHERIT THIS CLASS. SIMPLY MODIFY A BULLET INSTANCE PROPERTIES.
    {
        //Make your parent the soldier for making sure the soldier doesn't kill themself or an "ally" of them incase.
        parent = soldier;
        facingAngle = parent.facingAngle;

        //Three properties of the locationnesses and rate of change of location etc.
        this.location = location;
        velocity = new PointF();
        acceleration = new PointF();

        matrix = new Matrix();

        final Bitmap[] spriteSheet = new Bitmap[numFrames];

        //Setting the sizes of the bitmap images for the drawing.
        if (rawImage.getWidth() < rawImage.getHeight()) {
            this.radius = rawImage.getWidth() / 2;
        } else {
            this.radius = rawImage.getHeight() / 2;
            rawImageFlipped = true;
        }

        animation = new Animation();


        //More drawing preparation !$@#
        if (!rawImageFlipped) {
            for (int i = 0; i < numFrames; i++) {
                spriteSheet[i] = Bitmap.createBitmap(rawImage, 0, i * (rawImage.getHeight() / numFrames), rawImage.getWidth(), rawImage.getHeight() / numFrames);
            }
        } else {
            for (int i = 0; i < numFrames; i++) {
                spriteSheet[i] = Bitmap.createBitmap(rawImage, i * (rawImage.getWidth() / numFrames), 0, rawImage.getWidth() / numFrames, rawImage.getHeight());
            }
        }

        animation.setDelay(4);
        animation.setFrames(spriteSheet);

        paint = new Paint();
        paint.setFilterBitmap(true);

        GameView.homingMissileList.add(this);


    }
    public void draw(Canvas canvas) {
        matrix.postTranslate(-this.radius, -this.radius);//move matrix to center of image
        matrix.postRotate((float) this.facingAngle); //rotate
        matrix.postTranslate(location.x - GameView.viewOffset.x, location.y - GameView.viewOffset.y);
        canvas.drawBitmap(animation.getImage(), matrix, paint);
        matrix.reset(); //clear matrix
    }

    public void update()
    {

        if(!GameView.enemyList.isEmpty()) {
            //Update animation
            animation.update();

            //The life of a homingMissile
            life -= 1 / 30f;
            if (life <= 0) {
                die();
            }


            // All bullets can hit the player, unless the player is dead.
            if (this.isTouching(GameView.player) && !GameView.player.killed) {
                GameView.player.takeDamage(this.damage);
                die();
            }

            //Damage the enemy!
            for (int i = 0; i < GameView.enemyList.size(); i++) {

                if (this.isTouching(GameView.enemyList.get(i))) {
                    GameView.enemyList.get(i).takeDamage(this.damage);
                    this.die();
                }
            }


            //BEGIN HOMING MISSILE CODE

            //Detect which enemy is the closest.
            //Start with thinking the first enemy in the list being the closest. Because this makes cents.
            closestDistance = Math.sqrt((GameView.enemyList.get(1).location.x - this.location.x) * (GameView.enemyList.get(1).location.x - this.location.x) +
                    (GameView.enemyList.get(1).location.y - this.location.y) * (GameView.enemyList.get(1).location.y - this.location.y));
            this.target.set(GameView.enemyList.get(1).location.x, GameView.enemyList.get(1).location.y); //TODO: No! No creating things!

            //Now test each and every !@#$ing enemy in the list and see if its closer than the last. This will kill performance dammit. Isn't there a better way to do this?
            //This loop is clean.
            for (Soldier target : GameView.enemyList)
            {
                //Calculate the distance between this and the current target soldier.
                distance = Math.sqrt((target.location.x - this.location.x) * (target.location.x - this.location.x) +
                        (target.location.y - this.location.y) * (target.location.y - this.location.y));

                //If it is closer than that of the previous closest distance, then make the current closest one that one.
                if(distance < closestDistance)
                {
                    closestDistance = distance;
                    this.target.set(target.location.x, target.location.y);
                }
            }




            //UPDATE THE POSITION OF THE MISSILE.
//            seek(target);
//
//            if(Math.abs(velocity.x) < maxSpeed) //Limiting the velocity
//            {
//                velocity.x += acceleration.x; //Adding rate of change of velocity to velocity.
//            }
//            if(Math.abs(velocity.y) < maxSpeed) //Limit
//            {
//                velocity.y += acceleration.y; //Limiting the velocity
//            }
//            move();
//
//            acceleration.set(0, 0);
//
//            facingAngle = Math.toDegrees(Math.atan2(velocity.y, velocity.x));


            //Extremely lame atan2 missile. Goes in exactly the direction required to hit the enemy.
//            double angleToTarget = Math.atan2(target.y - this.location.y, target.x - this.location.x) * 180 / Math.PI;
//            facingAngle = angleToTarget;
//            velocity.x = (float) Math.cos(facingAngle / 180 * Math.PI) * speed;// getting a distance from an angle.
//            velocity.y = (float) Math.sin(facingAngle / 180 * Math.PI) * speed;
//            move();

            desired.set(target.x - location.x, target.y - location.y); //TODO: I said NO CREATING THINGS!

            distance = (float)Math.sqrt((desired.x * desired.x) + (desired.y * desired.y));

            desired.x /= distance;
            desired.y /= distance;

            velocity.x += desired.x * maxForce;
            velocity.y += desired.y * maxForce;

            totalVelocity = (float)Math.sqrt((velocity.x * velocity.x) + (velocity.y * velocity.y));

            if (totalVelocity > maxSpeed)
            {
                velocity.x = (velocity.x * maxSpeed) / totalVelocity;
                velocity.y = (velocity.y * maxSpeed) / totalVelocity;
            }
            facingAngle = Math.toDegrees(Math.atan2(velocity.y, velocity.x));

            move();
        } else {
            die();
        }
    }

    // Seek steering force algorithm
    void seek(PointF target) {
        PointF desired = new PointF(target.x - location.x, target.y - location.y);

        desired = normalize(desired);

        desired.x *= maxSpeed;
        desired.y *= maxSpeed;

        //Create a steering force:
        PointF steeringForce = new PointF(desired.x - velocity.x, desired.y - velocity.y);

        //This appears to be the problem.
        if(steeringForce.x >= maxForce)//Limiting the force (The force cannot be strong with this one.)
        {
            steeringForce.x = maxForce;
        }
        else if(steeringForce.x <= -maxForce)
        {
            steeringForce.x = -maxForce;
        }
        if(steeringForce.y >= maxForce)//Limiting the force
        {
            steeringForce.y = maxForce;
        }
        else if(steeringForce.y <= -maxForce)
        {
            steeringForce.y = -maxForce;
        }
        applyForce(steeringForce);


    }


    // Newton’s second law; we could divide by mass if we wanted.
    private void applyForce(PointF force) {

        acceleration.x += force.x; //Adding rate of change of velocity to velocity.
        acceleration.y += force.y; //Limiting the velocity

//        acceleration.add(force); //This is how its SUPPOSED to work.
    }

    PointF vector2 = new PointF();
    public PointF normalize(PointF vector) {


        float length = (float)Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        if (length != 0) {
            vector2.x = vector.x/length;
            vector2.y = vector.y/length;
        }
        return vector2;
    }

    private boolean isTouching(Soldier soldier) {
        double distance = Math.sqrt((soldier.location.x - this.location.x) * (soldier.location.x - this.location.x) +
                (soldier.location.y - this.location.y) * (soldier.location.y - this.location.y));

        return distance < (this.radius + soldier.radius);
    }

    public void die() {
//        Add in a temp list and to the thing so that concurrentModificationException doesn't pop out and say !$@# you!
        new Explosion(GameView.imageMap.get("rawExplosionImage"), new PointF(this.location.x, this.location.y), (short) 6);
        ArrayList<HomingMissile> tempHomingMissiles = new ArrayList<>(GameView.homingMissileList);

        tempHomingMissiles.remove(this);

        GameView.homingMissileList = tempHomingMissiles;

    }

    private void move() {
        location.x += velocity.x;
        location.y += velocity.y;
    }

    public int getRadius() {
        return radius;
    }
}
