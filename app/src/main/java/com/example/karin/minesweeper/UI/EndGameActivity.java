package com.example.karin.minesweeper.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.karin.minesweeper.R;

import org.w3c.dom.Text;

public class EndGameActivity extends AppCompatActivity {

    private String result;
    private String time;
    TextView txtRes;
    TextView txtTime;
    RelativeLayout endLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        Intent intent = getIntent();
        result = intent.getStringExtra(GameActivity.RESULT) ;
        time = intent.getStringExtra(GameActivity.TIMER);
        endLayout = (RelativeLayout)findViewById(R.id.activity_end_game);

        txtRes = (TextView)findViewById(R.id.txtPlayer);
        txtTime = (TextView)findViewById((R.id.txtTime));

        if(result.equals("Lose")) {
            endLayout.setBackgroundResource(R.drawable.lose);
            txtRes.setText("");
        }
        else {
            endLayout.setBackgroundResource(R.drawable.winer);
            txtTime.setText(time);
        }
    }
}
