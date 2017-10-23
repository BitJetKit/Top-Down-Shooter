package com.williamrobertwalker.topdownshooter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;


class LoopingBackground {

    private final           Bitmap      image;
    private final           PointF[][]  panelLocation; //The locations of all of the background panels
    private                 int         size = 100;


    public LoopingBackground(Bitmap image) {
        this.image = image;
        panelLocation = new PointF[size][size];

        setPanels(image);

    }
    public LoopingBackground(Bitmap image, int size) {
        this.image = image;
        this.size = size;
        panelLocation = new PointF[size][size];

        setPanels(image);

    }


    public final void update() {
        if      (GameView.player.location.x > panelLocation[(size /2) + 1][0].x)//Character went to the far right
        {
            offsetAllPanels(image.getWidth(), 0);
        }
        else if (GameView.player.location.x < panelLocation[(size /2)][0].x)//Character went to the far left
        {
            offsetAllPanels(-image.getWidth(), 0);
        }
        else if (GameView.player.location.y > panelLocation[0][(size /2) + 1].y)//character went to the far bottom
        {
            offsetAllPanels(0, image.getHeight());
        }
        else if (GameView.player.location.y < panelLocation[0][(size /2)].y)//Character went to the far top
        {
            offsetAllPanels(0, -image.getHeight());
        }
    }

    public void draw(Canvas canvas) //receive the main canvas and draw all the things to it
    {
        drawGrid(canvas);
    }



    private void drawGrid(Canvas canvas){
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++)
            {
                canvas.drawBitmap(image, panelLocation[i][j].x + -GameView.viewOffset.x, panelLocation[i][j].y + -GameView.viewOffset.y, null); //Draw background image to canvas
            }
        }
    }
    private void offsetAllPanels(int offsetx, int offsety) {
        for(int i = 0; i < panelLocation.length ; i++) {
            for(int j = 0; j < panelLocation[i].length ; j++) {
                panelLocation[i][j].offset(offsetx, offsety);
            }
        }
//        for (PointF[] pointArray : panelLocation) {
//            for (PointF point : pointArray) {
//                point.offset(offsetx, offsety);
//            }
//        }
    }
    private void setPanels(Bitmap image) {
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++)
            {
                panelLocation[j][i] = new PointF(image.getWidth()*j,image.getHeight()*i);
            }
        }
    }
}
