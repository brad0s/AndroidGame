package edu.byuh.squares.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import edu.byuh.squares.R;
import edu.byuh.squares.Views.MainActivity;
import edu.byuh.squares.Views.NSOptions;

/**
 * Created by b on 10/20/17.
 */

public class SplashActivity extends Activity {

    private ImageView imageView;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.splash);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(imageView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            float w = imageView.getWidth();
            float h = imageView.getHeight();
            float x = m.getX();
            float y = m.getY();
            RectF aboutButton = new RectF(0, h*0.75f, w*0.3f, h);
            //if user tapped lower-right corner, launch
            //the preferences screen. Otherwise, launch
            //main activity.

            if (x > w*0.7 && y > 0.75 * h) {
                Intent i = new Intent(this, NSOptions.class);
                startActivity(i);
            } else if (aboutButton.contains(x,y)){
                AlertDialog.Builder  ab = new AlertDialog.Builder(this);
                ab.setTitle("About \"Meeseeks Bounce-o-rama\"");
                ab.setMessage("This is a game");
                ab.setNeutralButton("OK", null);
                AlertDialog box = ab.create();
                box.show();
            } else {
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
        return true;
    }


}
