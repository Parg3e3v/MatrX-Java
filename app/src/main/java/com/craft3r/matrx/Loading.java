package com.craft3r.matrx;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Loading extends Service {

    public static Document doc;
    public static Document docInfo;

    public static String vers;
    public static String versNow = "0.1.1";
    public static String url;

    public static boolean CanDoNext = false;
    public static boolean InternetError = false;


    public static boolean NeedDownload = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    doc = Jsoup.connect("https://matrx.herokuapp.com/db.html").get();
                    docInfo = Jsoup.connect("https://matrx.herokuapp.com/").get();

                    ArrayList<String> downServers = new ArrayList<>();
                    Element dive = docInfo.select(".versionApk").get(0);
                    Element divURL = docInfo.selectFirst(".urlApk");
                    Elements a_ = divURL.select("a");
                    url = a_.text();
                    Elements p = dive.select("p");
                    Elements a = p.select("a");

                    vers = a.text();
                    CanDoNext = true;
                    if (vers.equals(versNow)){
                        NeedDownload = false;

                    }else{
                        NeedDownload = true;
                    }

                } catch (IOException e) {
                    stringBuilder.append("Error : ").append(e.getMessage()).append("\n");
                    InternetError = true;
                }

            }
        }).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        MainActivity.NeedDownload = false;
    }
}