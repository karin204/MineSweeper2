package com.example.karin.minesweeper.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karin.minesweeper.R;
import com.example.karin.minesweeper.logic.GameLogic;

public class GameActivity extends AppCompatActivity implements MyButtonListener{

    public final static String DETAILS = "LEVEL";
    public final static String RESULT = "SCORE";
    public final static String TIMER = "TIME";
    private GameLogic gameLogic;
    private int Rows, Cols, Mines;
    private String Level;
    private RelativeLayout rl;
    private GridLayout grid;
    private TextView timerTextView;
    long startTime = 0;

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

        /*
        String [] players = new String[3];
        players[0] = "a";
        players[1] = "b";
        players[2] = "c";

        SharedPreferences.Editor scoresEditor = getSharedPreferences("scores", MODE_PRIVATE).edit();
        scoresEditor.putString("p1", players[0]);
        scoresEditor.apply();

        SharedPreferences scorePref = getSharedPreferences("scores", MODE_PRIVATE);
        String name = scorePref.getString("p1",null);
        */


        rl = (RelativeLayout)findViewById(R.id.activity_game);

        //Timer section
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        Rows = getIntent().getIntExtra("ROWS",0);
        Cols = getIntent().getIntExtra("COLS",0);
        Mines = getIntent().getIntExtra("MINES",0);
        Level = getIntent().getStringExtra(DETAILS);

        grid = (GridLayout)findViewById(R.id.board);
        grid.setColumnCount(Cols);
        grid.setRowCount(Rows);
        grid.setId(0);
        grid.setBackgroundColor(Color.DKGRAY);
        gameLogic = new GameLogic(Rows,Cols,Mines);
        for(int i=0; i< Rows; i++)
        {
            for(int j = 0; j< Cols; j++)
            {
                MyButton btn = new MyButton(this,i,j);
                int width = getWindowManager().getDefaultDisplay().getWidth();
                int height = getWindowManager().getDefaultDisplay().getHeight();
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width/(Rows+1),width/(Rows+1));
                btn.setLayoutParams(lp);
                btn.setListener(this);
                btn.setText(" ");
                btn.setTextSize(9);
                btn.setBackgroundResource(R.drawable.box);

                grid.addView(btn);
            }
        }


    }

    @Override
    public void buttonClick(MyButton myButton)
    {
        int num;
        int curRow = myButton.getRow();
        int curCol = myButton.getCol();
        final Intent intent;

        if(gameLogic.CheckMine(curRow,curCol))
        {
            Toast.makeText(this, "You Lost!!", Toast.LENGTH_LONG).show();
            int [] mines = new int[Mines];
            mines = gameLogic.getMinePos();
            for(int i = 0; i< Mines; i++)
                grid.getChildAt(mines[i]).setBackgroundResource(R.drawable.mine);
            myButton.setBackgroundResource(R.drawable.mine_clicked);

            intent = new Intent(this,EndGameActivity.class);
            intent.putExtra(RESULT,"Lose");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){

                    startActivity(intent);
                }
            }, 3000);
        }
        else
        {
            if(myButton.isEnabled() && myButton.isClickable())
            {
                myButton.setEnabled(false);
                num = gameLogic.updateCell(curRow, curCol);
                if (num == 0)
                {
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
                }
                else
                {
                    myButton.setBackgroundResource(R.drawable.box_clicked);
                    myButton.setText(num + "");
                    myButton.setTextColor(Color.RED);
                }
            }
        }

        if(gameLogic.checkWin())
        {
            timerHandler.removeCallbacks(timerRunnable);
            Toast.makeText(this, "Well Done!!", Toast.LENGTH_LONG).show();
            final Intent intent1 = new Intent(this,EndGameActivity.class);

            CharSequence score = timerTextView.getText();

            SharedPreferences.Editor scoresEditor = getSharedPreferences("scores", MODE_PRIVATE).edit();
            SharedPreferences scores = getSharedPreferences("scores", MODE_PRIVATE);
            if(scores.contains(Level))
            {
                String oldScore = scores.getString(Level,null);
                int l = oldScore.length()-5;
                int newScorePos = 0;
                for(int i = l; i < oldScore.length(); i++)
                {
                    if(oldScore.charAt(i) < score.charAt(newScorePos))
                        break;
                    else if(oldScore.charAt(i) > score.charAt(newScorePos))
                    {
                        Intent intent2 = new Intent(this,NewHighScoreActivity.class);
                        intent2.putExtra("SCORE",score);
                        intent2.putExtra("LEVEL",Level);
                        startActivity(intent2);
                    }
                }
            }
            else
            {
                final Intent intent2 = new Intent(this,NewHighScoreActivity.class);
                intent2.putExtra("SCORE",score);
                intent2.putExtra("LEVEL",Level);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run(){

                        startActivity(intent2);
                    }
                }, 3000);

            }
            /*
            intent1.putExtra(TIMER,timerTextView.getText());
            intent1.putExtra(RESULT,"Win");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){

                    //startActivity(intent1);
                }
            }, 3000);*/

        }
    }

    @Override
    public void buttonLongClick(MyButton myButton)
    {

        if(myButton.getText().equals("flag"))
        {
            myButton.setText(" ");
            myButton.setBackgroundResource(R.drawable.box);
            myButton.setClickable(true);
        }
        else
        {
            myButton.setBackgroundResource(R.drawable.flag);
            myButton.setText("flag");
            myButton.setClickable(false);
            myButton.setTextColor(Color.TRANSPARENT);
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
        Intent intent = new Intent(GameActivity.this,StartPageActivity.class);
        startActivity(intent);
        finish();

    }
}
