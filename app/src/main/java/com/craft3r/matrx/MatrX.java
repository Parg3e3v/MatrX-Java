package com.craft3r.matrx;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class MatrX extends AppCompatActivity {

    public class SharedPreference {

        android.content.SharedPreferences pref;
        android.content.SharedPreferences.Editor editor;
        Context _context;
        private static final String PREF_NAME = "testing";

        public static final String KEY_SET_APP_RUN_FIRST_TIME = "KEY_SET_APP_RUN_FIRST_TIME";


        public SharedPreference(Context context) // Constructor
        {
            this._context = context;
            pref = _context.getSharedPreferences(PREF_NAME, 0);
            editor = pref.edit();

        }


        public void setApp_runFirst(String App_runFirst)
        {
            editor.remove(KEY_SET_APP_RUN_FIRST_TIME);
            editor.putString(KEY_SET_APP_RUN_FIRST_TIME, App_runFirst);
            editor.apply();
        }

        public String getApp_runFirst()
        {
            String  App_runFirst= pref.getString(KEY_SET_APP_RUN_FIRST_TIME, "FIRST");
            return  App_runFirst;
        }

    }

    private  SharedPreference sharedPreferenceObj;

    private TextView cmd, versText;
    private EditText cmd_input;
    private Button ok, quit, list, start;
    public static TextToSpeech mTTS;
    public static Locale locale = new Locale("ru");
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean UseLabel = false;

    public static double longitude, latitude;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matr_x);
        

        if (Loading.vers != null) {
            if (Loading.NeedDownload) {
                final AlertDialog.Builder a_builder = new AlertDialog.Builder(MatrX.this);
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
                Toast.makeText(MatrX.this, "Welcome to MatrX!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MatrX.this, "PING is too big", Toast.LENGTH_SHORT).show();
            finish();
        }

        sharedPreferenceObj = new SharedPreference(MatrX.this);

        if (sharedPreferenceObj.getApp_runFirst().equals("FIRST")) {
            HalfScreen.Enable = true;
            sharedPreferenceObj.setApp_runFirst("NO");
        }

        cmd = (TextView) findViewById(R.id.tempText);
        cmd_input = (EditText) findViewById(R.id.cmd_input);
        ok = (Button) findViewById(R.id.ok_but);
        quit = (Button) findViewById(R.id.quit_but);
        list = (Button) findViewById(R.id.cmd_list);
        start = (Button) findViewById(R.id.start_but);
        versText = (TextView) findViewById(R.id.textView4);

        versText.setText("v." + Loading.versNow);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    start.setEnabled(true);

                    int result = mTTS.setLanguage(locale);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MatrX.this, "Language not supported", Toast.LENGTH_SHORT).show();
                        start.setEnabled(false);
                    } else {
                        start.setEnabled(true);
                    }
                } else {
                    Toast.makeText(MatrX.this, "Init failed", Toast.LENGTH_SHORT).show();
                    start.setEnabled(false);
                }
            }
        });



        listen();
    }


    public void listen() {
        quit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTTS.stop();
                        mTTS.shutdown();
                        System.exit(0);
                    }
                }
        );

        list.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MatrX.this, CommandList.class);
                        startActivity(intent);
                    }
                }
        );

        ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tempCmd = cmd_input.getText().toString();
                        UseLabel = true;
                        Cmd(tempCmd);
                    }
                }
        );

        start.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, 10);
                        } else {
                            Toast.makeText(MatrX.this, "Your device don't support speech input", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Cmd(result.get(0));
                }
                break;
            case 11:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String url = result.get(0).replace(' ', '+');
                    mTTS.speak("Найдётся всё", TextToSpeech.QUEUE_FLUSH, null);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com/search?q=" + url));
                    listen();
                    startActivity(browserIntent);
                }
                break;
            case 12:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    try {
                        Uri gmmIntentUri = Uri.parse("geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "?q=" + Uri.encode(result.get(0)));
                        mTTS.speak("Попробуем найти", TextToSpeech.QUEUE_FLUSH, null);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);

                        }
                    }catch (SecurityException se){
                        Toast.makeText(MatrX.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }




    // For Location

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION : {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mTTS.speak("Спасибо. попробуйте снова", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                } else {
                    mTTS.speak("Вы не дали мне разрешение", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void Cmd(String command){
        cmd.setText(command);

        for (int i = 0; i < Data.commands.length; i++){
            for (int a = 0; a < Data.commands[i].length; a++) {
                if (command.toLowerCase().indexOf(Data.commands[i][a].toLowerCase()) !=-1){


                    if(Data.cmd_clas[i].equals("clock")){
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                        LocalDateTime now = LocalDateTime.now();
                        mTTS.speak("Сейчас " + dtf.format(now), TextToSpeech.QUEUE_FLUSH, null);
                    }else
                        if(Data.cmd_clas[i].equals("SomeQuestion")){
                        mTTS.speak("Хорошо", TextToSpeech.QUEUE_FLUSH, null);
//                        Toast.makeText(this, "Хорошо", Toast.LENGTH_SHORT).show();
                    }else
                        if(Data.cmd_clas[i].equals("hello")){
                            mTTS.speak("Здравствуйте сэр", TextToSpeech.QUEUE_FLUSH, null);
                    }else
                         if(Data.cmd_clas[i].equals("AskName")){
                             mTTS.speak("Меня завут Матрица", TextToSpeech.QUEUE_FLUSH, null);
                         }else
                         if(Data.cmd_clas[i].equals("search")){
                             mTTS.speak("что хотите найти?", TextToSpeech.QUEUE_FLUSH, null);
                             try {
                                 Thread.sleep(1000);
                             }catch (Exception e){

                             }
                             if(UseLabel){
                                 ok.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         String tempCmd = cmd_input.getText().toString();
                                         String url = tempCmd.replace(' ', '+');
                                         mTTS.speak("Найдётся всё", TextToSpeech.QUEUE_FLUSH, null);
                                         Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com/search?q=" + url));
                                         listen();
                                         startActivity(browserIntent);
                                         UseLabel = false;
                                     }
                                 });
                             }else{
                                 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                 intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                 intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);

                                 if(intent.resolveActivity(getPackageManager()) != null){
                                     startActivityForResult(intent, 11);
                                 }else{
                                     Toast.makeText(MatrX.this, "Your device don't support speech input", Toast.LENGTH_SHORT).show();
                                 }
                             }

                         }else
                         if(Data.cmd_clas[i].equals("geolocation")){
                             LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                             if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                 // If permission denied
                                 mTTS.speak("У меня нет доступа к вашемы месту нахождения. Нажмите на кнопку: Разрешить в любом режиме, или Разрешить только во время использования приложения, если есть такие кнопки", TextToSpeech.QUEUE_FLUSH, null);
                                 String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                                 ActivityCompat.requestPermissions((MatrX) this, PERMISSIONS, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );

                                 return;
                             }

                             try {
                                 Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                 longitude = location.getLongitude();
                                 latitude = location.getLatitude();
                             }catch (SecurityException se){

                             }

                             mTTS.speak("что хотите поискать?", TextToSpeech.QUEUE_FLUSH, null);
                             try {
                                 Thread.sleep(1500);
                             }catch (Exception e){

                             }
                             if(UseLabel){
                                 ok.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         String tempCmd = cmd_input.getText().toString();
                                         Uri gmmIntentUri = Uri.parse("geo:40.177200,44.503490?q=" + Uri.encode(tempCmd));
                                         mTTS.speak("Попробуем найти", TextToSpeech.QUEUE_FLUSH, null);
                                         Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                         mapIntent.setPackage("com.google.android.apps.maps");
                                         listen();
                                         startActivity(mapIntent);
                                     }
                                 });
                             }else {
                                 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                 intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                 intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);

                                 if (intent.resolveActivity(getPackageManager()) != null) {
                                     startActivityForResult(intent, 12);
                                 } else {
                                     Toast.makeText(MatrX.this, "Your device don't support speech input", Toast.LENGTH_SHORT).show();
                                 }
                             }

                         }else
                         if(Data.cmd_clas[i].equals("pr")){
                             mTTS.speak("", TextToSpeech.QUEUE_FLUSH, null);

                         }else
                         if(Data.cmd_clas[i].equals("end")){
                             mTTS.speak("До новых встреч", TextToSpeech.QUEUE_FLUSH, null);
                             try {
                                 Thread.sleep(1500);
                             }catch (Exception e){

                             }
                             System.exit(0);

                         }else
                         if(Data.cmd_clas[i].equals("sps")){
                             mTTS.speak("Незачто", TextToSpeech.QUEUE_FLUSH, null);

                         }else
                         if(Data.cmd_clas[i].equals("timer")){
                             mTTS.speak("Скажите Пуск, чтобы запустить таймер", TextToSpeech.QUEUE_FLUSH, null);
                             final AlertDialog.Builder alertB = new AlertDialog.Builder(MatrX.this);

                             LinearLayout ln = new LinearLayout(MatrX.this);
                             TextView txt = new TextView(MatrX.this);
                             txt.setText("00:00");
                             txt.setTextSize(30);
                             ln.addView(txt);
                             alertB.setView(ln);
                             alertB.setCancelable(true)
                                     .setPositiveButton("Старт", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {
                                             long sec = 0;
                                             boolean timerRunning = false;


                                         }
                                     });

                             AlertDialog alert = alertB.create();
                             alert.setTitle("Timer");
                             alert.show();
                         }
                }
            }
        }
    }
}
