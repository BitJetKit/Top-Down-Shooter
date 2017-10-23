package com.williamrobertwalker.topdownshooter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;


class BackgroundTile {

    public Bitmap image;
    protected PointF location;
//    private final int         size;


    public BackgroundTile(int imageID, PointF location) {
        this.image = GameView.getImageFromID(imageID, GameView.backgroundImageMap);
        this.location = new PointF(location.x * image.getWidth(), location.y * image.getHeight());
    }

    public BackgroundTile(Bitmap image, PointF location) {
        this.image = image;
//        try {
            this.location = new PointF(location.x * image.getWidth(), location.y * image.getHeight());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }


//    public void update()
//    {
//
//    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, location.x - GameView.viewOffset.x, location.y - GameView.viewOffset.y, null);
    }

    private void drawGrid(Canvas canvas, int size) {
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                canvas.drawBitmap(image, (i * image.getWidth()) - GameView.viewOffset.x, (j * image.getHeight()) - GameView.viewOffset.y, null); //Draw background image to canvas
            }
        }
    }
}