package com.example.karin.minesweeper.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.karin.minesweeper.R;


public class StartPageActivity extends AppCompatActivity implements OnClickListener{
    public final static String DETAILS = "LEVEL";
    private Button btnEasy;
    private Button btnMedium;
    private Button btnHard;
    private TextView resEasy;
    private TextView resMedium;
    private TextView resHard;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start_page);

        btnEasy = (Button)findViewById(R.id.btnEasy);
        btnMedium = (Button)findViewById(R.id.btnMedium);
        btnHard = (Button)findViewById(R.id.btnHard);
        resEasy = (TextView)findViewById(R.id.timeEasy);
        resMedium = (TextView)findViewById(R.id.timeMed);
        resHard = (TextView)findViewById(R.id.timeHard);
        btnEasy.setOnClickListener(this);
        btnMedium.setOnClickListener(this);
        btnHard.setOnClickListener(this);
        loadData();

    }

    @Override
    public void onClick(View v)
    {
        Intent intent;
        int id = v.getId();

        switch(id)
        {
            case R.id.btnEasy:
            {
                intent = new Intent(this,GameActivity.class);
                intent.putExtra("COLS", 10);
                intent.putExtra("ROWS", 10);
                intent.putExtra("MINES", 5);
                intent.putExtra(DETAILS,"Easy");
                startActivity(intent);
                finish();
                break;
            }

            case R.id.btnMedium:
            {
                intent = new Intent(this,GameActivity.class);
                intent.putExtra("COLS", 10);
                intent.putExtra("ROWS", 10);
                intent.putExtra("MINES", 10);
                intent.putExtra(DETAILS,"Medium");
                startActivity(intent);
                finish();
                break;
            }

            case R.id.btnHard:
            {
                intent = new Intent(this,GameActivity.class);
                intent.putExtra("COLS", 5);
                intent.putExtra("ROWS", 5);
                intent.putExtra("MINES", 10);
                intent.putExtra(DETAILS,"Hard");
                startActivity(intent);
                finish();
                break;
            }

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);

    }

    public void loadData()
    {
        SharedPreferences scores = getSharedPreferences("scores", MODE_PRIVATE);
        if(scores.contains("Easy")) {
            resEasy.setText(scores.getString("Easy",null));}
        if(scores.contains("Medium")) {
            resMedium.setText(scores.getString("Medium",null));}
        if(scores.contains("Hard")) {
            resHard.setText(scores.getString("Hard",null));}
    }
}
