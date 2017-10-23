package com.williamrobertwalker.topdownshooter;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;


class NextLevelBackgroundTile extends BackgroundTile {

    public static Context context;

    public NextLevelBackgroundTile(PointF location) {
        super(9, location);
    }

    public void update() {
        if (GameView.player.location.x > location.x && GameView.player.location.x < location.x + image.getWidth()
                && GameView.player.location.y > location.y && GameView.player.location.y < location.y + image.getHeight()) //Character went to the far right
        {
            //Go to next Level!
            Log.i("LEVEL", "You have just gone to the NEXT LEVEL!");
            GameView.level++;
            MainActivity.gameView1.generateLevel(GameView.level);
        }
    }
}