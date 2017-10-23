package com.williamrobertwalker.topdownshooter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class SettingsPopup extends Activity {

    public int width;
    public int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_popup);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        this.width = displayMetrics.widthPixels;
        this.height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (this.width * 0.80), (int) (this.height * 0.80));

        final ListView listView = (ListView) findViewById(R.id.listView);

        final String[] values = new String[]{"Reset Game", "Previous Weapon", "Next Weapon", "Kill All Enemies", "Load Level 1", "Load Level 2", "Load Level 3", "Drop Weapon"};

        final ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, values);

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        // React to user clicks on item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {

                // We know the View is a TextView so we can cast it
//                TextView clickedView = (TextView) view;

                if (id == 0) { // R.id.reset
                    GameView.level = 1;
                    synchronized(GameView.syncLock) {
                        MainActivity.gameView1.generateLevel(1);
                        GameView.player.location.x = 480;
                        GameView.player.location.y = 215;
                    }
                }

                else if(id == 1) { //R.id.previousWeapon
                    GameView.player.previousWeapon();
                }

                else if(id == 2) { //R.id.nextWeapon
                    GameView.player.nextWeapon();
                }

                else if(id == 3) { //R.id.killEnemies
                    for(Soldier soldier : GameView.enemyList) {
                        soldier.takeDamage(soldier.health);
                    }
                }
                else if(id == 4) { //R.id.level1
                    GameView.level = 1;
                    synchronized(GameView.syncLock) {
                        MainActivity.gameView1.generateLevel(1);
                        GameView.player.location.x = 480;
                        GameView.player.location.y = 215;
                    }
                }
                else if(id == 5) { //R.id.level2
                    GameView.level = 2;
                    synchronized(GameView.syncLock) {
                        MainActivity.gameView1.generateLevel(2);
                        GameView.player.location.x = 480;
                        GameView.player.location.y = 215;
                    }
                }
                else if(id == 6) { //R.id.level3
                    GameView.level = 3;
                    synchronized(GameView.syncLock) {
                        MainActivity.gameView1.generateLevel(3);
                        GameView.player.location.x = 480;
                        GameView.player.location.y = 215;
                    }
                }
                else if(id == 7) { //R.id.level3
                    synchronized(GameView.syncLock) {
                        GameView.player.dropWeapon();
                    }
                }
                else{
                    new UnsupportedOperationException().printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainActivity.resumeGame();
        super.onDestroy();
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
