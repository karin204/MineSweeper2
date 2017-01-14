package com.example.karin.minesweeper.logic;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Avi on 08/01/2017.
 */

public class DbSingleton
{
    private static DbSingleton dbSingleton;
    private static PlayersDbHandler db;
    private static HashMap<String,ArrayList<PlayerScore>> scoresMap = new HashMap<>();
    private static ArrayList<PlayerScore> easy;
    private static ArrayList<PlayerScore> medium;
    private static ArrayList<PlayerScore> hard;
    private  static boolean isChanged = false;
    private static boolean firstRun = true;

    private DbSingleton()
    {
        scoresMap.put("Easy", db.getAllScores("Easy"));
        scoresMap.put("Medium", db.getAllScores("Medium"));
        scoresMap.put("Hard", db.getAllScores("Hard"));
    }

    public static synchronized DbSingleton getInstance(Context context){
        if(dbSingleton == null)
        {
            db = PlayersDbHandler.getInstance(context);
            dbSingleton = new DbSingleton();
        }
        return dbSingleton;
    }

    public static synchronized ArrayList<PlayerScore> getPlayerScoresByLevel(String level) {
        return scoresMap.get(level);
    }

    public static void updateDB()
    {
        db.insertHighScore(scoresMap);
    }

    public static boolean isChanged() {
        return isChanged;
    }

    public static void setIsChanged(boolean isChanged) {
        DbSingleton.isChanged = isChanged;
    }

    public static boolean isFirstRun() {
        return firstRun;
    }

    public static void setFirstRun(boolean firstRun) {
        DbSingleton.firstRun = firstRun;
    }
}
