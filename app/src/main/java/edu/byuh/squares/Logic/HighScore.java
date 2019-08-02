package edu.byuh.squares.Logic;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by b on 11/21/17.
 */

public class HighScore implements Comparable<HighScore> {

    public String name;
    public int score;
    private static final String JSON_Name = "NAME";
    private static final String JSON_Score = "SCORE";


    public HighScore(int HSscore) {
        name = null;
        this.score = HSscore;
    }

    public HighScore(String n, int s){
        this.name = n;
        this.score = s;
    }


    public HighScore(JSONObject JSONobj) {
        try {
            name = JSONobj.getString(JSON_Name);
            score = Integer.parseInt(JSONobj.getString(JSON_Score));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(HighScore other) {
        if (other.score > this.score) {
            return 1;
        } else{
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HighScore){
            HighScore other = (HighScore)obj;
           return this.score == other.score;
        } else {
            return false;
        }
    }

    public JSONObject saveState() throws Exception {
        JSONObject jsonHighScoreObj = new JSONObject();
        jsonHighScoreObj.put(JSON_Name, name);
        jsonHighScoreObj.put(JSON_Score, score);
        return jsonHighScoreObj;
    }

    @Override
    public String toString() {
        return score +"";
    }

    public TableRow getTableRow(Context context) {
        TableRow tableRow = new TableRow(context);
        TextView scoreText = new TextView(context);
        scoreText.setText("" + score);
        TextView nameField;
        if (name != null){
            nameField = new TextView(context);
            nameField.setText(name);
            tableRow.addView(nameField);
            tableRow.addView(scoreText);
        } else {
            nameField = new EditText(context);
            nameField.addTextChangedListener(new TextWatcher() {
                 @Override
                 public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 }
                 @Override
                 public void onTextChanged(CharSequence s, int start, int before, int count) {
                     name = s.toString();
                 }
                 @Override
                 public void afterTextChanged(Editable s) {
                 }
             });
            tableRow.addView(nameField);
            tableRow.addView(scoreText);
        }
//        tableRow.addView(nameField);
//        tableRow.addView(scoreText);
        return tableRow;
    }
}
