package com.example.karin.minesweeper.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.karin.minesweeper.R;

/**
 * Created by Avi on 05/12/2016.
 */

public class NewHighScoreActivity extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        getSupportActionBar().hide();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.5),(int)(height*0.3));

        final String score = getIntent().getStringExtra("SCORE");
        final String level = getIntent().getStringExtra("LEVEL");
        final Intent intent = new Intent(this,StartPageActivity.class);

        TextView s = (TextView)findViewById(R.id.time);
        final EditText e = (EditText)findViewById(R.id.name);
        s.setText(score);

        Button b = (Button)findViewById(R.id.submit);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = String.valueOf(e.getText());
                if(name.isEmpty())
                    name = "Anonymous";

                name = name + " - " +score;
                SharedPreferences.Editor scoresEditor = getSharedPreferences("scores", MODE_PRIVATE).edit();
                scoresEditor.putString(level, (String) name);
                scoresEditor.apply();

                startActivity(intent);
            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NewHighScoreActivity.this,StartPageActivity.class);
        startActivity(intent);
        finish();

    }
}
