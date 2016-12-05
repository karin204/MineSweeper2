package com.example.karin.minesweeper.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.karin.minesweeper.R;

/**
 * Created by Avi on 05/12/2016.
 */

public class NewHighScoreActivity extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.5),(int)(height*0.5));

    }
}
