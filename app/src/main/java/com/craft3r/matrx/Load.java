package com.craft3r.matrx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class Load extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        startService(new Intent(Load.this, Loading.class));
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
            new LongOperation().execute("");
        }

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
                if (Loading.CanDoNext) {
                    startActivity(new Intent(Load.this, Welcome.class));
                    finish();
                    stopService(new Intent(Load.this, Loading.class));
                } else {
                    new LongOperation().execute("");
                }
            return null;
        }
    }
}