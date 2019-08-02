package edu.byuh.squares.Logic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import java.util.List;

import edu.byuh.squares.Views.NSOptions;
import edu.byuh.squares.R;
import edu.byuh.squares.Views.MainActivity;

/**
 * Created by student on 10/12/17.
 */

public class NumberedSquare implements TickListener {
    public int id;
    private static int counter = 1;
    private RectF bounds;
    private float screenWidth, screenHeight;
    private Paint textPaint;
    private Paint textPaintBorder;
    public PointF velocity;
    private int danceSpeed;
    private float velocitySpeed;
    public boolean frozen;
    private Bitmap image, frozenImage, normalImage;
    private String label;

    /**
     *Do the math and randomize each of the squares and put the coordinates in the bounds variable
     * and makes sure the bounds are within the screensize.
     * @param parent
     */
    public NumberedSquare(View parent, String etiquette) {

        screenHeight = parent.getHeight();
        screenWidth = parent.getWidth();
        float lesser = Math.min(screenHeight, screenWidth);
        float size = lesser / 7f;
        float x = (float) (Math.random() * (screenWidth - size));
        float y = (float) (Math.random() * (screenHeight - size));
        bounds = new RectF(x, y, x + size, y + size);

        id = counter;
        counter++;
        label = etiquette;

        textPaintBorder = new Paint();
        textPaintBorder.setColor(Color.rgb(234, 82, 45));
        textPaintBorder.setTextSize(MainActivity.findThePerfectFontSize(size * 0.7f));
        //textPaintBorder.setColor(Color.WHITE);
        textPaintBorder.setTextAlign(Paint.Align.CENTER);

        textPaint = new Paint();
        //textPaint.setColor(Color.rgb(234, 82, 45));
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(MainActivity.findThePerfectFontSize(size * 0.7f));
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(3);
        textPaint.setTextAlign(Paint.Align.CENTER);

        velocitySpeed = NSOptions.getVelocitySpeed(parent.getContext());
        float randomX = (float) (size * velocitySpeed - Math.random() * size * (velocitySpeed*2));
        float randomY = (float) (size * velocitySpeed - Math.random() * size * (velocitySpeed*2));
        velocity = new PointF(randomX, randomY);

        danceSpeed = NSOptions.getDanceSpeed(parent.getContext());

        normalImage = BitmapFactory.decodeResource(parent.getResources(), R.drawable.msknorm);
        normalImage = Bitmap.createScaledBitmap(normalImage, (int)size, (int)size, true);
        frozenImage = BitmapFactory.decodeResource(parent.getResources(), R.drawable.mskfroz);
        frozenImage = Bitmap.createScaledBitmap(frozenImage, (int)size, (int)size, true);
        image = normalImage;

    }

    /**
     * This is just like the other constructor, but instead of using a randomly-generated
     * rectangle as its bounds, it uses the pre-instantiated rectangle that's passed in.
     * This is a little bit wasteful, as we're just throwing away the random rectangle,
     * but it was easier to write. I'm saving my time rather than the CPU's. :-)
     * @param parent
     * @param rect
     */
    public NumberedSquare(View parent, RectF rect, String etiquette) {
        this(parent, etiquette);
        bounds.set(rect);

    }

    /**
     * function that lets the squares 'dance' around on the screen
     */
    public void dance() {
        if (!frozen) {
            if (danceSpeed != 0) {
                float dx = (float) (Math.random() * 2 * danceSpeed) - danceSpeed;
                float dy = (float) (Math.random() * 2 * danceSpeed) - danceSpeed;
                bounds.offset(dx, dy);
            }
        }
    }

    /**
     * This method is the logic behind keeps the squares in the screen when they are moving around
     */
    public void move() {
        if (!frozen) {
            if (bounds.left < 0 || bounds.right > screenWidth) {
                velocity.x *= -1;
                if (bounds.left < 0) {
                    setLeft(1);
                } else {
                    setRight(screenWidth - 1);
                }
            }
            if (bounds.top < 0 || bounds.bottom > screenHeight) {
                velocity.y *= -1;
                if (bounds.top < 0) {
                    setTop(1);
                } else {
                    setBottom(screenHeight - 1);
                }
            }
            bounds.offset(velocity.x, velocity.y);
        }
    }

    private enum HitSide {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        NONE
    }

    /**
     * Tests whether "this" square intersects the "other" square which is passed
     * into this method as an input parameter
     *
     * @param other the other square to compare with
     * @return true if the two squares overlap; false otherwise
     */
    public boolean overlaps(NumberedSquare other) {
        return overlaps(other.bounds);
    }

    /**
     * Tests whether "this" square intersects the "other" square which is passed
     * into this method as an input parameter
     *
     * @param other the other square to compare with
     * @return true if the two squares overlap; false otherwise
     */
    public boolean overlaps(RectF other) {
        return RectF.intersects(this.bounds, other);
    }

