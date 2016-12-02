package com.example.karin.minesweeper.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.karin.minesweeper.R;

import org.w3c.dom.Text;

public class EndGameActivity extends AppCompatActivity {

    private String result;
    TextView txtRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        Intent intent = getIntent();
        result = intent.getStringExtra(StartPageActivity.DETAILS);
        txtRes = (TextView)findViewById(R.id.textView);
        txtRes.setText(result);

    }
}
