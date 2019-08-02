package edu.byuh.squares.Views;

/**
 * Created by student on 10/12/17.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import edu.byuh.squares.GameStyle.CountingGame;
import edu.byuh.squares.GameStyle.EnglishAlphabet;
import edu.byuh.squares.GameStyle.GameStyle;
import edu.byuh.squares.GameStyle.GermanAlphabet;
import edu.byuh.squares.GameStyle.RussianAlphabet;
import edu.byuh.squares.GameStyle.SpellingGame;
import edu.byuh.squares.Logic.HighScore;
import edu.byuh.squares.Logic.NumberedSquare;
import edu.byuh.squares.R;
import edu.byuh.squares.Logic.TickListener;
import edu.byuh.squares.Logic.Timer;
import edu.byuh.squares.GameStyle.TouchStatus;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Math.abs;



/**
 * Created by student on 9/19/17.
 */

public class GameView extends ImageView implements TickListener {

    private boolean init;
    public List<NumberedSquare> squares;
    private float w, h;
    private GameStyle gs;
    private int score = 0;
    private Paint textPaint;
    private JSONArray arrayOfScores;
    private ArrayList<HighScore> preScores = new ArrayList<>();
    private ArrayList<HighScore> topFiveHS = new ArrayList<>();
    private HighScore currentScore;

    /**
     * redraws on tick
     */
    @Override
    public void tick(){
        for (NumberedSquare ns : squares) {
            ns.checkForCollisions(squares);
            ns.dance();
        }

        invalidate();
    }

    /**
     * Constructor method. Initializes the paint objects
     * @param context the activity this view belongs to.
     */
    public GameView(Context context) {
        super(context);

        Log.d("GAMESTYLE", "GETGAMESTYLE" + NSOptions.getGameStyle(getContext()));
        if (NSOptions.getGameStyle(getContext()) == 0) {
            gs = new CountingGame();
        } else if (NSOptions.getGameStyle(getContext()) == 1 ) {
            gs = new SpellingGame();
        } else if (NSOptions.getGameStyle(getContext()) == 2) {
            gs = new EnglishAlphabet();
        } else if (NSOptions.getGameStyle(getContext()) == 3) {
            gs = new GermanAlphabet();
        } else if (NSOptions.getGameStyle(getContext()) == 4) {
            gs = new RussianAlphabet();
        }
        init = false;
        squares = new ArrayList<>();
        if (NSOptions.getBackgroundImage(getContext()) == true){
            setImageResource(R.drawable.portal);
            setScaleType(ScaleType.FIT_XY);
        } else{
            setImageResource(R.drawable.mortytrans);
            setScaleType(ScaleType.FIT_XY);
        }

        checkHighScore();

        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(55);
    }

