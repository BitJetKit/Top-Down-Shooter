package com.williamrobertwalker.topdownshooter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

public class Wall {
    PointF location;
    private final int width;
    private final int height;
    private final Bitmap image;
    private Bullet bullet;

    public Wall(Bitmap image, PointF location, int width, int height) {

        this.location = new PointF();
        this.image = image;
        this.location.x = location.x * this.image.getWidth();
        this.location.y = location.y * this.image.getHeight();
        this.width = width;
        this.height = height;

//        Log.d("DEBUG", "Wall is: " + this + " Contents of wall: " + " image: " + image + " PointF: " + this.location + " Width: " + this.width + " Height: " + this.height);

        GameView.wallList.add(this);
    }

    public void draw(Canvas canvas) {

//        double distanceFromPlayer = Math.sqrt((GameView.player.location.x - this.location.x) * (GameView.player.location.x - this.location.x) +
//                (GameView.player.location.y - this.location.y) * (GameView.player.location.y - this.location.y));
//
//        if(distanceFromPlayer < (MainActivity.gameView1.getViewDimensions().x + 100)) {
            canvas.drawBitmap(image, location.x - GameView.viewOffset.x, location.y - GameView.viewOffset.y, null);
//        }
    }

    public void update() {

        double distanceFromPlayer = Math.sqrt((GameView.player.location.x - this.location.x) * (GameView.player.location.x - this.location.x) +
                (GameView.player.location.y - this.location.y) * (GameView.player.location.y - this.location.y));

        if(distanceFromPlayer < (MainActivity.gameView1.getViewDimensions().x + 100)) { //Make sure that this bullet is within render distance.
            //TODO: Kill this foreach loop and make it a proper loop.
//            int size = GameView.bulletList.size();
            for(int i = 0; i < GameView.bulletList.size(); i++)
            {
                bullet = GameView.bulletList.get(i);
                if(this.isTouchingBullet(bullet)) {
                    if(bullet.bounces) {
                        if(isTouchingHorizontal(bullet)) {
                            GameView.bulletList.get(i).velocity.y *= -1;
                        }
                        if(isTouchingVertical(bullet)) {
                            GameView.bulletList.get(i).velocity.x *= -1;
                        }
                    } else {
                        //Removes the Bullet
                        GameView.bulletList.get(i).die();
                    }

                }
            }
//            for(Bullet bullet : GameView.bulletList)     // BOUNCING AND STOPPING.
//            {
//                if(this.isTouchingBullet(bullet)) {
//                    if(bullet.bounces) {
//                        if(isTouchingHorizontal(bullet)) {
//                            bullet.velocity.y *= -1;
//                        }
//                        if(isTouchingVertical(bullet)) {
//                            bullet.velocity.x *= -1;
//                        }
//                    } else {
//                        //Removes the Bullet
//                        bullet.die();
//                    }
//
//                }
//            }
            //DONE: Kill this foreach loop and make it a proper loop.
//            int size = GameView.homingMissileList.size();
            for(int i = 0; i < GameView.homingMissileList.size() ; i++)
            {
                if(this.isTouchingBullet(GameView.homingMissileList.get(i))) {
                    if(GameView.homingMissileList.get(i).bounces) {
                        if(isTouchingHorizontal(GameView.homingMissileList.get(i))) {
                            GameView.homingMissileList.get(i).velocity.y *= -1;
                        }
                        if(isTouchingVertical(GameView.homingMissileList.get(i)))  // One of these statements should be true.
                        {
                            GameView.homingMissileList.get(i).velocity.x *= -1;
                        }
                    }
                    else {
                        //Removes the Bullet
                        GameView.homingMissileList.get(i).die();
                    }

                }
            }
//            for(HomingMissile homingMissile : GameView.homingMissileList)     // BOUNCING AND STOPPING.
//            {
//                if(this.isTouchingBullet(homingMissile)) {
//                    if(homingMissile.bounces) {
//                        if(isTouchingHorizontal(homingMissile)) {
//                            homingMissile.velocity.y *= -1;
//                        }
//                        if(isTouchingVertical(homingMissile))  // One of these statements should be true.
//                        {
//                            homingMissile.velocity.x *= -1;
//                        }
//                    }
//                    else {
//                        //Removes the Bullet
//                        homingMissile.die();
//                    }
//
//                }
//            }
        }


    }

    private boolean isTouchingBullet(Bullet bullet) // Normal Method to check wall.
    {
        // First you need the nearest point on this wall for the equation below.
        // This is used to to get a corner of the hypotenuse (distance) for the equation from this wall by finding the point on this wall
        // nearest to the bullet.
        PointF nearestPoint = this.pointNearestTo(bullet.location);

        // Find if nearestPoint is touching the wall using Pythagorean Theorem
        float distance = (float) Math.sqrt(
                (nearestPoint.x - bullet.location.x) * (nearestPoint.x - bullet.location.x)
                        + (nearestPoint.y - bullet.location.y) * (nearestPoint.y - bullet.location.y));


        return distance < bullet.getRadius(); //THIS IS THE TOLERANCE.

    }

