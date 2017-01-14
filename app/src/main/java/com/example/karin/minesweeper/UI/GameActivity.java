package com.example.karin.minesweeper.UI;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.karin.minesweeper.R;
import com.example.karin.minesweeper.Service.OrientationService;
import com.example.karin.minesweeper.logic.DbSingleton;
import com.example.karin.minesweeper.logic.GameLogic;
import com.example.karin.minesweeper.logic.PlayerScore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements MyButtonListener, LocationListener,OrientationService.MyServiceListener {

    public final static String DETAILS = "LEVEL";
    public final static String LP = "LP";
    private GameLogic gameLogic;
    private int rows, cols, mines;
    private String level;
    private GridLayout grid;
    private TextView timerTextView;
    private long startTime = 0;
    private Button helpButton;
    private Button highScoresButton;
    private boolean endGame = false;
    private LocationManager locationManager;
    private Location location;
    private MyButton[][] tiles;
    private DbSingleton dbs;

    private final String TAG = GameActivity.class.getSimpleName();
    private OrientationService myService;
    boolean ifFirst = true;
    float[] firstCheck = new float[3];
    long lastUpdated = 0;
    private TextView txtNumMine;

    int countAnimation = 0;
    boolean show = false;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
            if(serviceBinder instanceof OrientationService.ServiceBinder) {
                setService(((OrientationService.ServiceBinder) serviceBinder).getService());
            }
            Log.d(TAG,"onServiceConnected: "+ name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            setService(null);
            Log.d(TAG,"onServiceDisconnected: "+ name);
        }
    };

    //Timer
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            startTime += 1000;

            timerTextView.setText(String.format("%02d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 1000);
        }
    };

    Handler gpsPosHandler = new Handler();
    Runnable selfPositionThred = new Runnable() {
        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(GameActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(GameActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GameActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                return;
            }
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                Log.d(TAG,"Location obtained");
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, GameActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);

        dbs = DbSingleton.getInstance(getApplicationContext());

        //Timer section
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        helpButton = (Button) findViewById(R.id.button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(GameActivity.this);
                dialog.setContentView(R.layout.helpdialog);
                dialog.show();

            }
        });

        highScoresButton = (Button) findViewById(R.id.hs);
        highScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highScoresButton.setVisibility(View.INVISIBLE);
                helpButton.setVisibility(View.INVISIBLE);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                HighScoresFragment f = new HighScoresFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("ActivityId", R.id.activity_game);
                f.setArguments(bundle);
                fragmentTransaction.replace(R.id.activity_game, f);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        rows = getIntent().getIntExtra("ROWS",0);
        cols = getIntent().getIntExtra("COLS",0);
        mines = getIntent().getIntExtra("MINES",0);
        level = getIntent().getStringExtra(DETAILS);

        grid = (GridLayout)findViewById(R.id.board);
        grid.setColumnCount(cols);
        grid.setRowCount(rows);
        grid.setId(0);
        gameLogic = new GameLogic(rows,cols, mines);
        txtNumMine = (TextView)findViewById(R.id.txtMines);
        int n = gameLogic.getMinesCount();
        txtNumMine.setText(String.valueOf(n));
        tiles = new MyButton[rows][cols];

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {

                tiles[i][j] = new MyButton(this, i, j);
                tiles[i][j].setBackgroundColor(Color.TRANSPARENT);
                int width = getWindowManager().getDefaultDisplay().getWidth();
                int height = getWindowManager().getDefaultDisplay().getHeight();
                android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width / (rows + 1), width / (rows + 1));
                tiles[i][j].setLayoutParams(lp);
                tiles[i][j].setListener(this);
                tiles[i][j].setText(" ");
                tiles[i][j].setTextSize(12);
                tiles[i][j].setBackgroundResource(R.drawable.box);

                grid.addView(tiles[i][j]);
            }
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startTime = 0;

        boolean bindingSucceeded = bindService(new Intent(this, OrientationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate: " + (bindingSucceeded ? "the binding succeeded..." : "the binding failed!"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        if(myService != null)
            myService.stopListening();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        else {
            gpsPosHandler.postDelayed(selfPositionThred, 0);
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }

    @Override
    public void buttonClick(MyButton myButton)
    {
        int curRow = myButton.getRow();
        int curCol = myButton.getCol();

        //step on mine
        if(myButton.isClickable() && gameLogic.CheckMine(curRow,curCol)) {
            loose(myButton);
            myService.stopListening();

        }   //step on no mine
        else if (myButton.isEnabled() && myButton.isClickable())
            noMineClick(myButton, curRow, curCol);

        if(gameLogic.checkWin())
        {
            timerHandler.removeCallbacks(timerRunnable);
            endGame = true;

            final CharSequence score = timerTextView.getText();
            checkHigherScore(score);
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

    public void disableButtons(GridLayout layout)
    {
        // Get all touchable views
        ArrayList<View> layoutButtons = layout.getTouchables();

        // loop through them, if they are an instance of Button, disable it.
        for(View v : layoutButtons){
            if( v instanceof Button ) {
                ((Button)v).setEnabled(false);
            }
        }
    }

    public void loose(MyButton myButton)
    {
        timerHandler.removeCallbacks(timerRunnable);
        endGame = true;
        boolean finish = false;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                tilesAnimation(tiles[i][j]);

        ArrayList<Integer> mines = new ArrayList<>();
        mines = gameLogic.getMinePos();
        this.mines = gameLogic.getMinesCount();
        for(int i = 0; i< this.mines; i++)
            grid.getChildAt(mines.get(i)).setBackgroundResource(R.drawable.mine);
        myButton.setBackgroundResource(R.drawable.mine_clicked);
        disableButtons(grid);

        final Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra(DETAILS,"Lose");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){

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
        int rndNum;

        rndNum = random.nextBoolean() ? 1 : -1;
        ObjectAnimator flyOutX = ObjectAnimator.ofFloat(currentTile, "x", currentTile.getX(), rndNum * size.x);
        flyOutX.setDuration(duration);
        flyOutX.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator flyOutY = ObjectAnimator.ofFloat(currentTile, "y", currentTile.getY(), size.y);
        flyOutY.setDuration(duration);
        flyOutY.setInterpolator(new DecelerateInterpolator());

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

    public void noMineClick(MyButton myButton, int curRow, int curCol)
    {
        myButton.setEnabled(false);
        int num = gameLogic.updateCell(curRow, curCol);
        if (num == 0)
        {
            myButton.setBackgroundResource(R.drawable.box_clicked);
            if ((curRow - 1) >= 0)
                buttonClick((MyButton) grid.getChildAt((curRow - 1) * rows + curCol));
            if ((curRow + 1) <= rows - 1)
                buttonClick((MyButton) grid.getChildAt((curRow + 1) * rows + curCol));
            if ((curCol - 1) >= 0)
                buttonClick((MyButton) grid.getChildAt(curRow * rows + curCol - 1));
            if ((curCol + 1) <= cols - 1)
                buttonClick((MyButton) grid.getChildAt(curRow * rows + curCol + 1));
            if ((curCol + 1) <= cols - 1 && (curRow + 1) <= rows - 1)
                buttonClick((MyButton) grid.getChildAt((curRow + 1) * rows + curCol + 1));
            if ((curCol + 1) <= cols - 1 && (curRow - 1) >= 0)
                buttonClick((MyButton) grid.getChildAt((curRow - 1) * rows + curCol + 1));
            if ((curCol - 1) >= 0 && (curRow - 1) >= 0)
                buttonClick((MyButton) grid.getChildAt((curRow - 1) * rows + curCol - 1));
            if ((curCol - 1) >= 0 && (curRow + 1) <= rows - 1)
                buttonClick((MyButton) grid.getChildAt((curRow + 1) * rows + curCol - 1));
        }
        else
        {
            myButton.setBackgroundResource(R.drawable.box_clicked);
            myButton.setText(num + "");
            myButton.setTextColor(Color.BLACK);
        }
    }

    public void checkHigherScore(CharSequence score)
    {
        ArrayList<PlayerScore> arr = dbs.getPlayerScoresByLevel(level);
        if(arr.size()<10)
            newHighScore(score);
        else
        {
            String lowScore = arr.get(arr.size() - 1).getPlayerTime();
            for(int i = 0; i < score.length(); i++)
            {
                if (score.charAt(i) < lowScore.charAt(i))
                {
                    newHighScore(score);
                    break;
                }
                else if (score.charAt(i) > lowScore.charAt(i))
                {
                    final Intent intent2 = new Intent(this,EndGameActivity.class);
                    intent2.putExtra(DETAILS,"win");
                    intent2.putExtra("TIMER",score);
                    disableButtons(grid);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            startActivity(intent2);
                            finish();
                        }
                    }, 3000);
                    break;
                }
            }
        }
    }

    public void winAnimation(final ImageView jumpImg, Dialog dialog)
    {
        long duration = 400;
        int height = dialog.getWindow().getAttributes().height;
        final MediaPlayer jump = MediaPlayer.create(this, R.raw.jump);
        ObjectAnimator upAnim = ObjectAnimator.ofFloat(jumpImg, "y", 580, 220);
        upAnim.setDuration(duration);
        upAnim.setInterpolator(new LinearInterpolator());
        ObjectAnimator downAnim = ObjectAnimator.ofFloat(jumpImg, "y",220, 580);
        downAnim.setDuration(duration);
        downAnim.setInterpolator(new LinearInterpolator());



        final AnimatorSet animationSet1 = new AnimatorSet();
        animationSet1.playSequentially(upAnim,downAnim);
        animationSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(countAnimation<3)
                {
                    countAnimation++;
                    animationSet1.start();
                    jump.start();
                }
                else {
                    jumpImg.setY(580);
                    show = true;
                }
            }
        });

        animationSet1.start();
        jump.start();
    }

    public void newHighScore(final CharSequence score)
    {
        final Intent intent = new Intent(this,StartPageActivity.class);
        final Dialog dialog = new Dialog(GameActivity.this);
      //  dialog.setTitle("New High Score!");
        dialog.setContentView(R.layout.popup);

        //cancel back operation and outside click
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        final ImageView jumpImg = (ImageView)dialog.findViewById(R.id.jump);
        winAnimation(jumpImg,dialog);

        final EditText editText = (EditText)dialog.findViewById(R.id.name);
        final TextView txtRes = (TextView)dialog.findViewById(R.id.level);
        final TextView txtEnter = (TextView)dialog.findViewById(R.id.easy);
        final TextView s = (TextView)dialog.findViewById(R.id.score);
        final Button submit = (Button)dialog.findViewById(R.id.submit);

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable(){
            @Override
            public void run(){
                editText.setVisibility(View.VISIBLE);
                txtRes.setVisibility(View.VISIBLE);
                txtEnter.setVisibility(View.VISIBLE);
                s.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);

            }
        }, 5000);


        s.setText(score.toString());
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                PlayerScore p = null;
                if(name.isEmpty())
                    name = "Anonymous";
                if(location != null)
                    p = new PlayerScore(name, (String) score, level, location.getLatitude(), location.getLongitude());
                else
                    p = new PlayerScore(name, (String) score, level, 0.0, 0.0);

                ArrayList<PlayerScore> arr = dbs.getPlayerScoresByLevel(level);
                if(arr.size() > 9)
                    arr.remove(arr.size()-1);
                arr.add(p);
                dbs.setIsChanged(true);
                startActivity(intent);
                finish();
            }
        });

        disableButtons(grid);
        Handler handler = new Handler();
        //delay necessary?
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                dialog.show();
            }
        }, 0);
    }

    @Override
    public void onBackPressed()
    {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count != 0) {
            getFragmentManager().popBackStack();
            if(count == 1) {
                highScoresButton.setVisibility(View.VISIBLE);
                helpButton.setVisibility(View.VISIBLE);
            }
        }
        else if (!endGame){
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null) {
            Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        timerHandler.postDelayed(timerRunnable, 0);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void returnBoard(ArrayList<Point> updatedCell)
    {
        for(int i=0; i<updatedCell.size();i++)
        {
            int row = updatedCell.get(i).x;
            int col = updatedCell.get(i).y;
            tiles[row][col].setText("");
            tiles[row][col].setEnabled(true);
            tiles[row][col].setClickable(true);
            tiles[row][col].setBackgroundResource(R.drawable.box);
        }
    }

    @Override
    public void onSensorEvent(float[] values) {
       ArrayList<Point> updatedCells = new ArrayList<>();
        if(ifFirst) {
            Log.d(TAG, "OnSensorEventFirstTime: "+ Arrays.toString(values));
            firstCheck[0] = values[0];
            firstCheck[1] = values[1];
            firstCheck[2] = values[2];
            ifFirst = false;
        }
        else
        {
            if((Math.abs(values[0] - firstCheck[0]) > 0.5 * Math.abs(values[0])) && (Math.abs(values[1] - firstCheck[1]) > 0.5*Math.abs(values[1])) && (Math.abs(values[2]-firstCheck[2]) > 0.5*Math.abs(values[2]))) {
                Log.d(TAG, "OnSensorEventFirst: " + Arrays.toString(firstCheck));
                Log.d(TAG, "OnSensorEvent: " + Arrays.toString(values));

                long curTime = System.currentTimeMillis();
                if(curTime - lastUpdated > 2000) {
                    Log.d(TAG, "5 sec passed time;" + curTime);
                    lastUpdated = curTime;
                    if (gameLogic.getMinesCount()<rows*cols) {
                        updatedCells = gameLogic.addMine();
                        returnBoard(updatedCells);
                        txtNumMine.setText(String.valueOf(gameLogic.getMinesCount()));
                    }
                    else
                    {
                        txtNumMine.setText("Maximum mines in board, you lose!");
                        MyButton lastMine = new MyButton(this,rows-1,cols-1);
                        loose(lastMine);
                    }
                }
            }
        }
    }

    public void setService(OrientationService service)
    {
        if(service != null)
        {
            this.myService = service;
            service.setListener(this);
            service.startListening();
        }
        else
        {
            if(this.myService != null)
                this.myService.setListener(null);
            this.myService = null;
        }
    }
}


