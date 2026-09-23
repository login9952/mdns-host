package com.example.mdnshost;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private MdnsManager mdnsManager;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        mdnsManager = new MdnsManager(this);

        statusText.setText("mDNS Host\n已停止");

        startButton.setOnClickListener(v -> {

            boolean success = mdnsManager.start();

            if (success) {
                statusText.setText(
                        "mDNS Host\n正在启动...\n\n" +
                        "phone.local\n→ 192.168.1.61"
                );
            } else {
                statusText.setText(
                        "mDNS Host\n启动失败\n\n" +
                        "请查看后续测试结果"
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
