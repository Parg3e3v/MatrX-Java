package com.craft3r.matrx;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class welcome2 extends AppCompatActivity {

    private Button prev, next;
    private MotionLayout ml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome2);

        ml = (MotionLayout)findViewById(R.id.wl2);
        if (Loading.vers != null) {
            if(Loading.vers == Loading.versNow){
                Loading.NeedDownload = false;
            }
            if (Loading.NeedDownload) {
                final AlertDialog.Builder a_builder = new AlertDialog.Builder(welcome2.this);
                a_builder.setMessage("Доступна новая версия Матрицы")
                        .setCancelable(false)
                        .setPositiveButton("Обнавить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Loading.url));
                                AlertDialog alert = a_builder.create();
                                alert.setTitle("Доступно обновление");
                                alert.show();
                                startActivity(browserIntent);
                            }
                        })
                        .setNegativeButton("Не сейчас", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        });

                AlertDialog alert = a_builder.create();
                alert.setTitle("Доступно обновление");
                alert.show();
            } else {
                Toast.makeText(welcome2.this, "Welcome to MatrX!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(welcome2.this, "PING is too big", Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ml.setTransition(R.id.start_wl2, R.id.end_wl2);
        ml.setTransitionDuration(2500);
        ml.transitionToEnd();

        ml.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) { }
            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) { }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {

                if (ActivityCompat.checkSelfPermission(welcome2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(welcome2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                    ActivityCompat.requestPermissions(welcome2.this, PERMISSIONS, MatrX.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }else{
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    but();

                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) { }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MatrX.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION : {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    but();

                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    but();
                }
                return;
            }
        }
    }

    private void but(){
        ml.setTransition(R.id.end_wl2, R.id.buttons);
        ml.setTransitionDuration(1000);
        ml.transitionToEnd();
        ml.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }
        });
    }
    
    public void nxt(View v){
        finish();
        startActivity(new Intent(welcome2.this, welcome3.class));
    }

    public void prev(View v){
        finish();
        startActivity(new Intent(welcome2.this, Welcome.class));
    }




}