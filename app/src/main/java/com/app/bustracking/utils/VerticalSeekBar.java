package com.app.bustracking.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class VerticalSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    private List<LatLng> coordinatesList = new ArrayList<>();

    public VerticalSeekBar(Context context) {
        super(context);
    }


    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDataList(List<LatLng> coordinatesList) {
        this.coordinatesList = coordinatesList;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);

//        c.rotate(270);
//        c.translate(-getHeight(), 0);
//
//        super.onDraw(c);
//
//        // Draw discrete points for coordinates
//        Paint pointPaint = new Paint();
//        pointPaint.setColor(Color.RED); // Customize as needed
//        pointPaint.setStyle(Paint.Style.FILL);
//
//        int height = getHeight();
//        int numPoints = coordinatesList.size();
//        float pointSize = 10f; // Adjust as needed
//
//        for (int i = 0; i < numPoints; i++) {
//            float y = (height * i) / (numPoints - 1); // Map coordinates to vertical positions
//            c.drawCircle(getWidth() / 2, y, pointSize, pointPaint);
//        }

//        c.rotate(-90);
//        c.translate(-getHeight(), 0);
//
//        // Draw SeekBar line first
//        super.onDraw(c);
//
//        // Draw discrete points on the line
//        Paint pointPaint = new Paint();
//        pointPaint.setColor(Color.RED); // Customize as needed
//        pointPaint.setStyle(Paint.Style.FILL);
//
//        int height = getHeight();
//        int numPoints = coordinatesList.size();
//        float pointSize = 10f; // Adjust as needed
//
//        for (int i = 0; i < numPoints; i++) {
//            float y = (height * i) / (numPoints - 1); // Map coordinates to vertical positions
//            float x = getWidth() / 2; // Center the points on the line
//            c.drawCircle(x, y, pointSize, pointPaint);
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int i = getMax() - (int) (getMax() * event.getY() / getHeight());
                setProgress(i);
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}