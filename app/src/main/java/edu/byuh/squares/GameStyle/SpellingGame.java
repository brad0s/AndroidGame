package edu.byuh.squares.GameStyle;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.byuh.squares.Logic.NumberedSquare;

/**
 * Created by b on 11/3/17.
 */

public class SpellingGame implements GameStyle {

    private TouchStatus touchStatus;

    private String[] words = {
            "DOG", "CAT", "CAR"
    };
    private int currentWordIndex;
    private List<String> labels;
    private int currentLetterIndex;

    public SpellingGame() {
        currentWordIndex = 0;
        labels = new ArrayList<>();
        prepare();
    }

    private void prepare() {
        labels.clear();
        currentLetterIndex = 0;
        String word = words[currentWordIndex];
        //first few squares have letters from the word
        for (int i=0; i<word.length(); i++) {
            labels.add(""+word.charAt(i));
        }
        //then make a few squares with random letters, to distract user
        for (int i=word.length(); i<10; i++) {
            int randomChar = 65 + (int)(Math.random() * 26);
            labels.add("" + (char)randomChar);
        }
    }
    @Override
    public String getNextLevelLabel() {
        return "Spell \"" + words[currentWordIndex] + "\"";
    }

    @Override
    public String getTryAgainLabel() {
        String word = words[currentWordIndex];
        char let = word.charAt(currentLetterIndex);
        return "Tap the \"" + let + "\" in \"" + word + "\"";
    }

    @Override
    public List<String> getSquareLabel() {
        return labels;
    }

    @Override
    public TouchStatus getTouchStatus(NumberedSquare c) {
        String word = words[currentWordIndex];
        String letter = "" + word.charAt(currentLetterIndex);
        if (letter.equals(c.toString()) && !c.frozen) {
            currentLetterIndex++;
            c.frozen();
            touchStatus = TouchStatus.CONTINUE;
            if (currentLetterIndex == word.length() && c.frozen) {
                if (currentWordIndex == words.length-1) {
                    currentLetterIndex = 0;
                    currentWordIndex = 0;
                    touchStatus = TouchStatus.GAME_OVER;
                } else {
                    currentWordIndex = (currentWordIndex + 1) % words.length;
                    prepare();
                    touchStatus = TouchStatus.LEVEL_COMPLETE;
                }
            }
        } else {
            touchStatus = TouchStatus.TRY_AGAIN;
        }
        Log.d("TOUCHSTATUS", "" + touchStatus);
        return touchStatus;
    }

    @Override
    public String toString() {
        return "SpellingGame";
    }
}
