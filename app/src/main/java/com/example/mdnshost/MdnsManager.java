package com.example.mdnshost;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

public class MdnsManager {
    private String hostName = "phone";
    private int servicePort = 80;

    public void setHostName(String hostName) {
        if (hostName == null || hostName.trim().isEmpty()) {
            this.hostName = "phone";
            return;
        }
        String name = hostName.trim().toLowerCase();
        if (name.endsWith(".local")) {
            name = name.substring(0, name.length() - 6);
        }
        this.hostName = name;
    }

    public void setPort(int port) {
        if (port >= 1 && port <= 65535) {
            this.servicePort = port;
        } else {
            this.servicePort = 80;
        }
    }
}
