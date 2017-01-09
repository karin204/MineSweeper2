package com.example.karin.minesweeper.UI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.example.karin.minesweeper.logic.DbSingleton;


public class StartPageActivity extends AppCompatActivity implements OnClickListener{
    public final static String DETAILS = "LEVEL";
    public final static String LP = "Last played";
    private Button btnEasy;
    private Button btnMedium;
    private Button btnHard;
    private Button btnHs;
    private TextView resEasy;
    private TextView resMedium;
    private TextView resHard;
    private DbSingleton dbs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start_page);

        dbs = DbSingleton.getInstance(this);
        btnEasy = (Button)findViewById(R.id.btnEasy);
        btnMedium = (Button)findViewById(R.id.btnMedium);
        btnHard = (Button)findViewById(R.id.btnHard);
        btnHs = (Button)findViewById(R.id.hs);
        resEasy = (TextView)findViewById(R.id.timeEasy);
        resMedium = (TextView)findViewById(R.id.timeMed);
        resHard = (TextView)findViewById(R.id.timeHard);
        btnEasy.setOnClickListener(this);
        btnMedium.setOnClickListener(this);
        btnHard.setOnClickListener(this);
        btnHs.setOnClickListener(this);
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

            case R.id.hs:
            {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                HighScoresFragment f = new HighScoresFragment();
                fragmentTransaction.replace(R.id.activity_start_page, f);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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

    /*@Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }*/

    public void loadData()
    {
        SharedPreferences scores = getSharedPreferences("scores", MODE_PRIVATE);
        if(scores.contains("Easy")) {
            resEasy.setText(scores.getString("Easy",null));}
        if(scores.contains("Medium")) {
            resMedium.setText(scores.getString("Medium",null));}
        if(scores.contains("Hard")) {
            resHard.setText(scores.getString("Hard",null));}
        if(scores.contains("LP"))
        {
            String lp = scores.getString("LP",null);
            if(lp.equals("Easy"))
                btnEasy.setText(LP);
            else if(lp.equals("Medium"))
                btnMedium.setText(LP);
            else
                btnHard.setText(LP);
        }
    }
}
