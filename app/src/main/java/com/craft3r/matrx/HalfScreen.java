package com.craft3r.matrx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.craft3r.matrx.MatrX.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.craft3r.matrx.MatrX.locale;

public class HalfScreen extends AppCompatActivity {

    public static boolean Enable = false;

    private TextView empty;

    private MotionLayout ml;


    private void LoadUrl(){
        ml = (MotionLayout)findViewById(R.id.animAssist);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(Loading.vers != null) {
            if (Loading.NeedDownload) {
                final AlertDialog.Builder a_builder = new AlertDialog.Builder(HalfScreen.this);
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
                                stopService(new Intent(HalfScreen.this, Loading.class));
                                System.exit(0);
                            }
                        });

                AlertDialog alert = a_builder.create();
                alert.setTitle("Доступно обновление");
                stopService(new Intent(HalfScreen.this, Loading.class));
                alert.show();
            }
        }else {
            Toast.makeText(HalfScreen.this, "Загрузка...", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(HalfScreen.this, Load.class));
            MainActivity.IsHalfNeeded = true;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_half_screen);

        startService(new Intent(HalfScreen.this, Loading.class));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (Loading.InternetError) {
            Toast.makeText(getApplicationContext(), "Can't connect to internet", Toast.LENGTH_SHORT).show();
            finish();
            Loading.InternetError = false;
        } else {
            LoadUrl();
            new HalfScreen.LongOperation().execute("");
        }



        empty = (TextView)findViewById(R.id.empty);


        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ml.transitionToStart();
            }
        });


        MatrX.mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = MatrX.mTTS.setLanguage(locale);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(HalfScreen.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HalfScreen.this, "Init failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION : {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    MatrX.mTTS.speak("Спасибо. попробуйте снова", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    MatrX.mTTS.speak("Вы не дали мне разрешение", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Cmd(result.get(0));
                }else{
                    ml.transitionToStart();
                }
                break;
            case 11:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String url = result.get(0).replace(' ', '+');
                    MatrX.mTTS.speak("Найдётся всё", TextToSpeech.QUEUE_FLUSH, null);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com/search?q=" + url));
                    startActivity(browserIntent);
                    ml.transitionToStart();
                }
                break;
            case 12:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    try {
                        Uri gmmIntentUri = Uri.parse("geo:" + String.valueOf(MatrX.latitude) + "," + String.valueOf(MatrX.longitude) + "?q=" + Uri.encode(result.get(0)));
                        MatrX.mTTS.speak("Попробуем найти", TextToSpeech.QUEUE_FLUSH, null);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                            ml.transitionToStart();

                        }
                    }catch (SecurityException se){
                        Toast.makeText(HalfScreen.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public void Cmd(String command){


        for (int i = 0; i < Data.commands.length; i++){
            for (int a = 0; a < Data.commands[i].length; a++) {
                if (command.toLowerCase().indexOf(Data.commands[i][a].toLowerCase()) !=-1){


                    if(Data.cmd_clas[i].equals("clock")){
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                        LocalDateTime now = LocalDateTime.now();
                        MatrX.mTTS.speak("Сейчас " + dtf.format(now), TextToSpeech.QUEUE_FLUSH, null);
                        while(MatrX.mTTS.isSpeaking()){

                        }
                        ml.transitionToStart();
                    }else
                    if(Data.cmd_clas[i].equals("SomeQuestion")){
                        MatrX.mTTS.speak("Хорошо", TextToSpeech.QUEUE_FLUSH, null);
                        while(MatrX.mTTS.isSpeaking()){

                        }
                        ml.transitionToStart();
                    }else
                    if(Data.cmd_clas[i].equals("hello")){
                        MatrX.mTTS.speak("Здравствуйте сэр", TextToSpeech.QUEUE_FLUSH, null);
                        while(MatrX.mTTS.isSpeaking()){

                        }
                        ml.transitionToStart();
                    }else
                    if(Data.cmd_clas[i].equals("AskName")){
                        MatrX.mTTS.speak("Меня завут Матрица", TextToSpeech.QUEUE_FLUSH, null);
                        while(MatrX.mTTS.isSpeaking()){

                        }
                        ml.transitionToStart();
                    }else
                    if(Data.cmd_clas[i].equals("search")){
                        MatrX.mTTS.speak("что хотите найти?", TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep(1000);
                        }catch (Exception e) {

                        }
                            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);

                            if(intent.resolveActivity(getPackageManager()) != null){
                                startActivityForResult(intent, 11);
                            }else{
                                Toast.makeText(HalfScreen.this, "Your device don't support speech input", Toast.LENGTH_SHORT).show();
                            }

                    }else
                    if(Data.cmd_clas[i].equals("geolocation")){
                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // If permission denied
                            MatrX.mTTS.speak("У меня нет доступа к вашемы месту нахождения. Нажмите на кнопку: Разрешить в любом режиме, или Разрешить только во время использования приложения, если есть такие кнопки", TextToSpeech.QUEUE_FLUSH, null);
                            String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                            ActivityCompat.requestPermissions((HalfScreen) this, PERMISSIONS, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );

                            return;
                        }

                        try {
                            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            MatrX.longitude = location.getLongitude();
                            MatrX.latitude = location.getLatitude();
                        }catch (SecurityException se){

                        }

                        MatrX.mTTS.speak("что хотите поискать?", TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep(1500);
                        }catch (Exception e){

                        }
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, 12);
                        } else {
                            Toast.makeText(HalfScreen.this, "Your device don't support speech input", Toast.LENGTH_SHORT).show();
                        }

                    }else
                    if(Data.cmd_clas[i].equals("pr")){
                        MatrX.mTTS.speak("", TextToSpeech.QUEUE_FLUSH, null);
                        while(MatrX.mTTS.isSpeaking()){

                        }
                        ml.transitionToStart();

                    }else
                    if(Data.cmd_clas[i].equals("sps")){
                        MatrX.mTTS.speak("Незачто", TextToSpeech.QUEUE_FLUSH, null);
                        while(MatrX.mTTS.isSpeaking()){

                        }
                        finish();

                    }else{
                        finish();
                    }
                }
            }
        }
    }



    @Override
    public void onBackPressed() {
        ml.transitionToStart();
        super.onBackPressed();
    }
    private class LongOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (Loading.CanDoNext && !Loading.NeedDownload) {

                ml.transitionToEnd();

                final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(700);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            startActivityForResult(intent, 10);
                            ml.setTransitionListener(new MotionLayout.TransitionListener() {
                                @Override
                                public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {}
                                @Override
                                public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {}

                                @Override
                                public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                                    finish();
                                }

                                @Override
                                public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) { }
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(HalfScreen.this, "Your device don't support speech input", Toast.LENGTH_SHORT).show();
                }

            } else {
                new HalfScreen.LongOperation().execute("");
            }
            stopService(new Intent(HalfScreen.this, Loading.class));

            return null;
        }
    }

}