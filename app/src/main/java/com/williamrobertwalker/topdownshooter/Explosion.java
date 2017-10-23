package com.williamrobertwalker.topdownshooter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;

import java.util.ArrayList;

public class Explosion {
    PointF location;
    private final Bitmap[] spritesheet;
    private Animation animation;
    private int radius;
    private int numFrames;
    private boolean rawImageFlipped = false;
    private boolean dieOnNextFrame = false;
    private Matrix matrix;

    Explosion(Bitmap rawImage, PointF location, short numFrames) {

        this.location = location;
//        facingAngle     = 0;
        this.matrix = new Matrix();
        this.numFrames = numFrames;
        if (rawImage.getWidth() < rawImage.getHeight()) {
            radius = rawImage.getWidth() / 2;
        } else {
            radius = rawImage.getWidth() / 2;
            rawImageFlipped = true;
        }

        animation = new Animation();
        spritesheet = new Bitmap[numFrames];

        if (!rawImageFlipped) {
            for (int i = 0; i < numFrames; i++) {
                spritesheet[i] = Bitmap.createBitmap(rawImage, 0, i * (rawImage.getHeight() / numFrames), rawImage.getWidth(), rawImage.getHeight() / numFrames);
            }
        } else {
            for (int i = 0; i < numFrames; i++) {
                spritesheet[i] = Bitmap.createBitmap(rawImage, i * (rawImage.getWidth() / numFrames), 0, rawImage.getWidth() / numFrames, rawImage.getHeight());
            }
        }

        animation.setDelay(100);
        animation.setFrames(spritesheet);

        GameView.explosionList.add(this);
    }

    public void update() {
        animation.update();
        if (animation.getFrame() == animation.getFrames().length - 1) {
            dieOnNextFrame = true;
        } else if (animation.getFrame() == 0 && dieOnNextFrame) {
            die();
        }
    }

    private void die() {
        ArrayList<Explosion> tempExplosion = new ArrayList<>(GameView.explosionList);

        tempExplosion.remove(this);

        GameView.explosionList = tempExplosion;
    }

    public void draw(Canvas canvas) {
//        canvas.drawBitmap(animation.getImage(), location.x - GameView.viewOffset.x, location.y - GameView.viewOffset.y, null);
        matrix.postTranslate(-radius, -radius);//move matrix to center of image
        matrix.postTranslate(location.x - GameView.viewOffset.x, location.y - GameView.viewOffset.y);
        canvas.drawBitmap(animation.getImage(), matrix, null);
        matrix.reset(); //clear matrix
    }
}