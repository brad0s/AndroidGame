package edu.byuh.squares.Views;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import edu.byuh.squares.R;

/**
 * Created by b on 10/24/17.
 */

public class NSOptions extends PreferenceActivity {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        addPreferencesFromResource(R.xml.prefs);
    }

    //this is a "facade" method to hide the nastiness
    //of Android's preferences API.
    public static boolean getMusicOption(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean("music_option", true);
    }

    public static boolean getBackgroundImage(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("background_image", true);
    }

    public static int getDanceSpeed(Context c) {
        String speed = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("dance_speed", "0");
        return Integer.parseInt(speed);
    }

    public static float getVelocitySpeed(Context context){
        String velocity = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("velocity_speed",  "0.08");
        return Float.parseFloat(velocity);
    }

    public static float getGameStyle(Context context){
        String gameStyle = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("gameStyle_key",  "0");
        return Integer.parseInt(gameStyle);
    }


}
