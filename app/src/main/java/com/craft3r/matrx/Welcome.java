package com.craft3r.matrx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Welcome extends AppCompatActivity {

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

    private SharedPreference sharedPreferenceObj;

    private MotionLayout ml;

    public static boolean IsFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sharedPreferenceObj = new SharedPreference(Welcome.this);


        if (sharedPreferenceObj.getApp_runFirst().equals("FIRST")) {
            IsFirst = true;
            HalfScreen.Enable = false;
            sharedPreferenceObj.setApp_runFirst("NO");
        }
        if (!IsFirst) {
            finish();
            startActivity(new Intent(Welcome.this, MainActivity.class));
            // App is not First Time Launch
        }
        ml = (MotionLayout) findViewById(R.id.ml);



    }

    public void st(View v){
        finish();
        startActivity(new Intent(Welcome.this, welcome2.class));
    }


}