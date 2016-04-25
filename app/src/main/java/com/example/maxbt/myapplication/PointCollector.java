package com.example.maxbt.myapplication;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Максим on 22.04.2016.
 */
public class PointCollector implements View.OnTouchListener {
    public static final int NUMOFPOINTS = 2;
    private List<Point> points = new ArrayList<Point>();
    private PointCollectorListener collectedPointListener;

    public void setCollectedPointListener(PointCollectorListener collectedPointListener) {
        this.collectedPointListener = collectedPointListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        points.add(new Point(x,y));
        if (points.size()==NUMOFPOINTS){
            if (collectedPointListener != null){
                collectedPointListener.pointsCollected(points);
            }
        }

        return false;
    }

    public void clearPoints(){
        points.clear();
    }
}
