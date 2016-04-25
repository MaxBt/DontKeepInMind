package com.example.maxbt.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Максим on 22.04.2016.
 */
public class Database extends SQLiteOpenHelper {

    private static final String POINTS_TABLE = "POINTS";
    private static final String[] POINTS_TABLE_COLUMNS = {"ID","X","Y"};

    public Database(Context context) {
        super(context, "myAppData.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s INTEGER NOT NULL)",
                POINTS_TABLE, POINTS_TABLE_COLUMNS[0], POINTS_TABLE_COLUMNS[1], POINTS_TABLE_COLUMNS[2]);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deletePoints(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(POINTS_TABLE, null, null);
        db.close();
    }

    public void storePoints(List<Point> points){
        SQLiteDatabase db = getWritableDatabase();



        for (Point p: points) {
            ContentValues values= new ContentValues();
            values.put(POINTS_TABLE_COLUMNS[1],p.x);
            values.put(POINTS_TABLE_COLUMNS[2],p.y);
            Log.d(MainActivity.DEBUGTAG, String.format("Stored p: %d, %d",p.x, p.y));
            db.insert(POINTS_TABLE, null, values);
        }


        db.close();

    }

    public List<Point> getPoints(){
        SQLiteDatabase db = getReadableDatabase();
        List<Point> storedPoints = new ArrayList<Point>();
        String sql = String.format("SELECT * FROM %s ORDER BY %s",POINTS_TABLE,POINTS_TABLE_COLUMNS[0]);
        Cursor cur = db.rawQuery(sql, null);
        while( cur.moveToNext()){
            int id = cur.getInt(0);
            int x = cur.getInt(1);
            int y = cur.getInt(2);
            storedPoints.add(new Point(x,y));
        }

        db.close();
        return storedPoints;
    }
}
