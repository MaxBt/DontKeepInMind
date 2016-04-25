package com.example.maxbt.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Максим on 25.04.2016.
 */
public class PasspointsManager {
    public static final int TOUCH_ACCURACY = 50;
    private static final String PASSPOINTS_SETTED = "PASSPOINTS";
    Context curActivity;
    SharedPreferences prefs;

    public void setPointCollector(PointCollector pointCollector) {
        this.pointCollector = pointCollector;
    }

    PointCollector pointCollector;
    Database db;

    public PasspointsManager(Context curActivity) {
        this.curActivity = curActivity;
        prefs = curActivity.getSharedPreferences("passpoints_prefs",Context.MODE_PRIVATE);
        db = new Database(curActivity);
    }

    public boolean arePasspointSetted(){
        return prefs.getBoolean(PASSPOINTS_SETTED,false);
    }
    public void savePasspoints(final List<Point> points){
        AlertDialog.Builder builder = new AlertDialog.Builder(curActivity);
        builder.setMessage(R.string.progress);
        final AlertDialog dig = builder.create();
        dig.show();
        AsyncTask<Void, Void,Void> task = new AsyncTask<Void, Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Log.d(MainActivity.DEBUGTAG,"Storing");
                db.storePoints(points);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d(MainActivity.DEBUGTAG,"Dismissing");
                pointCollector.clearPoints();
                //SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PASSPOINTS_SETTED, true);
                editor.commit();
                dig.dismiss();
            };

        };
        task.execute();
    }

    public boolean verifyPasspoints(final List<Point> gotPoints){
        boolean isRight = false;
        final List<Point> savedPoints = db.getPoints();
        AlertDialog.Builder builder = new AlertDialog.Builder(curActivity);
        builder.setMessage(R.string.check_prgrss);

        final AlertDialog dig = builder.create();
        dig.show();
        AsyncTask<Void,Void,Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {

                if (savedPoints.size() != gotPoints.size()){
                    Log.d(MainActivity.DEBUGTAG,String.format("Sizes differ: saved: %d, got: %d",
                            savedPoints.size(),gotPoints.size()));
                    return false;
                }

                for (int i = 0; i < PointCollector.NUMOFPOINTS; i++){
                    int difX = savedPoints.get(i).x - gotPoints.get(i).x;
                    int difY = savedPoints.get(i).y - gotPoints.get(i).y;
                    Log.d(MainActivity.DEBUGTAG,String.format("dif x: = %d,  dif  y = %d, dist: %d",
                            difX,difY,difX*difX+difY*difY));
                    Log.d(MainActivity.DEBUGTAG,String.format("saved point: (%d, %d), got point (%d, %d)",
                            savedPoints.get(i).x, savedPoints.get(i).y,gotPoints.get(i).x,gotPoints.get(i).y));
                    if (difX*difX+difY*difY > TOUCH_ACCURACY*TOUCH_ACCURACY){
                        return false;
                    }

                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                dig.dismiss();
                if (!aBoolean){
                    pointCollector.clearPoints();
                    Toast.makeText(curActivity, "Access Denied", Toast.LENGTH_LONG);
                }
            }
        };

        task.execute();
        try {
            isRight = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return isRight;
    }

    public void clearPasspoints(){
        db.deletePoints();
        pointCollector.clearPoints();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PASSPOINTS_SETTED, false);
        editor.commit();
    }
}
