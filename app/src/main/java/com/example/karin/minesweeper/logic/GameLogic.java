package com.example.karin.minesweeper.logic;

import java.util.Random;

/**
 * Created by Karin on 01/12/2016.
 */

public class GameLogic
{
    private int rows;
    private int cols;
    private int mines;

    private int[][] gameBoard;

    public GameLogic(int rows, int cols,int mines)
    {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.gameBoard = new int[rows][cols];
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
                gameBoard[x][y] = -1;
        }
        for(int i=0 ;i<rows; i++)
            for(int j=0;j<cols; j++)
                if(gameBoard[i][j] != -1)
                    gameBoard[i][j]= CalculateNeighbours(i,j);

    }

    public boolean CheckMine(int x, int y)
    {
        if(this.gameBoard[x][y] == -1)
            return true;
        else
            return false;
    }

    public void updateCell()
    {

    }

    public int CalculateNeighbours(int row, int col)
    {
        int countNeigh = 0;

        if(row-1 >= 0) //not the first row
        {
            if (gameBoard[col][row - 1] == -1)
                countNeigh++;
            if (col - 1 >= 0)
                if (gameBoard[col - 1][row - 1] == -1)
                    countNeigh++;
            if (col+1 <cols)
                if(gameBoard[col+1][row-1] == -1)
                    countNeigh++;
        }

        if(row+1 <rows) // not the last row
        {
            if(gameBoard[col][row+1] == -1)
                countNeigh++;
            if(col-1 >= 0)
                if(gameBoard[col-1][row+1] == -1)
                    countNeigh++;
            if(col+1 < cols)
                if(gameBoard[col+1][row+1] == -1)
                    countNeigh++;
        }

       if(col-1 >= 0) //not the first col
           if(gameBoard[col-1][row] == -1)
               countNeigh++;

        if(col+1 <cols) //not the last col
            if(gameBoard[col+1][row] == -1)
                countNeigh++;

        return countNeigh;

    }

    public void checkWin()
    {

    }

    public int[][] getGameBoard()
    {return this.gameBoard;}

    public int getRows()
    {return this.rows;}

    public int getCols()
    {return this.cols;}
}
