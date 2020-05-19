package com.example.karis.projproj;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * ABOUT THE ACTIVITY
 * Get Started Screen
 */

public class StartActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        button = (Button) findViewById(R.id.button_startActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanActivity();
            }
        });

    }

    public void openScanActivity(){
        Intent openScan = new Intent(this, ScanActivity.class);
        startActivity(openScan);
    }
}
