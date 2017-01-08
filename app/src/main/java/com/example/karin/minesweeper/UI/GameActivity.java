package com.example.karin.minesweeper.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karin.minesweeper.R;
import com.example.karin.minesweeper.logic.GameLogic;
import com.example.karin.minesweeper.service.OrientationService;
import com.example.karin.minesweeper.service.OrientationServiceMock;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements MyButtonListener {

    public final static String DETAILS = "LEVEL";
    public final static String LP = "LP";
    private GameLogic gameLogic;
    private int Rows, Cols, Mines;
    private String Level;
    private GridLayout grid;
    private TextView timerTextView;
    long startTime = 0;
    Button helpButton;
    private boolean endGame = false;
    private MyButton[][] tiles;

    private final String TAG = GameActivity.class.getSimpleName();
    private boolean isServiceBound = false;
    public OrientationService.OrientationServiceBinder binder;
    private static final boolean SHOULD_USE_MOCK = false;


    //Timer
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%02d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);

        //Timer section
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        helpButton = (Button) findViewById(R.id.button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(GameActivity.this);
                dialog.setContentView(R.layout.helpdialog);
                dialog.show();

            }
        });

        Rows = getIntent().getIntExtra("ROWS", 0);
        Cols = getIntent().getIntExtra("COLS", 0);
        Mines = getIntent().getIntExtra("MINES", 0);
        Level = getIntent().getStringExtra(DETAILS);

        grid = (GridLayout) findViewById(R.id.board);
        grid.setColumnCount(Cols);
        grid.setRowCount(Rows);
        grid.setId(0);
        grid.setBackgroundColor(Color.DKGRAY);
        gameLogic = new GameLogic(Rows, Cols, Mines);
        tiles = new MyButton[Rows][Cols];

        for (int i = 0; i < Rows; i++) {
            for (int j = 0; j < Cols; j++) {

                tiles[i][j] = new MyButton(this, i, j);
                int width = getWindowManager().getDefaultDisplay().getWidth();
                int height = getWindowManager().getDefaultDisplay().getHeight();
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width / (Rows + 1), width / (Rows + 1));
                tiles[i][j].setLayoutParams(lp);
                tiles[i][j].setListener(this);
                tiles[i][j].setText(" ");
                tiles[i][j].setTextSize(9);
                tiles[i][j].setBackgroundResource(R.drawable.box);

                grid.addView(tiles[i][j]);
            }
        }

        if (SHOULD_USE_MOCK) {
            bindService(new Intent(this, OrientationServiceMock.class), sensorsBoundServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            bindService(new Intent(this, OrientationService.class), sensorsBoundServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: activity destroyed");
        unbindService(sensorsBoundServiceConnection);
    }


    @Override
    public void onPause() {
        super.onPause();
        startTime = System.currentTimeMillis();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void buttonClick(MyButton myButton) {
        int curRow = myButton.getRow();
        int curCol = myButton.getCol();

        //step on mine
        if (myButton.isClickable() && gameLogic.CheckMine(curRow, curCol))
            loose(myButton);
            //step on no mine
        else if (myButton.isEnabled() && myButton.isClickable())
            noMineClick(myButton, curRow, curCol);

        if (gameLogic.checkWin()) {
            timerHandler.removeCallbacks(timerRunnable);
            endGame = true;
            Toast.makeText(this, "Well Done!!", Toast.LENGTH_SHORT).show();
            final Intent intent1 = new Intent(this, EndGameActivity.class);
            final CharSequence score = timerTextView.getText();
            SharedPreferences.Editor scoresEditor = getSharedPreferences("scores", MODE_PRIVATE).edit();
            SharedPreferences scores = getSharedPreferences("scores", MODE_PRIVATE);

            //there is an high score -> need to check if the new score is better
            if (scores.contains(Level))
                checkHigherScore(scores, score);
                //no previous high score
            else
                newHighScore(score);
        }
    }

    @Override
    public void buttonLongClick(MyButton myButton) {
        if (myButton.getText().equals("flag")) {
            myButton.setText(" ");
            myButton.setBackgroundResource(R.drawable.box);
            myButton.setClickable(true);
        } else {
            myButton.setBackgroundResource(R.drawable.flag);
            myButton.setText("flag");
            myButton.setClickable(false);
            myButton.setTextColor(Color.TRANSPARENT);
        }
    }

    public void disableButtons(GridLayout layout) {
        // Get all touchable views
        ArrayList<View> layoutButtons = layout.getTouchables();

        // loop through them, if they are an instance of Button, disable it.
        for (View v : layoutButtons) {
            if (v instanceof Button) {
                ((Button) v).setEnabled(false);
            }
        }
    }

    public void loose(MyButton myButton) {
        timerHandler.removeCallbacks(timerRunnable);
        endGame = true;
        boolean finish = false;

        for (int i = 0; i < Rows; i++)
            for (int j = 0; j < Cols; j++)
                tilesAnimation(tiles[i][j]);



        Toast.makeText(this, "You Lost!!", Toast.LENGTH_SHORT).show();
        int[] mines = new int[Mines];
        mines = gameLogic.getMinePos();
        for (int i = 0; i < Mines; i++)
            grid.getChildAt(mines[i]).setBackgroundResource(R.drawable.mine);
        myButton.setBackgroundResource(R.drawable.mine_clicked);
        disableButtons(grid);



        SharedPreferences.Editor scoresEditor = getSharedPreferences("scores", MODE_PRIVATE).edit();
        scoresEditor.putString(LP, Level);
        scoresEditor.apply();

        final Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra(DETAILS, "Lose");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    public void tilesAnimation(final MyButton currentTile) {
        long duration = 4000;
        Random random = new Random();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int otherSide;

        otherSide = random.nextBoolean() ? 1 : -1;
        ObjectAnimator flyOutX = ObjectAnimator.ofFloat(currentTile, "x", currentTile.getX(), otherSide * size.x);
        flyOutX.setDuration(duration);
        flyOutX.setInterpolator(new DecelerateInterpolator());

        otherSide = random.nextBoolean() ? 1 : -1;
        ObjectAnimator flyOutY = ObjectAnimator.ofFloat(currentTile, "y", currentTile.getY(), size.y);
        flyOutY.setDuration(duration);
        flyOutY.setInterpolator(new DecelerateInterpolator());

        otherSide = random.nextBoolean() ? 1 : -1;
        ObjectAnimator rotate = ObjectAnimator.ofFloat(currentTile, "rotation", otherSide == 1 ? 0f : 360f, otherSide == 1 ? 360f : 0f);
        rotate.setDuration(duration);
        rotate.setInterpolator(new DecelerateInterpolator());


        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            Random random = new Random();

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentTile.setTranslationX((random.nextFloat() - 0.5f) * currentTile.getWidth() * 0.05f);
                currentTile.setTranslationY((random.nextFloat() - 0.5f) * currentTile.getHeight() * 0.05f);

            }
        });

        //tiles disappear
        currentTile.animate().setDuration(150).setStartDelay(1000).scaleX(0f).scaleY(0f).alpha(0f).start();


        AnimatorSet animationSet1 = new AnimatorSet();
        AnimatorSet animationSet2 = new AnimatorSet();
        animationSet1.playTogether(flyOutY, flyOutX);
        animationSet2.playSequentially(animator, animationSet1);
        animationSet2.start();

    }


    public void noMineClick(MyButton myButton, int curRow, int curCol) {
        myButton.setEnabled(false);
        int num = gameLogic.updateCell(curRow, curCol);
        if (num == 0) {
            myButton.setBackgroundResource(R.drawable.box_clicked);
            if ((curRow - 1) >= 0)
                buttonClick((MyButton) grid.getChildAt((curRow - 1) * Rows + curCol));
            if ((curRow + 1) <= Rows - 1)
                buttonClick((MyButton) grid.getChildAt((curRow + 1) * Rows + curCol));
            if ((curCol - 1) >= 0)
                buttonClick((MyButton) grid.getChildAt(curRow * Rows + curCol - 1));
            if ((curCol + 1) <= Cols - 1)
                buttonClick((MyButton) grid.getChildAt(curRow * Rows + curCol + 1));
            if ((curCol + 1) <= Cols - 1 && (curRow + 1) <= Rows - 1)
                buttonClick((MyButton) grid.getChildAt((curRow + 1) * Rows + curCol + 1));
            if ((curCol + 1) <= Cols - 1 && (curRow - 1) >= 0)
                buttonClick((MyButton) grid.getChildAt((curRow - 1) * Rows + curCol + 1));
            if ((curCol - 1) >= 0 && (curRow - 1) >= 0)
                buttonClick((MyButton) grid.getChildAt((curRow - 1) * Rows + curCol - 1));
            if ((curCol - 1) >= 0 && (curRow + 1) <= Rows - 1)
                buttonClick((MyButton) grid.getChildAt((curRow + 1) * Rows + curCol - 1));
        } else {
            myButton.setBackgroundResource(R.drawable.box_clicked);
            myButton.setText(num + "");
            myButton.setTextColor(Color.RED);
        }
    }

    public void checkHigherScore(SharedPreferences scores, CharSequence score) {
        String oldScore = scores.getString(Level, null);
        int l = oldScore.length() - 5;
        int newScorePos = 0;
        for (int i = l; i < oldScore.length(); i++, newScorePos++) {
            //No new high score
            if (oldScore.charAt(i) < score.charAt(newScorePos)) {
                final Intent intent2 = new Intent(this, EndGameActivity.class);
                SharedPreferences.Editor scoresEditor = getSharedPreferences("scores", MODE_PRIVATE).edit();
                scoresEditor.putString(LP, Level);
                scoresEditor.apply();

                intent2.putExtra(DETAILS, "win");
                intent2.putExtra("TIMER", score);

                disableButtons(grid);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent2);
                        finish();
                    }
                }, 3000);
                break;
            }
            //New high score
            else if (oldScore.charAt(i) > score.charAt(newScorePos)) {
                newHighScore(score);
                break;
            }
        }
    }

    public void newHighScore(final CharSequence score) {
        final Intent intent = new Intent(this, StartPageActivity.class);
        final Dialog dialog = new Dialog(GameActivity.this);
        dialog.setTitle("New High Score!");
        dialog.setContentView(R.layout.popup);

        //cancel back operation and outside click
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        final EditText editText = (EditText) dialog.findViewById(R.id.name);
        TextView s = (TextView) dialog.findViewById(R.id.score);
        s.setText(score.toString());
        Button submit = (Button) dialog.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                if (name.isEmpty())
                    name = "Anonymous";
                name = name + " - " + score;
                SharedPreferences.Editor scoresEditor = getSharedPreferences("scores", MODE_PRIVATE).edit();
                scoresEditor.putString(Level, name);
                scoresEditor.putString(LP, Level);
                scoresEditor.apply();
                startActivity(intent);
                finish();
            }
        });

        disableButtons(grid);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        }, 3000);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
            onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!endGame) {
            Intent intent = new Intent(GameActivity.this, StartPageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private ServiceConnection sensorsBoundServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (OrientationService.OrientationServiceBinder) service;
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isServiceBound = false;
        }


        void notifyBoundService(String massageFromActivity) {
            if (isServiceBound) {
                binder.notifyService(massageFromActivity);
            }
        }

        ;

    };
}

