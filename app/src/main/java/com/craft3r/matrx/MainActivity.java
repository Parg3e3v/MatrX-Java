package com.craft3r.matrx;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText login, password;
    private TextView error;
    private Button but;

    public static boolean IsHalfNeeded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(IsHalfNeeded) {
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, HalfScreen.class));
            IsHalfNeeded = false;
        }

        login = (EditText)findViewById(R.id.log_in);
        password = (EditText)findViewById(R.id.pass_in);
        error = (TextView)findViewById(R.id.error);
        but = (Button)findViewById(R.id.lg_button);
        LoadUrl();
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHtmlFromWeb();
            }
        });

    }

    private void LoadUrl(){
        if(Loading.vers != null) {
            if (Loading.NeedDownload) {
                final AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
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
                                stopService(new Intent(MainActivity.this, Loading.class));
                                System.exit(0);
                            }
                        });

                AlertDialog alert = a_builder.create();
                alert.setTitle("Доступно обновление");
                alert.show();
            } else {
                if(!IsHalfNeeded)
                    Toast.makeText(MainActivity.this, "Welcome to MatrX!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "PING is too big", Toast.LENGTH_SHORT).show();
//            finish();
        }

    }


    private void getHtmlFromWeb() {

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final StringBuilder stringBuilder = new StringBuilder();

                    ArrayList<String> downServers = new ArrayList<>();
                    Element table = Loading.doc.select("table").get(0);
                    Elements rows = table.select("tr");
                    Elements texts = rows.select("td");
                    String gg = "";
                    for (int i = 0; i < texts.size(); i += 2) {
                        Element cols = texts.get(i);
                        if (cols.text().equals(String.valueOf(login.getText()))) {
                            for (int a = 1; a < texts.size(); a += 2) {
                                Element colsPass = texts.get(a);

                                if (colsPass.text().equals(String.valueOf(password.getText())) && cols.attr("id").equals(colsPass.attr("id"))) {
                                    error.setText("");
                                    Intent intent = new Intent(MainActivity.this, MatrX.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    try {
                                        Thread.sleep(1500);
                                    } catch (Exception e) {

                                    }
                                    error.setText("Invalid login or password");

                                }
                            }
                        } else {
                            try {
                                Thread.sleep(1500);
                            } catch (Exception e) {

                            }
                            error.setText("Invalid login or password");

                        }

                    }


                }
            }).start();
        }catch (java.lang.NullPointerException e){
            Toast.makeText(MainActivity.this, "Your version of Matrix is not latest", Toast.LENGTH_LONG).show();
        }
    }
}
