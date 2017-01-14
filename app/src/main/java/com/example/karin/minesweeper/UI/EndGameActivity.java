package com.example.karin.minesweeper.UI;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.karin.minesweeper.R;

import pl.droidsonroids.gif.GifImageView;

public class EndGameActivity extends AppCompatActivity {

    private final String TAG = EndGameActivity.class.getSimpleName();

    private String result;
    private String time;
    private ExplosionImageView ex;
    private ImageView img;

    TextView txtRes;
    TextView txtTime;
    RelativeLayout endLayout;
    Intent startIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_end_game);
        Intent intent = getIntent();
        startIntent = new Intent(EndGameActivity.this, StartPageActivity.class);
        result = intent.getStringExtra("LEVEL");
        time = intent.getStringExtra("TIMER");
        endLayout = (RelativeLayout) findViewById(R.id.activity_end_game);

        txtRes = (TextView) findViewById(R.id.txtPlayer);
        txtTime = (TextView) findViewById((R.id.txtTime));

        if (result.equals("Lose")) {
            img = (GifImageView) findViewById(R.id.ivlose);


            ex = ExplosionImageView.attach2Window(this);
            MediaPlayer mp = MediaPlayer.create(this, R.raw.laugh);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {

                    MediaPlayer mp2 = MediaPlayer.create(EndGameActivity.this, R.raw.bomb);
                    mp2.start();
                    addListener(img);
                }

            });

            mp.start();

            txtRes.setText("");
        } else {
            //endLayout.setBackgroundResource(R.drawable.winer);
            txtTime.setText(time);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(startIntent);
                finish();
            }
        }, 5000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {

    }

    private void addListener(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                addListener(parent.getChildAt(i));
            }
        } else {

                    ex.explode(root);


        }

    }







}
