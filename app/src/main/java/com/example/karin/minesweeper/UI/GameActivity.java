package com.example.karin.minesweeper.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karin.minesweeper.R;
import com.example.karin.minesweeper.logic.GameLogic;

public class GameActivity extends AppCompatActivity implements MyButtonListener{

    public final static String RESULT = "SCORE";
    public final static String TIMER = "TIME";
    private GameLogic gameLogic;
    private int Rows, Cols, Mines;
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

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

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
                //gridbtnH = ;
                //gridbtnW = grid.getWidth();
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(45,45); // 60 is height you can set it as u need
                btn.setLayoutParams(lp);
                btn.setListener(this);
                btn.setText(" ");
                btn.setTextSize(9);
                btn.setBackgroundResource(R.drawable.mine);

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
        Intent intent;

        if(gameLogic.CheckMine(curRow,curCol))
        {
            intent = new Intent(this,EndGameActivity.class);
            intent.putExtra(RESULT,"Lose");
            startActivity(intent);
        }
        else
        {
            if(myButton.isEnabled() && myButton.isClickable())
            {
                num = gameLogic.updateCell(curRow, curCol);
                if (num == 0) {
                    myButton.setEnabled(false);
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
                } else
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

            //Toast.makeText(this, "Well Done!!", Toast.LENGTH_LONG).show();


            intent = new Intent(this,EndGameActivity.class);
            intent.putExtra(TIMER,timerTextView.getText());
            intent.putExtra(RESULT,"Win");
            startActivity(intent);
        }
    }

    @Override
    public void buttonLongClick(MyButton myButton)
    {

        if(myButton.getText().equals("flag"))
        {
            myButton.setText(" ");
            myButton.setBackgroundResource(R.drawable.mine);
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
}
