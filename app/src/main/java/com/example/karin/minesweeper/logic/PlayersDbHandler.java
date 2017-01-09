package com.example.karin.minesweeper.logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Avi on 07/01/2017.
 */

public class PlayersDbHandler extends SQLiteOpenHelper{
    private static final String LOGCAT = null;
    private static PlayersDbHandler pDbInstance;

    public static synchronized PlayersDbHandler getInstance(Context context) {

        if (pDbInstance == null) {
            pDbInstance = new PlayersDbHandler(context.getApplicationContext());
        }
        return pDbInstance;
    }

    private PlayersDbHandler(Context applicationcontext)
    {
        super(applicationcontext, "scoresDB.db", null, 1);
        Log.d(LOGCAT,"Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query;
        query = "CREATE TABLE HighScores ( PlayerId INTEGER PRIMARY KEY, PlayerName TEXT, PlayerTime TEXT, PlayerLevel TEXT, PlayerAltitude REAL, PlayerLongitude REAL)";
        db.execSQL(query);
        Log.d(LOGCAT,"HighScores Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String query;
        query = "DROP TABLE IF EXISTS HighScores";
        db.execSQL(query);
        onCreate(db);
    }

    public void insertHighScore(String playerName, String playerTime, String playerLevel, Double playerAltitude, Double playerLongitude)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PlayerName", playerName);
        values.put("PlayerTime", playerTime);
        values.put("PlayerLevel", playerLevel);
        values.put("PlayerAltitude", playerAltitude);
        values.put("PlayerLongitude", playerLongitude);
        database.insert("HighScores", null, values);
        database.close();
    }

    /*
    public int updateHighScore(HashMap<String, String> queryValues)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("StudentName", queryValues.get("StudentName"));
        return database.update("Students", values, "StudentId" + " = ?", new String[] { queryValues.get("StudentId") });
        //String updateQuery = "Update words set txtWord='"+word+"' where txtWord='"+ oldWord +"'";
        // Log.d(LOGCAT,updateQuery); //database.rawQuery(updateQuery, null);
        // return database.update("words", values, "txtWord = ?", new String[] { word });
    }*/

    public void deleteHighScore(int id)
    {
        Log.d(LOGCAT,"delete");
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM HighScores where PlayerId='"+ id +"'";
        Log.d("query",deleteQuery);
        database.execSQL(deleteQuery);
    }

    public ArrayList<PlayerScore> getAllScores(String playerLevel) {
        ArrayList<PlayerScore> arr = new ArrayList<PlayerScore>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor  =  db.rawQuery( "select * from HighScores", null );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PlayerScore p = new PlayerScore();
                p.setPlayerName(cursor.getString(1));
                p.setPlayerTime(cursor.getString(2));
                p.setPlayerLevel(cursor.getString(3));
                p.setPlayerAltitude(Double.parseDouble(cursor.getString(4)));
                p.setPlayerLongitude(Double.parseDouble(cursor.getString(5)));
                // Adding contact to list
                arr.add(p);
            } while (cursor.moveToNext());
        }

        return arr;
    }
}
