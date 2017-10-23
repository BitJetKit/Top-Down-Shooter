package com.williamrobertwalker.topdownshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;
//import android.util.Log;

import com.williamrobertwalker.topdownshooter.Weapons.InaccurateAndSlow;
import com.williamrobertwalker.topdownshooter.Weapons.Pistol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Class Load holds all of the Loading methods in a nicely organized fashion.
 */
public class Load {

    /**
     * Loads the background from a filename.
     * @param filename Needs the filename to load the file.
     * @param context Needs a context to open the file from assets.
     * @throws IOException
     */
    public static void background(String filename, Context context) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        int height;

        InputStream is = context.getAssets().open(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if (!line.startsWith("!")) { //If the line isn't a comment
                lines.add(line);
                width = Math.max(width, line.length());

            }
        }
        height = lines.size();

        for (int j = 0; j < height; j++) {
            String line = lines.get(j);
            for (int i = 0; i < width; i++) {

                if (i < line.length()) {
                    char character = line.charAt(i);
                    try {
                        if (Character.getNumericValue(character) >= 0) {
                            BackgroundTile backgroundTile = new BackgroundTile(GameView.getImageFromID(Character.getNumericValue(character), GameView.backgroundImageMap), new PointF(i, j));
//                            Log.i("DEBUG", "ImageID is: " + Character.getNumericValue(character) + " Bitmap is: " + getImageFromID(Character.getNumericValue(character), GameView.backgroundImageMap));
                            GameView.backgroundTileList.add(backgroundTile);
                        }
                        if (Character.getNumericValue(character) == 9) {
                            GameView.nextLevelBackgroundTile = new NextLevelBackgroundTile(new PointF(i, j));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Error", "Image did not load correctly.");
                    }
                }
            }
        }
//        Log.i("MAP", "Background map has been loaded successfully");
    }

    /**
     * Loads the background from files using the current level
     * @param level Needs the current level to load the correct files.
     * @param context Needs a context to load from the file.
     * @throws IOException Incase it explodes.
     */
    public static void background(int level, Context context) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        int height;

