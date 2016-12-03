package com.example.karin.minesweeper.UI;
import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 * Created by Karin on 01/12/2016.
 */

public class MyButton extends Button implements View.OnClickListener, View.OnLongClickListener
{
    private int row;
    private int col;
    private MyButtonListener listener;

    public MyButton(Context context,int row, int col)
    {
        super(context);
        this.row = row;
        this.col = col;
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (listener != null)
            listener.buttonClick(this);
    }

    @Override
    public boolean onLongClick(View v)
    {
        if (listener != null) {
            listener.buttonLongClick(this);
            return true;
        }
        return false;
    }

    public int getRow()
    {return this.row;}

    public int getCol()
    {return this.col;}

    public void setRow(int row)
    {this.row = row;}

    public void setCol(int col)
    {this.col = col;}

    public void setListener(MyButtonListener listener)
    {this.listener = listener;}

}
