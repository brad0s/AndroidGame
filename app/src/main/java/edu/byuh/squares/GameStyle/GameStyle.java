package edu.byuh.squares.GameStyle;

import java.util.List;

import edu.byuh.squares.Logic.NumberedSquare;

/**
 * Created by b on 10/27/17.
 */

public interface GameStyle {

    String getNextLevelLabel();
    String getTryAgainLabel();
    List<String> getSquareLabel();
    TouchStatus getTouchStatus(NumberedSquare numberedSquare);

}
