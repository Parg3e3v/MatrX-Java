package com.craft3r.matrx;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class QuickNeedChecker extends AppCompatActivity {

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_need_checker);

        if (isAppRunning(QuickNeedChecker.this, "com.craft3r.matrx")){
            Toast.makeText(QuickNeedChecker.this, "dsfsdfs", Toast.LENGTH_LONG);
        }else{
            startActivity(new Intent(QuickNeedChecker.this, HalfScreen.class));
            finish();

        }

    }
}