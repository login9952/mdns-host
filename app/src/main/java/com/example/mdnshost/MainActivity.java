package com.example.mdnshost;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.content.SharedPreferences;

public class MainActivity extends Activity {

    private MdnsManager mdnsManager;
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

        mdnsManager = new MdnsManager(this);

        // 接收 mDNS 实际运行状态
        mdnsManager.setStatusListener(new MdnsManager.StatusListener() {

            @Override
            public void onRegistered(String hostname) {

                runOnUiThread(() -> {

                    statusText.setText(
                            "mDNS Host\n" +
                            "已运行\n\n" +
                            hostname + ".local"
                    );

                });
            }

            @Override
            public void onRegistrationFailed(int errorCode) {

                runOnUiThread(() -> {

                    statusText.setText(
                            "mDNS Host\n" +
                            "启动失败\n\n" +
                            "错误代码：" + errorCode
                    );

                });
            }
        });

        statusText.setText(
                "mDNS Host\n已停止"
        );

        startButton.setOnClickListener(v -> {
            
            String hostname = hostnameInput.getText().toString().trim();
            preferences.edit()
                        .putString("hostname", hostname)
                        .apply();
            
            mdnsManager.setHostName(hostname);

            Intent serviceIntent =
            new Intent(this, MdnsForegroundService.class);

            startForegroundService(serviceIntent);
            
            boolean success = mdnsManager.start();

            if (!success) {

                statusText.setText(
                        "mDNS Host\n启动失败\n\n" +
                        "请查看日志"
                );
            } else {

                statusText.setText(
                        "mDNS Host\n正在启动...\n\n" +
                         "IP：" + mdnsManager.getCurrentIpAddress()
                );
            }
        });

        stopButton.setOnClickListener(v -> {

            mdnsManager.stop();

            statusText.setText(
                    "mDNS Host\n已停止"
            );
        });
    }

    @Override
    protected void onDestroy() {

        if (mdnsManager != null) {
            mdnsManager.stop();
        }

        super.onDestroy();
    }
}
