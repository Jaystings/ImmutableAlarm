package com.example.immutablealarm;

import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;

public class MainActivity extends AppCompatActivity {

    ToneGenerator tg;

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

    }

    public void TestBeep(View view){
        tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
                ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    }
}