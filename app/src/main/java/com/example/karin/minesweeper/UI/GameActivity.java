package com.example.karin.minesweeper.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.karin.minesweeper.R;
import com.example.karin.minesweeper.logic.GameLogic;

import java.util.Timer;

public class GameActivity extends AppCompatActivity implements MyButtonListener{

    public final static String RESULT = "SCORE";
    private GameLogic gameLogic;
    private Timer timer;
    private int Rows, Cols, Mines;
    private RelativeLayout rl;
    //private String level;
    //private GridLayout board;
    //private int btnX;
   // private int btnY;
    //private MyButton[][] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);

        rl = (RelativeLayout)findViewById(R.id.activity_game);

        Rows = getIntent().getIntExtra("ROWS",0);
        Cols = getIntent().getIntExtra("COLS",0);
        Mines = getIntent().getIntExtra("MINES",0);

        GridLayout grid = (GridLayout)findViewById(R.id.board);
        grid.setColumnCount(Cols);
        grid.setRowCount(Rows);
        grid.setId(0);

        gameLogic = new GameLogic(Rows,Cols,Mines);
        for(int i=0; i< Rows; i++)
        {
            for(int j = 0; j< Cols; j++)
            {
                MyButton btn = new MyButton(this,i,j);
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(120,120); // 60 is height you can set it as u need
                btn.setLayoutParams(lp);
                btn.setListener(this);
                grid.addView(btn);
            }
        }
    }

    @Override
    public void buttonClick(MyButton myButton)
    {
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
            if(gameLogic.getGameBoard()[curRow][curCol] == 0)
                myButton.setBackgroundColor(Color.GRAY);
            else
                myButton.setText(String.valueOf(gameLogic.getGameBoard()[curRow][curCol]));


        }
    }

    @Override
    public void buttonLongClick(MyButton myButton)
    {
        if(myButton.getBackground().equals(R.drawable.flag))
            myButton.setBackgroundColor(Color.GRAY);
        else
            myButton.setBackgroundResource(R.drawable.flag);
    }
}
