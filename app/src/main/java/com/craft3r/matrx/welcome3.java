package com.craft3r.matrx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

public class welcome3 extends AppCompatActivity {

    private MotionLayout ml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome3);

        ml = (MotionLayout)findViewById(R.id.wl3);

        ml.setTransition(R.id.start_wl3, R.id.end_wl3);
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ml.setTransition(R.id.end_wl3, R.id.buttons_wl3);
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

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }
        });

    }

    public void nxt(View v){
        Welcome.IsFirst = false;
        finish();
        startActivity(new Intent(welcome3.this, Load.class));
    }

    public void prev(View v){
        finish();
        startActivity(new Intent(welcome3.this, welcome2.class));
    }

    public void click(View v){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$ManageAssistActivity"));
        Toast.makeText(getApplicationContext(), "нажмите на 'вспомогательные приложения' и выберите Матрицу", Toast.LENGTH_LONG).show();
//        MatrX.mTTS.speak("нажмите на 'вспомогательные приложения' и выберите Матрицу", TextToSpeech.QUEUE_FLUSH, null);
        startActivity(intent);
    }
}