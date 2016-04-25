package com.example.maxbt.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class ImageActivity extends AppCompatActivity implements PointCollectorListener{
    public static final int PASSPOINS_CONFIRMED_RESULT_CODE = 9;
    private static final String CONFIRMATION_PASSPOINTS = "Passpoints_confirmed";

    private  boolean arePasspointsSetted;
    private static PasspointsManager passpointsManager;
    private PointCollector pointCollector;


    private void setArePasspointsSetted(boolean arePasspointsSetted) {
        this.arePasspointsSetted = arePasspointsSetted;
    }

    public void setPointCollector(PointCollector pointCollector) {
        this.pointCollector = pointCollector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        passpointsManager = new PasspointsManager(this);
        setPointCollector(new PointCollector());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);




        addTouchListener();
        setArePasspointsSetted(passpointsManager.arePasspointSetted());
        if(!arePasspointsSetted){showPromptCreatePsswrd();}
        pointCollector.setCollectedPointListener(this);
        passpointsManager.setPointCollector(pointCollector);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            boolean reset = extras.getBoolean(MainActivity.RESET_PASSPOINTS);
            if (reset) {
                showPromptResetPsswrd();
            }
        }
    }

    private void showPromptResetPsswrd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restoring PassPoints");
        builder.setMessage("Do u want to restore Psswrd?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(MainActivity.DEBUGTAG, "Show confirmation dig");
                showPromptConfirmPsswrd();
                Log.d(MainActivity.DEBUGTAG, "Set confirmation true");
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(CONFIRMATION_PASSPOINTS, true);
                edit.commit();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dig = builder.create();
        dig.show();
    }

    private void showPromptConfirmPsswrd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm passpoints");
        builder.setMessage("pleaseeeee");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dig = builder.create();
        dig.show();

    }

    private void showPromptRepeatPsswrd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Repeat passpoints");
        builder.setMessage("You need do it");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dig = builder.create();
        dig.show();
    }

    private void showPromptCreatePsswrd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setTitle("Create password!");
        builder.setMessage("You need to create Password");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void addTouchListener() {
        ImageView view = (ImageView) findViewById(R.id.touch_image);
        view.setOnTouchListener(pointCollector);
    }
    @Override
    public void pointsCollected(final List<Point> points) {

        setArePasspointsSetted(passpointsManager.arePasspointSetted());
        if(arePasspointsSetted){
            Log.d(MainActivity.DEBUGTAG,"Verifying");
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            boolean confirmation = prefs.getBoolean(CONFIRMATION_PASSPOINTS, false);
            boolean verified = passpointsManager.verifyPasspoints(points);

            if (verified){
                if (confirmation){
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(CONFIRMATION_PASSPOINTS, false);
                    edit.commit();
                    Log.d(MainActivity.DEBUGTAG, "DELETING PASSPOINTS");
                    passpointsManager.clearPasspoints();
                    setArePasspointsSetted(passpointsManager.arePasspointSetted());
                    Log.d(MainActivity.DEBUGTAG, "Return to main activity");
                    setResult(PASSPOINS_CONFIRMED_RESULT_CODE);

                }
                else {
                    Log.d(MainActivity.DEBUGTAG, "Just staring new");
                    Intent i = new Intent(ImageActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }
        }else{
            Log.d(MainActivity.DEBUGTAG,"Saving");
            passpointsManager.savePasspoints(points);
        }
    }


}
