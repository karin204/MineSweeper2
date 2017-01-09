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

    private DbSingleton()
    {
        scoresMap.put("Easy", new ArrayList<PlayerScore>());
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
}
