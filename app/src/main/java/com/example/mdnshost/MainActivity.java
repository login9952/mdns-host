package com.example.mdnshost;
import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.content.SharedPreferences;

public class MainActivity extends Activity {

    private TextView statusText;
    private EditText hostnameInput;
    private EditText portInput;
    private SharedPreferences preferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        hostnameInput = findViewById(R.id.hostnameInput);
        portInput = findViewById(R.id.portInput);
        preferences = getSharedPreferences("mdns_config", MODE_PRIVATE);
        String savedHost =
        preferences.getString("hostname","phone");

        hostnameInput.setText(savedHost);
        
        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

       

        statusText.setText(
                "mDNS Host\n已停止"
        );

        stopButton.setOnClickListener(v -> {

    Intent serviceIntent =
            new Intent(this, MdnsForegroundService.class);

    stopService(serviceIntent);


    statusText.setText(
            "mDNS Host\n已停止"
    );
});
    }

}
