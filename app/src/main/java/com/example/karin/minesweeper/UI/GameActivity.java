package com.example.karin.minesweeper.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.karin.minesweeper.R;
import com.example.karin.minesweeper.logic.GameLogic;

import java.util.Timer;

public class GameActivity extends AppCompatActivity implements MyButtonListener{

    public final static String RESULT = "SCORE";
    private GameLogic gameLogic;
    private Timer timer;
    private int Rows, Cols, Mines;
    private RelativeLayout rl;
    private GridLayout grid;
    //private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);

        rl = (RelativeLayout)findViewById(R.id.activity_game);

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
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(125,125); // 60 is height you can set it as u need
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
            if(myButton.isEnabled())
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
            Toast.makeText(this, "Well Done!!", Toast.LENGTH_LONG).show();

            intent = new Intent(this,EndGameActivity.class);
            intent.putExtra(RESULT,"Win");
            startActivity(intent);
        }

    }

    @Override
    public void buttonLongClick(MyButton myButton)
    {
        if(myButton.getBackground().equals(R.drawable.flag))
            myButton.setBackgroundResource(R.drawable.mine);
        else
            myButton.setBackgroundResource(R.drawable.flag);
    }
}
