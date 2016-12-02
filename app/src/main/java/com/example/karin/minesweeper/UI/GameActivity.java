package com.example.karin.minesweeper.UI;
import com.example.karin.minesweeper.R;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import com.example.karin.minesweeper.logic.GameLogic;

import java.util.Timer;

public class GameActivity extends AppCompatActivity implements MyButtonListener{

    public final static String RESULT = "SCORE";
    private GameLogic gameLogic;
    private Timer timer;
    private String level;
    private GridLayout board;
    private int btnX;
    private int btnY;
    private MyButton[][] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        level = intent.getStringExtra(StartPageActivity.DETAILS);
        board = (GridLayout)findViewById(R.id.board);

        switch (level)
        {
            case "Easy":
            {
                btnX = 10;
                btnY = 10;
                buttons = new MyButton[btnX][btnY];
                gameLogic = new GameLogic(btnX,btnY,5);
                board.setRowCount(btnX);
                board.setColumnCount(btnY);
                gameLogic.buildBoard();

                for(int xIdx=0; xIdx<btnX; xIdx++)
                    for(int yIdx=0; yIdx<btnY; yIdx++)
                    {

                        buttons[xIdx][yIdx] = new MyButton(this,xIdx,yIdx);
                        board.addView(buttons[xIdx][yIdx]);

                    }



                break;
            }
            case "Medium":
            {
                btnX = 10;
                btnY = 10;
                buttons = new MyButton[btnX][btnY];
                gameLogic = new GameLogic(btnX,btnY,10);
                board.setRowCount(btnX);
                board.setColumnCount(btnY);
                gameLogic.buildBoard();
                break;
            }
            case "Hard":
            {
                btnX = 5;
                btnY = 5;
                buttons = new MyButton[btnX][btnY];
                gameLogic = new GameLogic(btnX,btnY,10);
                board.setRowCount(btnX);
                board.setColumnCount(btnY);
                gameLogic.buildBoard();
                break;
            }
        }

    }

    @Override
    public void buttonClick(MyButton myButton)
    {
        int curRow = myButton.getRow();
        int curCol = myButton.getCol();
        Intent intent;

        if(gameLogic.getGameBoard()[curRow][curCol] == -1)
        {
            intent = new Intent(this,GameActivity.class);
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
