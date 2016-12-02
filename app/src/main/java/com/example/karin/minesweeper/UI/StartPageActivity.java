package com.example.karin.minesweeper.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import com.example.karin.minesweeper.R;


public class StartPageActivity extends AppCompatActivity implements OnClickListener{
    public final static String DETAILS = "LEVEL";
    private Button btnEasy;
    private Button btnMedium;
    private Button btnHard;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        btnEasy = (Button)findViewById(R.id.btnEasy);
        btnMedium = (Button)findViewById(R.id.btnMedium);
        btnHard = (Button)findViewById(R.id.btnHard);
        btnEasy.setOnClickListener(this);
        btnMedium.setOnClickListener(this);
        btnHard.setOnClickListener(this);
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
                intent.putExtra(DETAILS,"Easy");
                startActivity(intent);
                break;
            }

            case R.id.btnMedium:
            {
                intent = new Intent(this,GameActivity.class);
                intent.putExtra(DETAILS,"Medium");
                startActivity(intent);
                break;
            }

            case R.id.btnHard:
            {
                intent = new Intent(this,GameActivity.class);
                intent.putExtra(DETAILS,"Hard");
                startActivity(intent);
                break;
            }

        }
    }
}
