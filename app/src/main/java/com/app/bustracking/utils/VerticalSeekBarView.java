package com.app.bustracking.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class VerticalSeekBarView extends View {
    private List<LatLng> coordinatesList = new ArrayList<>();
    private Paint paint;

    public VerticalSeekBarView(Context context) {
        super(context);
        init();
    }

    public VerticalSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
    }

    public void setCoordinatesList(List<LatLng> coordinatesList) {
        this.coordinatesList = coordinatesList;
        invalidate(); // Trigger a redraw
    }

    public void setCoordinatesList(LatLng coordinate) {
        coordinatesList.add(coordinate);
        invalidate(); // Trigger a redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (coordinatesList != null) {
            int height = getHeight();
            int numPoints = coordinatesList.size();
            float pointSize = 10f; // Adjust as needed

//            for (int i = 0; i < numPoints; i++) {
//                float y = (height * i) / (numPoints - 1); // Map coordinates to vertical positions
//                canvas.drawCircle(getWidth() / 2, y, pointSize, paint);
//            }

            for (int i = 0; i < numPoints; i++) {
                float y;
                if (numPoints > 1) {
                    y = (height * i) / (numPoints - 1);
                } else {
                    // Handle the case where numPoints is less than or equal to 1
                    // You can choose to draw a single point or handle it differently
                    y = height / 2;
                }
                canvas.drawCircle(getWidth() / 2, y, pointSize, paint);
            }

        }

//        if (coordinatesList != null && !coordinatesList.isEmpty()) {
//            int viewHeight = getHeight();
//            int viewWidth = getWidth();
//            int pointRadius = 10;
//
//            for (LatLng latLng : coordinatesList) {
//                float yPos = (float) ((latLng.getLatitude() / 90.0) * viewHeight); // Adjust for latitude
//                float xPos = (float) ((latLng.getLongitude() / 180.0) * viewWidth); // Adjust for longitude
//
//                canvas.drawCircle(xPos, yPos, pointRadius, paint);
//            }
//        }
    }
}
