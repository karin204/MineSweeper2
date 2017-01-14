package com.example.karin.minesweeper.UI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.example.karin.minesweeper.R;
import com.example.karin.minesweeper.logic.DbSingleton;

import pl.droidsonroids.gif.GifImageView;


public class StartPageActivity extends AppCompatActivity implements OnClickListener{
    private final String TAG = StartPageActivity.class.getSimpleName();
    public final static String DETAILS = "LEVEL";
    public final static String LP = "Last played";
    private Button btnEasy;
    private Button btnMedium;
    private Button btnHard;
    private Button btnHs;
    private DbSingleton dbs;
    private GifImageView start;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start_page);

        start = (GifImageView)findViewById(R.id.ivopen);


        dbs = DbSingleton.getInstance(this);
        btnEasy = (Button)findViewById(R.id.btnEasy);
        btnMedium = (Button)findViewById(R.id.btnMedium);
        btnHard = (Button)findViewById(R.id.btnHard);
        btnHs = (Button)findViewById(R.id.hs);

        if(dbs.isFirstRun())
        {
            startAnimation();
        }

        btnEasy.setOnClickListener(this);
        btnMedium.setOnClickListener(this);
        btnHard.setOnClickListener(this);
        btnHs.setOnClickListener(this);
    }

    private void startAnimation()
    {
        btnHs.setVisibility(View.INVISIBLE);
        btnEasy.setVisibility(View.INVISIBLE);
        btnMedium.setVisibility(View.INVISIBLE);
        btnHard.setVisibility(View.INVISIBLE);
        start.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);

        MediaPlayer mp = MediaPlayer.create(this, R.raw.hello);

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                Display display = getWindowManager().getDefaultDisplay();
                float width = display.getWidth();
                TranslateAnimation animation = new TranslateAnimation(0, width - 50, 0, 0); // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
                animation.setDuration(1000); // animation duration
                animation.setRepeatCount(0); // animation repeat count
                animation.setRepeatMode(0); // repeat animation (left to right, right to


                start.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        start.setVisibility(View.INVISIBLE);
                        btnHs.setVisibility(View.VISIBLE);
                        btnEasy.setVisibility(View.VISIBLE);
                        btnMedium.setVisibility(View.VISIBLE);
                        btnHard.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation){}
                });
            }
        });
        mp.start();
        dbs.setFirstRun(false);
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
                //finish();
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
                //finish();
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
                //finish();
                break;
            }

            case R.id.hs:
            {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                HighScoresFragment f = new HighScoresFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("ActivityId", R.id.activity_start_page);
                f.setArguments(bundle);
                fragmentTransaction.replace(R.id.activity_start_page, f);
                fragmentTransaction.addToBackStack("score");
                fragmentTransaction.commit();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() != 0)
            getFragmentManager().popBackStack();
        else
             finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(dbs.isChanged()) {
            dbs.updateDB();
            Log.d(TAG, "DB saved!!!!!!!!!!!!!!!!!!!!!!");
            System.exit(0);
        }
    }
}
