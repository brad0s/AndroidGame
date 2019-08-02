package edu.byuh.squares.GameStyle;

import java.util.ArrayList;
import java.util.List;

import edu.byuh.squares.Logic.NumberedSquare;

/**
 * Created by b on 10/27/17.
 */

public class CountingGame implements GameStyle {

    //private GameView gameView = new GameView(Context c);
    public static int level = 1;
    private int count = 1;
    private TouchStatus touchStatus;
    private ArrayList<String> labels;

    public CountingGame() {
        level = 1;
        count = 1;
        labels = new ArrayList<>();
        prepare();
    }

    private void prepare() {
        count = 1;
        labels.clear();
        for (int i=1; i<=level; ++i) {
            labels.add(""+i);
        }
    }

    /**
     *
     * @return next level string
     */
    @Override
    public String getNextLevelLabel() {
        String levelLabel = "Level " + level;
        return levelLabel;
    }

    /**
     *
     * @return try again string
     */
    @Override
    public String getTryAgainLabel() {
        String tryAgainLabel = "Out of Order. Try Again!";
        return tryAgainLabel;
    }

    @Override
    public List<String> getSquareLabel() {
        return labels;
    }

    /**
     * Logic for touching the squares. It takes in the touched square and compares
     * the logic for it
     * @param numberedSquare
     * @return gives the enum that correlates to the toast value
     */
    @Override
    public TouchStatus getTouchStatus(NumberedSquare numberedSquare) {
        if (count == numberedSquare.id && !numberedSquare.frozen){
            numberedSquare.frozen();
            count++;
            touchStatus = TouchStatus.CONTINUE;
            if (numberedSquare.id == level && numberedSquare.frozen) {
                if (level == 3){
                    touchStatus = TouchStatus.GAME_OVER;
                    level = 1;
                    count = 1;
                    prepare();
                    toString();
                } else {
                    level++;
                    prepare();
                    count = 1;
                    touchStatus = TouchStatus.LEVEL_COMPLETE;
                }
            }
        } else {
            touchStatus = TouchStatus.TRY_AGAIN;
        }
        return touchStatus;
    }

    @Override
    public String toString() {
        return "CountingGame";
    }
}