    /**
     * This method draws the objects that we created onto the canvas
     * @param c is the canvas we draw our stuff on.
     */
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        if (!init) {
            w = c.getWidth();
            h = c.getHeight();
            createSquares();
            init = true;
        }
        Timer.getTimer().subscribe(this);
        for (NumberedSquare ns : squares){
            ns.draw(c);
        }
        c.drawText("SCORE: " + score, w*0.01f, h*0.09f, textPaint);
        if (preScores.size() > 0){
            c.drawText("HIGHSCORE: " +preScores.get(0) , w*0.01f, h*0.05f, textPaint);
        } else{
            c.drawText("HIGHSCORE: 0", w*0.01f, h*0.05f, textPaint);
        }

    }

    /**
     * On touch event
     * when user touches screen, it runs createsSquares()
     */
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            float x = m.getX();
            float y = m.getY();
            for (NumberedSquare ns : squares){
                if(ns.contains(x,y)) {
                    TouchStatus game = gs.getTouchStatus(ns);
                    if (game == TouchStatus.TRY_AGAIN) {
                        score -= 5;
                        Toast.makeText(getContext(), gs.getTryAgainLabel(), Toast.LENGTH_SHORT).show();
                    } else if (game == TouchStatus.LEVEL_COMPLETE){
                        createSquares();
                        score += 10;
                        Toast.makeText(getContext(), gs.getNextLevelLabel(), Toast.LENGTH_SHORT).show();
                        break;
                    } else if (game == TouchStatus.CONTINUE) {
                        //ns.frozen();
                        score += 10;
                    } else if (game == TouchStatus.GAME_OVER) {
                        score += 10;

                        currentScore = new HighScore(score);
                        try{
                            preScores.add(currentScore);

                            arrayOfScores.put(currentScore.saveState());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Collections.sort(preScores);
                        for(int l=0; l<5; l++) {
                            topFiveHS.add(preScores.get(l));
                            try {
                                arrayOfScores.put(preScores.get(l).saveState());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        Collections.sort(topFiveHS);
                        Log.d("SCORES", "top5 " + topFiveHS);
                        if (topFiveHS.get(topFiveHS.size()-1) == currentScore){
                            playAgainBox();

                        } else{
//                            try {
//                                arrayOfScores.put(currentScore.saveState());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                            writeHighScore();
                        }
//                        if (preScores.get(0).equals(currentScore)) {
//
//                            writeHighScore();
//                        } else {
//
//                            playAgainBox();
//                        }
                    }
                }
            }
            invalidate();
        }
        return true;
    }

    public void playAgainBox(){
        //game over logic
        AlertDialog.Builder  ab = new AlertDialog.Builder(getContext());
        ab.setTitle("You Won!")
                .setMessage("Would you like to play again?")
                .setCancelable(false) //doesn't let the user tap outside the dialog box
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createSquares();
                        score = 0;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)getContext()).finish();
                    }
                });
        AlertDialog box = ab.create();
        box.show();
    }

    private void checkHighScore(){
        //Write the score to a txt file
        arrayOfScores = new JSONArray();
        try {
            FileInputStream fileInputStream = getContext().openFileInput(gs.toString());
            Scanner scanner = new Scanner(fileInputStream);
            StringBuilder stringOfScores = new StringBuilder();
            while (scanner.hasNext()){
                stringOfScores.append(scanner.nextLine());
            }
            scanner.close();
            String temp = stringOfScores.toString();
            Log.d("SCORES","LOAD TEMP" + temp);
            arrayOfScores = new JSONArray(temp);
            Log.d("SCORES", "arrayofscores" + arrayOfScores);
            for (int i = 0; i < arrayOfScores.length(); i++) {
                try {
                    JSONObject tempObj = arrayOfScores.getJSONObject(i);
                    HighScore highScore = new HighScore(tempObj);
                    preScores.add(highScore);

                } catch (JSONException e) {
                   e.printStackTrace();
                }
            }
            Log.d("SCORES", "Prescores: " + preScores);
            //Collections.sort(preScores);

            Log.d("SCORES", "" + preScores.size());
        } catch (FileNotFoundException e) {
            HighScore a = new HighScore("A", 10);
            HighScore b = new HighScore("B", 15);
            HighScore c = new HighScore("C", 25);
            HighScore d = new HighScore("D", 29);
            HighScore f = new HighScore("E", 30);
            preScores.add(a);
            preScores.add(b);
            preScores.add(c);
            preScores.add(d);
            preScores.add(f);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void writeHighScore(){
        try {
            FileOutputStream fileOutputStream = getContext().openFileOutput(gs.toString(), Context.MODE_PRIVATE);
            String JSONText = arrayOfScores.toString();
            Log.d("SCORES", "JSONWRITE" + arrayOfScores);
            fileOutputStream.write(JSONText.getBytes());
            fileOutputStream.close();
            Log.d("WRITE!!", JSONText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TableLayout highScoresTable = new TableLayout(getContext());
        TableRow tr = new TableRow(getContext());
        for(HighScore hs : topFiveHS) {
            tr = hs.getTableRow(getContext());
            highScoresTable.addView(tr);
        }

        Log.d("SCORES", "table" + highScoresTable);
        Log.d("SCORES", "rows" + tr);

        //high score and play again box
        AlertDialog.Builder  ab = new AlertDialog.Builder(getContext());
        ab.setView(highScoresTable)
                .setTitle("You Got a High Score!!")
                .setMessage("Congratulations! You got a new high score of " + Integer.toString(score) + ". Would you like to play again?")
                .setCancelable(false) //doesn't let the user tap outside the dialog box
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createSquares();
                        score = 0;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)getContext()).finish();
                    }
                });
        AlertDialog box = ab.create();
        box.show();
    }

    /**
     * creates the squares and checks if the randomized squares that were created
     * intersect with each other. IF they do it creates more squares until enough
     * squares are created
     */
    private void createSquares() {
        squares.clear();
        NumberedSquare.resetCounter();
        List<String> labels = gs.getSquareLabel();
        NumberedSquare first = new NumberedSquare(this, labels.get(0));
        float size = first.getSize();
        squares.add(first);
        Timer.getTimer().subscribe(first);
        for (int i=1; i<labels.size(); i++) {
            boolean legal = false;
            while (!legal) {
                float candidateX = (float)(Math.random() * (w-size));
                float candidateY = (float)(Math.random() * (h-size));
                RectF candidate = new RectF(candidateX, candidateY, candidateX+size, candidateY+size);
                legal = true;
                for (NumberedSquare other : squares) {
                    if (other.overlaps(candidate)) {
                        legal = false;
                        break;
                    }
                }
                if (legal) {
                    NumberedSquare ns = new NumberedSquare(this, candidate, labels.get(i));
                    squares.add(ns);
                    Timer.getTimer().subscribe(ns);
                }
            }
        }
       Toast.makeText(getContext(), gs.getNextLevelLabel(), Toast.LENGTH_SHORT).show();
    }

}

