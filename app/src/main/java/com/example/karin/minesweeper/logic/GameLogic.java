package com.example.karin.minesweeper.logic;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Karin on 01/12/2016.
 */

public class GameLogic
{
    private int rows;
    private int cols;
    private int mines;
    private int counter;
    private int[][] gameBoard;
    private ArrayList<Integer> minePos;

    public GameLogic(int rows, int cols,int mines)
    {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.counter = rows*cols-mines;
        this.gameBoard = new int[rows][cols];
        this.minePos = new ArrayList<>();
        this.buildBoard();
    }


    public void buildBoard()
    {
        Random rand = new Random();
        for(int idx = 0; idx < mines; idx++)
        {
            int x = rand.nextInt(rows);
            int y = rand.nextInt(cols);
            if(gameBoard[x][y] == -1)
                idx--;
            else
            {
                gameBoard[x][y] = -1;
                minePos.add(x*this.rows+y);
            }
        }
        for(int i=0 ;i<rows; i++)
            for(int j=0;j<cols; j++)
                if(gameBoard[i][j] != -1)
                    gameBoard[i][j]= CalculateNeighbours(i,j);

    }

    public ArrayList<Point> addMine()
    {
        ArrayList<Point> updatedCells = new ArrayList<>();
        int temp = 0;
        Random rand = new Random();
        int x = rand.nextInt(rows);
        int y = rand.nextInt(cols);

        while(gameBoard[x][y] == -1) {
            x = rand.nextInt(rows);
            y = rand.nextInt(cols);
        }
        gameBoard[x][y] = -1;
        updatedCells.add(new Point(x,y));
        minePos.add(x*this.rows+y);
        this.mines++;

        for(int i=0 ;i<rows; i++)
            for(int j=0;j<cols; j++)
                if(gameBoard[i][j] != -1) {
                    temp = CalculateNeighbours(i, j);
                    if(gameBoard[i][j] != temp) {
                        updatedCells.add(new Point(i,j));
                        gameBoard[i][j] = temp;
                    }
                }
        return updatedCells;
    }


    public boolean CheckMine(int x, int y)
    {
        if(this.gameBoard[x][y] == -1)
            return true;
        else
            return false;
    }

    public int updateCell(int x, int y)
    {
        this.counter--;
        return gameBoard[x][y];
    }

    public int CalculateNeighbours(int row, int col)
    {
        int countNeigh = 0;

        if(row-1 >= 0) //not the first row
        {
            if (gameBoard[row - 1][col] == -1)
                countNeigh++;
            if (col - 1 >= 0)
                if (gameBoard[row - 1][col - 1] == -1)
                    countNeigh++;
            if (col+1 <cols)
                if(gameBoard[row-1][col+1] == -1)
                    countNeigh++;
        }

        if(row+1 <rows) // not the last row
        {
            if(gameBoard[row+1][col] == -1)
                countNeigh++;
            if(col-1 >= 0)
                if(gameBoard[row+1][col-1] == -1)
                    countNeigh++;
            if(col+1 < cols)
                if(gameBoard[row+1][col+1] == -1)
                    countNeigh++;
        }

       if(col-1 >= 0) //not the first col
           if(gameBoard[row][col-1] == -1)
               countNeigh++;

        if(col+1 <cols) //not the last col
            if(gameBoard[row][col+1] == -1)
                countNeigh++;

        return countNeigh;

    }

    public boolean checkWin()
    {
        if(counter ==0)
            return true;

        return false;
    }

    public int[][] getGameBoard()
    {return this.gameBoard;}

    public int getRows()
    {return this.rows;}

    public int getCols()
    {return this.cols;}

    public int getMinesCount()
    {return this.mines;}

    public ArrayList<Integer> getMinePos()
    {   return minePos; }
}
