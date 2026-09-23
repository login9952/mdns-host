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

            boolean success = mdnsManager.start();

            if (!success) {

                statusText.setText(
                        "mDNS Host\n启动失败\n\n" +
                        "请查看日志"
                );
            } else {

                statusText.setText(
                        "mDNS Host\n正在启动..."
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