    /**
     * checks if the squares run into each other and does logic to figure out where they hit
     * @param others
     */
    public void checkForCollisions(List<NumberedSquare> others) {
        for (NumberedSquare other : others) {
            if (other.id > this.id) {
                if (this.overlaps(other)) {
                    HitSide hitSide = HitSide.NONE;
                    float dtop = Math.abs(other.bounds.bottom - this.bounds.top);
                    float dbot = Math.abs(other.bounds.top - this.bounds.bottom);
                    float dleft = Math.abs(other.bounds.right - this.bounds.left);
                    float drt = Math.abs(other.bounds.left - this.bounds.right);
                    float min = Math.min(Math.min(dtop, dbot), Math.min(drt, dleft));
                    if (min == dtop) {
                        hitSide = HitSide.TOP;
                    }
                    if (min == dbot) {
                        hitSide = HitSide.BOTTOM;
                    }
                    if (min == dleft) {
                        hitSide = HitSide.LEFT;
                    }
                    if (min == drt) {
                        hitSide = HitSide.RIGHT;
                    }
                    exchangeMomentum(other, hitSide);
                }
            }
        }
    }

    /**
     * squares change direction and speed when they hit each other
     * @param other
     * @param hitside
     */
    private void exchangeMomentum(NumberedSquare other, HitSide hitside) {
        float tmp;
        forceApart(other, hitside);
        if (hitside == HitSide.TOP || hitside == HitSide.BOTTOM) {
            if (this.frozen) {
                other.velocity.y = -other.velocity.y;
            } else if (other.frozen) {
                this.velocity.y = -this.velocity.y;
            } else {
                tmp = this.velocity.y;
                this.velocity.y = other.velocity.y;
                other.velocity.y = tmp;
            }
        } else {
            if (this.frozen) {
                other.velocity.x = -other.velocity.x;
            } else if (other.frozen) {
                this.velocity.x = -this.velocity.x;
            } else {
                tmp = this.velocity.x;
                this.velocity.x = other.velocity.x;
                other.velocity.x = tmp;
            }
        }
    }

    /**
     *Breaks the squares apart when they collide to avoid bugs
     * @param other
     * @param hitside
     */
    private void forceApart(NumberedSquare other, HitSide hitside) {
        RectF myBounds = new RectF(this.bounds);
        RectF otherBounds = new RectF(other.bounds);
        switch (hitside) {
            case LEFT:
                this.setLeft(otherBounds.right + 1);
                other.setRight(myBounds.left - 1);
                break;
            case RIGHT:
                this.setRight(otherBounds.left - 1);
                other.setLeft(myBounds.right + 1);
                break;
            case TOP:
                this.setTop(otherBounds.bottom + 1);
                other.setBottom(myBounds.top - 1);
                break;
            case BOTTOM:
                this.setBottom(otherBounds.top - 1);
                other.setTop(myBounds.bottom + 1);
        }
    }

    private void setBottom(float b) {
        if (!frozen) {
            float dy = b - bounds.bottom;
            bounds.offset(0, dy);
        }
    }

    private void setRight(float r) {
        if (!frozen) {
            float dx = r - bounds.right;
            bounds.offset(dx, 0);
        }
    }

    private void setLeft(float lf) {
        if (!frozen) {
            bounds.offsetTo(lf, bounds.top);
        }
    }

    private void setTop(float t) {
        if (!frozen) {
            bounds.offsetTo(bounds.left, t);
        }
    }

    /**
     * This draws up the rectangles to be drawn in the onDraw method
     * @param c is the canvas we draw on
     */
    public void draw(Canvas c) {
        c.drawBitmap(image, bounds.left, bounds.top, textPaint);
        c.drawText(label, bounds.centerX(), bounds.centerY()+bounds.width()/7, textPaintBorder);
        c.drawText(label, bounds.centerX(), bounds.centerY()+bounds.width()/7, textPaint);
    }

    public static void resetCounter() {
        counter = 1;
    }

    /**
     * Returns bounds to be used in createSquares() to check if the squares intersect with each other
     * @return bounds are the x and y coordinates of the square used to draw the squares
     */
    public RectF getBounds() {
        return bounds;
    }

    /**
     * Sets the counter to the right number of squares after we check for intersection between squares
     */
    public static void subCount() {
        counter = counter - 1;
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x,y);
    }

    /**
     * Simply returns the width (which is the same as the height) of this square.
     * @return the width of the square
     */
    public float getSize() {
        return bounds.width();
    }

    /**
     * calls move on tick
     */
    @Override
    public void tick() {
        this.move();
    }

    /**
     * sets frozen boolean to true to make square look like its frozen when tapped
     */
    public void frozen(){
        frozen = true;
        image = frozenImage;
    }

    @Override
    public String toString() {
        return label;
    }
}


