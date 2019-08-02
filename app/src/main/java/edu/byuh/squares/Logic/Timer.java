package edu.byuh.squares.Logic;

/**
 * Created by student on 10/12/17.
 */

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

/**
 * Created by student on 10/4/17.
 */

public class Timer extends Handler{

    private static Timer singleton;

    private ArrayList<TickListener> subscribers = new ArrayList<TickListener>() {
    };

    /**
     * registers an object to tickListener
     * @param tickListener
     */
    public void subscribe(TickListener tickListener){
        subscribers.add(tickListener);
    }

    /**
     * unregisters an object from tickListener
     * @param tickListener
     */
    public void unsubscribe(TickListener tickListener){
        subscribers.remove(tickListener);
    }

    /**
     * Timer sends a message every 100 milliseconds
     */
    private Timer() {
        sendMessageDelayed(obtainMessage(), 100);
    }

    public static Timer getTimer(){
        if (singleton == null) {
            singleton = new Timer();
        }
        return singleton;
    }


    @Override
    public void handleMessage(Message m){
        /*for (NumberedSquare moveNS : squares){
            moveNS.move();
            moveNS.CheckCollisions(squares);
        }*/
        for (TickListener tickListener : subscribers){
            tickListener.tick();
        }
        //invalidate();
        sendMessageDelayed(obtainMessage(), 100);
    }

}