    private boolean isTouchingBullet(HomingMissile homingMissile) // Normal Method to check wall.
    {
        // First you need the nearest point on this wall for the equation below.
        // This is used to to get a corner of the hypotenuse (distance) for the equation from this wall by finding the point on this wall
        // nearest to the bullet.
        PointF nearestPoint = this.pointNearestTo(homingMissile.location);

        // Find if nearestPoint is touching the wall using Pythagorean Theorem
        float distance = (float) Math.sqrt(
                (nearestPoint.x - homingMissile.location.x) * (nearestPoint.x - homingMissile.location.x)
                        + (nearestPoint.y - homingMissile.location.y) * (nearestPoint.y - homingMissile.location.y));


        return distance < homingMissile.getRadius(); //THIS IS THE TOLERANCE.

    }

    private boolean isTouchingVertical(Bullet bullet)// To detect if it's touching the side.
    {

        return bullet.location.x >= this.getLocation().x && bullet.location.x <= this.getLocation().x + 15 //Left side of wall
                || bullet.location.x >= (this.getLocation().x + this.getWidth()) - 10 && bullet.location.x <= ((this.getLocation().x + this.getWidth()) + 10); //Right side of wall
    }

    private boolean isTouchingVertical(HomingMissile homingMissile)// To detect if it's touching the side.
    {

        return homingMissile.location.x >= this.getLocation().x && homingMissile.location.x <= this.getLocation().x + 15 //Left side of wall
                || homingMissile.location.x >= (this.getLocation().x + this.getWidth()) - 10 && homingMissile.location.x <= ((this.getLocation().x + this.getWidth()) + 10); //Right side of wall
    }

    private boolean isTouchingHorizontal(Bullet bullet)// To detect if it's touching the top or the bottom.
    {

        return bullet.location.y >= this.getLocation().y && bullet.location.y <= this.getLocation().y + 15 //Top of wall
                || bullet.location.y >= (this.getLocation().y + this.getHeight()) - 10 && bullet.location.y <= (this.getLocation().y + this.getHeight()) + 10; //Bottom of wall

    }

    private boolean isTouchingHorizontal(HomingMissile homingMissile)// To detect if it's touching the top or the bottom.
    {

        return homingMissile.location.y >= this.getLocation().y && homingMissile.location.y <= this.getLocation().y + 15 //Top of wall
                || homingMissile.location.y >= (this.getLocation().y + this.getHeight()) - 10 && homingMissile.location.y <= (this.getLocation().y + this.getHeight()) + 10; //Bottom of wall

    }

    PointF  nearestPoint = new PointF();
    public PointF pointNearestTo(PointF point) {

        //Summary:
        // This function is to see where the nearest point of this instance of wall is compared to whatever calls this function.
        // This is done to figure out the other corner of the hypotenuse in a trig triangle to determine distances. The first corner is whatever called this function's location.
        // This function returns that corner.
        // In the case that a bullet calls this function, PointF nearestPoint will be the location of the bullet.


        //Check if the location.x edge of this wall is to the right of the point "point"
        //if it is, then the nearestPoint's x coordinate must be the location.x edge of this wall
        if(this.location.x > point.x) // if it's to the location.x
        {
            nearestPoint.x = this.location.x;
        }
        //Else if the right edge of this wall is to the location.x of the point "point"
        //the nearestPoint's x coordinate must be the right edge of this wall
        else if(this.location.x + this.width < point.x) {
            nearestPoint.x = this.location.x + this.width;
        }
        //If the wall's x coordinate is not to the location.x or right of point "point", Point "point"'s x coordinate must be between the location.x and right of the wall.
        //So set the nearestPoint's x coordinate equal to point's x coordinate.
        else {
            nearestPoint.x = point.x;
        }


        // Check if this top edge if this wall is under the point "point"
        // If so, then set nearestpoint.y to the top of the wall
        if(this.location.y > point.y) {
            nearestPoint.y = this.location.y;
        }
        //Else if the bottom edge of this wall is above of the point "point"
        //the nearestPoint's y coordinate must be the bottom edge of this wall
        else if(this.location.y + this.height < point.y) {
            nearestPoint.y = this.location.y + this.height;
        }
        //If the wall's y coordinate is not to the top or bottom of point "point", Point "point"'s y coordinate must be between the top and bottom of the wall.
        //So set the nearestPoint's y coordinate equal to "point"'s y coordinate.
        else {
            nearestPoint.y = point.y;
        }

        //return the corner for use in a later equation.
        return nearestPoint;
    }


    //region Getters & Setters:
    public int getWidth() {

        return width;
    }

    public int getHeight() {

        return height;
    }

    public PointF getLocation() {

        return location;
    }
    //endregion

}
