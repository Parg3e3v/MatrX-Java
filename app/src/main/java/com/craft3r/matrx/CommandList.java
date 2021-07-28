package com.craft3r.matrx;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommandList extends AppCompatActivity {

    private LinearLayout line;
    private TextView versText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_list);


        line = (LinearLayout)findViewById(R.id.list);
        versText = (TextView)findViewById(R.id.textView5);

        versText.setText("v." + Loading.versNow);

        for( int i = 0; i < Data.commands.length; i++ )
        {
            String tempTxt;
            tempTxt = "• ";
            final TextView textView = new TextView(this);
            for (int a = 0; a < Data.commands[i].length; a++){
                tempTxt += Data.commands[i][a];
                if(a != Data.commands[i].length - 1) {
                    tempTxt += ", ";
                }
            }
            textView.setText(tempTxt);
            textView.setBackgroundColor(Color.parseColor("#800099FF"));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(30,10,30,10);
            textView.setLayoutParams(params);

            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.setBackgroundColor(Color.parseColor("#8000CCFF"));
                    AlertDialog.Builder a_builder = new AlertDialog.Builder(CommandList.this);
                    a_builder.setMessage(Data.msges[finalI])
                            .setCancelable(true);

                    AlertDialog alert = a_builder.create();
                    alert.setTitle("Инфо");
                    alert.show();
                    alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            textView.setBackgroundColor(Color.parseColor("#800099FF"));
                        }
                    });

                }

            });

            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            textView.setTextColor(ColorStateList.valueOf(Color.rgb(0, 212, 245)));
            line.addView(textView);
        }
    }
}
