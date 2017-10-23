package com.williamrobertwalker.topdownshooter;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


class DrawThread extends Thread {

    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private volatile boolean running = false;

    public DrawThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    @Override
    public void run() //Game Clock
    {
        //super.run();
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int framecount = 0;
        int GAME_FPS = 60;
        long targetTime = 1000 / GAME_FPS;//Each time you run the game loop you want it to take 1000/60 seconds (Time of one frame at 60FPS.)
        Canvas canvas;

        while (running) {
            startTime = System.nanoTime(); //When this iteration first started

            if(surfaceHolder.getSurface().isValid()) //Remove the need for a try/catch/finally statement.
            {
                canvas = surfaceHolder.lockCanvas();
                synchronized (GameView.syncLock) {
                    gameView.draw(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }


            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try {
                //Thread.sleep(waitTime);
                sleep(waitTime);
            } catch (Exception e) {
//                e.printStackTrace();
            }

            totalTime += System.nanoTime() - startTime; //The total time this iteration took
            framecount++;
            if (framecount == GAME_FPS) {
                double averageFPS = 1000 / ((totalTime / framecount) / 1000000);
                framecount = 0;
                totalTime = 0;
                Log.i("FPS", "FPS: " + averageFPS);
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {

        return running;
    }
}