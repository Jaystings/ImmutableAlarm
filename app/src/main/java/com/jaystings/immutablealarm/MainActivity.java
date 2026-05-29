package com.jaystings.immutablealarm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

    int hours;
    int minutes;
    int seconds;

    private TimerService timerService;
    private boolean isBound = false;



    final Handler handler = new Handler(Looper.getMainLooper());
    Runnable tick = new Runnable() {
        @Override
        public void run() {
            // Code to execute every second: Tick the timer
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

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getService();
            isBound = true;

            // Pass and run your existing Runnable inside the Service context
            timerService.startTask(tick);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
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

        // Start and Bind to the service so the tick runnable runs in background while app is open,
        // even when the phone is locked or other apps are being used
        Intent intent = new Intent(this, TimerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    public void TestBeep(View view){
        TestBeep();
    }

    public void TestBeep(){
        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
                ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    }
    public void getNumber(char hms) throws Exception{
        if (hms == 'h'){
            hours = Integer.parseInt(((EditText) findViewById(R.id.txtHours)).getText().toString());
        } else if (hms == 'm'){
            minutes = Integer.parseInt(((EditText) findViewById(R.id.txtMinutes)).getText().toString());
        } else if (hms == 's'){
            seconds = Integer.parseInt(((EditText) findViewById(R.id.txtSeconds)).getText().toString());
        }
    }

    public void StartTimer(View view){

        try{
            getNumber('h');
        } catch (Exception e) {
            txtHours.setText("0");
            hours = 0;
        }
        try{
            getNumber('m');
        } catch (Exception e) {
            txtMinutes.setText("0");
            minutes = 0;
        }
        try{
            getNumber('s');
        } catch (Exception e) {
            txtSeconds.setText("0");
            seconds = 0;
        }
        if(isRunning) {
            isRunning = false;
            btnStart.setText(R.string.engStart);
            EnableInputs();
        } else {
            isRunning = true;
            btnStart.setText(R.string.engPause);
            DisableInputs();
        }
    }
    public void Reset(View v){
        isRunning = false;
        EnableInputs();
        txtHours.setText("0");
        txtMinutes.setText("0");
        txtSeconds.setText("0");
        btnStart.setText(R.string.engStart);

    }
    public void EnableInputs(){
        txtHours.setEnabled(true);
        txtMinutes.setEnabled(true);
        txtSeconds.setEnabled(true);
    }
    public void DisableInputs(){
        txtHours.setEnabled(false);
        txtMinutes.setEnabled(false);
        txtSeconds.setEnabled(false);
    }
}