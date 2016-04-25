package com.example.maxbt.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    public  static final String DEBUGTAG = "allDebusMsg";
    public static final String TEXTFILE = "input.txt";
    public static final String FILESAVED = "FileSaved";
    public static final String RESET_PASSPOINTS = "Resetpasspoints";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addOnSaveListener();
        addOnLockListener();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (prefs.getBoolean(FILESAVED, false)){
        loadSavedFile();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_reset_passpoints:
                Log.d(MainActivity.DEBUGTAG,"Reset passpoints clicked");
                Intent i = new Intent(MainActivity.this,ImageActivity.class);
                i.putExtra(RESET_PASSPOINTS, true);
                startActivityForResult(i, ImageActivity.PASSPOINS_CONFIRMED_RESULT_CODE);
                startActivity(i);
                return true;
            case R.id.menu_change_photo:
                Log.d(MainActivity.DEBUGTAG,"Changing photo");
                Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(photo);
                return true;

        }
        return true;
    }

    private void loadSavedFile() {

        try {
            FileInputStream fis = openFileInput(TEXTFILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(fis)));
            String line;
            EditText editText = (EditText)findViewById(R.id.inputText);
            while ((line = reader.readLine()) != null){
                line = line.toUpperCase();
                editText.append(line);
                editText.append("\n");
            }
        } catch (FileNotFoundException e) {
            Log.e(DEBUGTAG,"Erreur: file nit found", e);
        } catch (IOException e) {
            Log.e(DEBUGTAG,"Erreur: ", e);
        }
        Log.d(DEBUGTAG,"FileFound");
    }

    private void addOnSaveListener(){
        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.inputText);
                String text = editText.getText().toString();
                FileOutputStream fos;
                try{
                    fos = openFileOutput(TEXTFILE,MODE_PRIVATE);
                    fos.write(text.getBytes());
                    fos.close();
                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(FILESAVED, true);
                    editor.commit();
                }
                catch (FileNotFoundException fnfe){
                    Log.e(DEBUGTAG,"Erreur: file not found", fnfe);
                }
                catch (IOException ioe){
                    Log.e(DEBUGTAG,"Erreur: something wrong", ioe);
                }
                Log.d(DEBUGTAG,"Clicked!!" + text);
                Toast.makeText(MainActivity.this, getString(R.string.toast_file_saved), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOnLockListener() {
        Button lock = (Button) findViewById(R.id.lockBtn);
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ImageActivity.class);
                startActivity(i);
            }
        });
    }
}
