package com.jaystings.immutablealarm;

import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;

public class MainActivity extends AppCompatActivity {

    ToneGenerator tg;
    boolean isRunning = false;
    Button btnStart;

    TextView txtHours;
    TextView txtMinutes;
    TextView txtSeconds;


    final Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Code to execute every second:

            if(isRunning) {
                Log.d("Timer", "Tick");
                if (txtHours.getText().toString().equals("0")
                        && txtMinutes.getText().toString().equals("0")
                        && txtSeconds.getText().toString().equals("0")){
                    TestBeep();
                } else if (txtMinutes.getText().toString().equals("0")
                        && txtSeconds.getText().toString().equals("0")) {
                    txtHours.setText(String.valueOf(Integer.parseInt(txtHours.getText().toString()) - 1));
                    txtMinutes.setText("59");
                    txtSeconds.setText("59");
                } else if (txtSeconds.getText().toString().equals("0")) {
                    txtMinutes.setText(String.valueOf(Integer.parseInt(txtMinutes.getText().toString()) - 1));
                    txtSeconds.setText("59");
                } else {
                    txtSeconds.setText(String.valueOf(Integer.parseInt(txtSeconds.getText().toString()) - 1));
                }
            }

            // Repeat this runnable again after 1 second
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize custom variables
        tg = new ToneGenerator(ToneGenerator.TONE_DTMF_4, 100);
        btnStart = findViewById(R.id.btnStart);
        txtHours = findViewById(R.id.txtHours);
        txtMinutes = findViewById(R.id.txtMinutes);
        txtSeconds = findViewById(R.id.txtSeconds);

        handler.postDelayed(runnable, 1000);

    }

    public void TestBeep(View view){
        TestBeep();
    }

    public void TestBeep(){
        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
                ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    }

    public void StartTimer(View view){
        int hours = Integer.parseInt(((EditText)findViewById(R.id.txtHours)).getText().toString());
        int minutes = Integer.parseInt(((EditText)findViewById(R.id.txtMinutes)).getText().toString());
        int seconds = Integer.parseInt(((EditText)findViewById(R.id.txtSeconds)).getText().toString());
        if(isRunning) {
            isRunning = false;
            btnStart.setText(R.string.engStart);
        } else {
            isRunning = true;
            btnStart.setText(R.string.engPause);
        }
    }

    public void Reset(View v){
        isRunning = false;
        btnStart.setText(R.string.engStart);

    }
}