        InputStream is = context.getAssets().open("Levels/background_level_" + level);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if (!line.startsWith("!")) { //If the line isn't a comment
                lines.add(line);
                width = Math.max(width, line.length());

            }
        }
        height = lines.size();

        for (int j = 0; j < height; j++) {
            String line = lines.get(j);
            for (int i = 0; i < width; i++) {

                if (i < line.length()) {
                    char character = line.charAt(i);
                    try {
                        if (Character.getNumericValue(character) >= 0) {
                            BackgroundTile backgroundTile = new BackgroundTile(GameView.getImageFromID(Character.getNumericValue(character), GameView.backgroundImageMap), new PointF(i, j));
//                            Log.i("DEBUG", "ImageID is: " + Character.getNumericValue(character) + " Bitmap is: " + getImageFromID(Character.getNumericValue(character), GameView.backgroundImageMap));
                            GameView.backgroundTileList.add(backgroundTile);
                        }
                        if (Character.getNumericValue(character) == 9) {
                            GameView.nextLevelBackgroundTile = new NextLevelBackgroundTile(new PointF(i, j));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Error", "Image did not load correctly.");
                    }
                }
            }
        }
//        Log.i("MAP", "Background map has been loaded successfully");
    }


    //TODO: make it so that the width of the wall isn't based on the image. Make it based on a static number that can be changed.
    //TODO: this will make sure that when you move from a phone to a tablet you don't get more viewing distance.
    /**
     * Loads all of the walls from a filename
     * @param filename Needs the filename for the file to load. Duh.
     * @param context Needs a context to load from the file.
     * @throws IOException Incase it explodes.
     */
    public static void walls(String filename, Context context) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        int height;
        boolean loadedSuccessfully = true;

        InputStream is = context.getAssets().open(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if (!line.startsWith("!")) { //If the line isn't a comment
                lines.add(line);
                width = Math.max(width, line.length());

            }
        }
        height = lines.size();

        for (int j = 0; j < height; j++) {
            String line = lines.get(j);
            for (int i = 0; i < width; i++) {
                if (i < line.length()) {
                    char character = line.charAt(i);
                    try {
                        if (Character.getNumericValue(character) >= 0) {
                            Bitmap image = GameView.getImageFromID(Character.getNumericValue(character), GameView.wallImageMap);
                            new Wall(image, new PointF(i, j), image.getWidth(), image.getHeight());
//                            GameView.wallList.add(wall);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DEBUG", "Wall Image did not load correctly.");
                        loadedSuccessfully = false;
                    }
                    //DO THE THING
                }
            }
        }
        if (!loadedSuccessfully)
//            Log.i("MAP", "Wall map has been loaded successfully");
            Log.e("MAP", "Wall map did not load correctly.");


    }

    //TODO: make it so that the width of the wall isn't based on the image. Make it based on a static number that can be changed.
    //TODO: this will make sure that when you move from a phone to a tablet you don't get more viewing distance.
    /**
     * Loads all of the walls in the game using the level and the context.
     * @param level Requries the current level of the game to load its walls from that file.
     * @param context Needs a context to load walls from assets.
     * @throws IOException Throws IOException incase it doesn't work properly.
     */
    public static void walls(int level, Context context) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        int height;
        boolean loadedSuccessfully = true;

        InputStream is = context.getAssets().open("Levels/walls_level_" + level);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if (!line.startsWith("!")) { //If the line isn't a comment
                lines.add(line);
                width = Math.max(width, line.length());

            }
        }
        height = lines.size();

        for (int j = 0; j < height; j++) {
            String line = lines.get(j);
            for (int i = 0; i < width; i++) {
                if (i < line.length()) {
                    char character = line.charAt(i);
                    try {
                        if (Character.getNumericValue(character) >= 0) {
                            Bitmap image = GameView.getImageFromID(Character.getNumericValue(character), GameView.wallImageMap);
                            new Wall(image, new PointF(i, j), image.getWidth(), image.getHeight());
//                            GameView.wallList.add(wall);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DEBUG", "Wall Image did not load correctly.");
                        loadedSuccessfully = false;
                    }
                    //DO THE THING
                }
            }
        }
        if (!loadedSuccessfully)
//            Log.i("MAP", "Wall map has been loaded successfully");
            Log.e("MAP", "Wall map did not load correctly.");


    }


    //TODO: Make the enemies load offset by based on how wide walls are.
    public static void enemies(String filename, Context context) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        int height;
        boolean loadedSuccessfully = true;

        InputStream is = context.getAssets().open(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if (!line.startsWith("!")) { //If the line isn't a comment
                lines.add(line);
                width = Math.max(width, line.length());

            }
        }
        height = lines.size();

        for (int j = 0; j < height; j++) {
            String line = lines.get(j);
            for (int i = 0; i < width; i++) {
                if (i < line.length()) {
                    char character = line.charAt(i);
                    try {
                        if (Character.getNumericValue(character) >= 0) {
                            if (Character.getNumericValue(character) == 1) {
                                GameView.enemyList.add(new EnemySoldier(GameView.imageMap.get("rawEnemySoldierImage"), new PointF(i * 150 + 75, j * 150 + 75), (short) 4));
                            } else if (Character.getNumericValue(character) == 2) {
                                GameView.enemyList.add(new TargetSoldier(GameView.imageMap.get("rawTargetSolderImage"), new PointF(i * 150 + 75, j * 150 + 75), (short) 4));
                            } /*else {
                                Log.i("ERROR", "This is not an enemy!");
                            }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("DEBUG", "Enemy " + j + ", " + i + " did not load correctly.");
                        loadedSuccessfully = false;
                    }
                    //DO THE THING
                }
            }
        }
        if (loadedSuccessfully)
            Log.i("MAP", "Enemy map has been loaded successfully");
        else
            Log.i("MAP", "Enemy map did not load correctly.");

    }

    //TODO: Make the enemies load offset by based on how wide walls are.
    public static void enemies(int level, Context context) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        int height;
        boolean loadedSuccessfully = true;

        InputStream is = context.getAssets().open("Levels/enemies_level_" + level);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if (!line.startsWith("!")) { //If the line isn't a comment
                lines.add(line);
                width = Math.max(width, line.length());

            }
        }
        height = lines.size();

        for (int j = 0; j < height; j++) {
            String line = lines.get(j);
            for (int i = 0; i < width; i++) {
                if (i < line.length()) {
                    char character = line.charAt(i);
                    try {
                        if (Character.getNumericValue(character) >= 0) {
                            if (Character.getNumericValue(character) == 1) {
                                GameView.enemyList.add(new EnemySoldier(GameView.imageMap.get("rawEnemySoldierImage"), new PointF(i * 150 + 75, j * 150 + 75), (short) 4));
                            } else if (Character.getNumericValue(character) == 2) {
                                GameView.enemyList.add(new TargetSoldier(GameView.imageMap.get("rawTargetSolderImage"), new PointF(i * 150 + 75, j * 150 + 75), (short) 4));
                            } /*else {
                                Log.i("ERROR", "This is not an enemy!");
                            }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DEBUG", "Enemy " + j + ", " + i + " did not load correctly.");
                        loadedSuccessfully = false;
                    }
                    //DO THE THING
                }
            }
        }
        if (!loadedSuccessfully)
//            Log.i("MAP", "Enemy map has been loaded successfully");
            Log.i("MAP", "Enemy map did not load correctly.");

    }

    public static void weapons(String filename, Context context) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        int height;
        boolean loadedSuccessfully = true;

        InputStream is = context.getAssets().open(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if (!line.startsWith("!")) { //If the line isn't a comment
                lines.add(line);
                width = Math.max(width, line.length());

            }
        }
        height = lines.size();

        for (int j = 0; j < height; j++) {
            String line = lines.get(j);
            for (int i = 0; i < width; i++) {
                if (i < line.length()) {
                    char character = line.charAt(i);
                    try {
                        if (Character.getNumericValue(character) >= 0) {
                            if (Character.getNumericValue(character) == 1) {
                                GameView.weaponList.add(new Pistol(new PointF(i * 150 + 75, j * 150 + 75)));
                            } else if (Character.getNumericValue(character) == 2) {
                                GameView.weaponList.add(new InaccurateAndSlow(new PointF(i * 150 + 75, j * 150 + 75)));
                            } /*else {
                                Log.i("ERROR", "This is not a weapon!");
                            }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DEBUG", "Weapon @ location " + j + ", " + i + " did not load correctly.");
                        loadedSuccessfully = false;
                    }
                    //DO THE THING
                }
            }
        }
        if (!loadedSuccessfully)
//            Log.i("MAP", "Weapon map has been loaded successfully");
            Log.i("MAP", "Weapon map did not load correctly.");


    }

    public static void weapons(int level, Context context) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        int height;
        boolean loadedSuccessfully = true;

        InputStream is = context.getAssets().open("Levels/weapons_level_" + level);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if (!line.startsWith("!")) { //If the line isn't a comment
                lines.add(line);
                width = Math.max(width, line.length());

            }
        }
        height = lines.size();

        for (int j = 0; j < height; j++) {
            String line = lines.get(j);
            for (int i = 0; i < width; i++) {
                if (i < line.length()) {
                    char character = line.charAt(i);
                    try {
                        if (Character.getNumericValue(character) >= 0) {
                            if (Character.getNumericValue(character) == 1) {
                                Pistol pistol = new Pistol(new PointF(i * 150 + 75, j * 150 + 75));
                                pistol.onGround = true;
                                GameView.weaponList.add(pistol);
                            } else if (Character.getNumericValue(character) == 2) {
                                InaccurateAndSlow inaccurateAndSlow = new InaccurateAndSlow(new PointF(i * 150 + 75, j * 150 + 75));
                                inaccurateAndSlow.onGround = true;
                                GameView.weaponList.add(inaccurateAndSlow);
                            } /*else {
                                Log.i("ERROR", "This is not a weapon!");
                            }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DEBUG", "Weapon @ location " + j + ", " + i + " did not load correctly.");
                        loadedSuccessfully = false;
                    }
                    //DO THE THING
                }
            }
        }
        if (!loadedSuccessfully)
//            Log.i("MAP", "Weapon map has been loaded successfully");
            Log.i("MAP", "Weapon map did not load correctly.");


    }
}