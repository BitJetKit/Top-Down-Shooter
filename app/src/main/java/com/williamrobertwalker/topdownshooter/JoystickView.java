package com.williamrobertwalker.topdownshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.LoginFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    // =========================================
    // Private Members
    // =========================================

    private final   String                  TAG = "JoystickView";
    private         Paint                   circlePaint;
    private         Paint                   handlePaint;
    private         double                  touchX, touchY;
    private         int                     innerPadding;
    private         int                     handleRadius;
    private         int                     handleInnerBoundaries;
    private         JoystickMovedListener   listener;
    private         int                     sensitivity;

    // =========================================
    // Constructors
    // =========================================

    public JoystickView(Context context) {

        super(context);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        initJoystickView();
    }

    // =========================================
    // Initialization
    // =========================================

    private void initJoystickView() {

        setFocusable(true);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.argb(100, 100, 100, 100));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(Color.argb(100, 100, 100, 100));
        handlePaint.setStrokeWidth(1);
        handlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerPadding = 10;
        sensitivity = 10;
    }


    // =========================================
    // Public Methods
    // =========================================

    public void setOnJostickMovedListener(JoystickMovedListener listener) {

        this.listener = listener;
    }


    // =========================================
    // Drawing Functionality
    // =========================================

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Here we make sure that we have a perfect circle
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int d = Math.min(measuredWidth, measuredHeight);

        handleRadius = (int) (d * 0.25);
        handleInnerBoundaries = handleRadius;

        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {

        int result;
        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int px = getMeasuredWidth() / 2;
        int py = getMeasuredHeight() / 2;
        int radius = Math.min(px, py);

        // Draw the background
        canvas.drawCircle(px, py, radius - innerPadding, circlePaint);

        // Draw the handle
        canvas.drawCircle((int) touchX + px, (int) touchY + py,
                handleRadius, handlePaint);

        canvas.save();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        int actionType = event.getAction();
        if(actionType == MotionEvent.ACTION_MOVE) {
            int px = getMeasuredWidth() / 2;
            int py = getMeasuredHeight() / 2;
            int radius = Math.min(px, py) - handleInnerBoundaries;

            touchX = (event.getX() - px);
            touchX = Math.max(Math.min(touchX, radius), -radius);

            touchY = (event.getY() - py);
            touchY = Math.max(Math.min(touchY, radius), -radius);

            // Coordinates
//            Log.d(TAG, "X:" + touchX + "|Y:" + touchY);

            //Set the player's Movement

            //Use this crazy set of numbers to convert the ratio to the player's ratio of speed from -75 - 75 to -6 - 6.
//            float oldMin = -75;
//            float oldMax = 75;
//            float newMin = -6;
//            float newMax = 6;
//
//            float oldValue = (float) touchX;
//            PointF preNormalizedVelocity = new PointF();
//            preNormalizedVelocity.x = (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
//            oldValue = (float) touchY;
//            preNormalizedVelocity.y = (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
//
//            preNormalizedVelocity = normalize(preNormalizedVelocity);
//            preNormalizedVelocity.x *= 6;
//            preNormalizedVelocity.y *= 6;
//            GameView.player.velocity = preNormalizedVelocity;
            PointF velocity = normalize(new PointF((float) touchX, (float) touchY));
            velocity.y *= 6;
            velocity.x *= 6;
            GameView.player.velocity = velocity;


//            GameView.player.velocity.x = (float)((touchX-(-1))/(1-(-1)));
//            GameView.player.velocity.y = (float)((touchY-(-1))/(1-(-1)));

            // Pressure
            if(listener != null) {
                listener.OnMoved((int) (touchX / radius * sensitivity), (int) (touchY / radius * sensitivity));
            }

            invalidate();
        } else if(actionType == MotionEvent.ACTION_UP) {
            returnHandleToCenter();
//            Log.d(TAG, "X:" + touchX + "|Y:" + touchY);
            //Reset the player's movement.
            GameView.player.velocity.x = 0;
            GameView.player.velocity.y = 0;
        }
        return true;
    }

    PointF vector2 = new PointF();
    public PointF normalize(PointF vector) {


        float length = (float)Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        if (length != 0) {
            vector2.x = vector.x/length;
            vector2.y = vector.y/length;
        }

        return vector2;
    }

    private void returnHandleToCenter() {

        Handler handler = new Handler();
        int numberOfFrames = 5;
        final double intervalsX = (0 - touchX) / numberOfFrames;
        final double intervalsY = (0 - touchY) / numberOfFrames;

        for(int i = 0; i < numberOfFrames; i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    touchX += intervalsX;
                    touchY += intervalsY;
                    invalidate();
                }
            }, i * 40);
        }

        if(listener != null) {
            listener.OnReleased();
        }
    }
}