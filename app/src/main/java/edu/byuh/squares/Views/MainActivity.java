package edu.byuh.squares.Views;

import android.app.Activity;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.byuh.squares.R;

public class MainActivity extends Activity {

    private MediaPlayer music;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        music = MediaPlayer.create(this, R.raw.zhaytee_microcomposer_1);
        music.setLooping(true);
        if (NSOptions.getMusicOption(this)) {
            music.start();
        }
        GameView gv = new GameView(this);
        setContentView(gv);
    }

    /**
     * Helper method I made up one day, to work around the
     * lack of documentation about font sizes in Android.
     * This function is only "partially debugged" and I do not
     * guarantee its accuracy.
     * @param lowerThreshold how many pixels high the text should be
     * @return the font size that corresponds to the requested pixel height
     */
    public static float findThePerfectFontSize(float lowerThreshold) {
        float fontSize = 1;
        Paint p = new Paint();
        p.setTextSize(fontSize);
        while (true) {
            float asc = -p.getFontMetrics().ascent;
            if (asc > lowerThreshold) {
                break;
            }
            fontSize++;
            p.setTextSize(fontSize);
        }
        return fontSize;
    }

    //TODO override onDestroy, and stop the background music.
    @Override
    public void onDestroy() {
        super.onDestroy();
        music.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (NSOptions.getMusicOption(this)) {
            music.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NSOptions.getMusicOption(this)) {
            music.start();
        }
    }
}
