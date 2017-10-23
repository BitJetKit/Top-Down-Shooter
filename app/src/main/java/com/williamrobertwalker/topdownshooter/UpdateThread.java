package com.williamrobertwalker.topdownshooter;

import android.util.Log;


class UpdateThread extends Thread {

    private final GameView gameView;
    private volatile boolean running = false;

    public UpdateThread(GameView gameView) {
        super();
        this.gameView = gameView;
        this.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void run() //Game Clock
    {
        //super.run();
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int tickCount = 0;
        int GAME_TPS = 62;
        long targetTime = 1000 / GAME_TPS;//Each time you run the game loop you want it to take 1000/60 seconds (Time of one tick at 60TPS.)


        while (running) {
            startTime = System.nanoTime(); //When this iteration first started

            synchronized (GameView.syncLock) {
                this.gameView.update();
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try {
                sleep(waitTime);
            } catch (Exception e) {
                //Fail
//                e.printStackTrace();
            }

            totalTime += System.nanoTime() - startTime; //The total time this iteration took
            tickCount++;
            if (tickCount == GAME_TPS) {
                double averageTPS = 1000 / ((totalTime / tickCount) / 1000000);
                tickCount = 0;
                totalTime = 0;
                Log.i("TPS", "Ticks per Second: " + averageTPS);
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