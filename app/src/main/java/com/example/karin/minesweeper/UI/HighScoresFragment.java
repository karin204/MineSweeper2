package com.example.karin.minesweeper.UI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.karin.minesweeper.R;
import com.example.karin.minesweeper.logic.DbSingleton;
import com.example.karin.minesweeper.logic.PlayerScore;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Avi on 07/01/2017.
 */

public class HighScoresFragment extends Fragment implements View.OnClickListener {
    private TableLayout table;
    private Button [] button = new Button[3];
    private Button mapButton;
    private DbSingleton dbs;
    private String level = "Easy";
    ArrayList<PlayerScore> arr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_highscores, container, false);
        table = (TableLayout) v.findViewById(R.id.tableScores);
        dbs = DbSingleton.getInstance(getActivity().getApplicationContext());
        button[0] = (Button) v.findViewById(R.id.Easy);
        button[0].setOnClickListener(this);
        button[0].setEnabled(false);
        button[1] = (Button) v.findViewById(R.id.Medium);
        button[1].setOnClickListener(this);
        button[2] = (Button) v.findViewById(R.id.Hard);
        button[2].setOnClickListener(this);

        mapButton = (Button) v.findViewById(R.id.mapButton);
        mapButton.setOnClickListener(this);

        buildTable("Easy", 0);

        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.Easy: {
                level = "Easy";
                buildTable("Easy", 0);
                break;
            }

            case R.id.Medium: {
                level = "Medium";
                buildTable("Medium", 1);
                break;
            }

            case R.id.Hard: {
                level = "Hard";
                buildTable("Hard", 2);
                break;
            }

            case R.id.mapButton: {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MapScoresFragment f = new MapScoresFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Level", level);
                f.setArguments(bundle);
                fragmentTransaction.replace(getArguments().getInt("ActivityId"), f);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
        }
    }

    public void buildTable(String level, int id)
    {
        for(int i = 0; i < button.length; i++)
        {
            if(id == i)
                button[i].setEnabled(false);
            else
                button[i].setEnabled(true);
        }

        arr = dbs.getPlayerScoresByLevel(level);
        Collections.sort(arr);
        int count = arr.size();
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;

        table.removeAllViews();
        for (PlayerScore p : arr)
        {
            TableRow tr = new TableRow(getActivity());
            TextView tv1 = new TextView(getActivity());
            tv1.setText(p.getPlayerName());
            tv1.setTextSize(20);
            tv1.setPadding(50/count, 50/count, 50/count, 50/count);
            tr.addView(tv1);

            TextView tv2 = new TextView(getActivity());
            tv2.setText(p.getPlayerTime());
            tv2.setTextSize(20);
            tv2.setPadding(50/count, 50/count, 50/count, 50/count);
            tr.addView(tv2);

            table.addView(tr);
        }
    }
}
