package com.example.mdnshost;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;

public class MdnsManager {

    public interface StatusListener {
    void onRegistered(String hostname);
    void onRegistrationFailed(int errorCode);
}
    
    private static final String HOST_NAME = "phone";

    private final NsdManager nsdManager;
    private NsdManager.RegistrationListener registrationListener;
    private boolean running = false;

    private StatusListener statusListener;

public void setStatusListener(StatusListener listener) {
    this.statusListener = listener;
}
    
private final Context context;

public MdnsManager(Context context) {

    this.context = context;

    nsdManager =
            (NsdManager) context.getSystemService(Context.NSD_SERVICE);
}

    public boolean start() {

        if (running) {
            return true;
        }

        try {
            // 尝试解除 Android Hidden API 限制
            tryEnableHiddenApis();

            NsdServiceInfo serviceInfo = new NsdServiceInfo();

            serviceInfo.setServiceName(HOST_NAME);
            serviceInfo.setServiceType("_http._tcp");
            serviceInfo.setPort(80);

            // 设置 mDNS 主机名
            Method setHostname =
                    NsdServiceInfo.class.getDeclaredMethod(
                            "setHostname",
                            String.class
                    );

            setHostname.setAccessible(true);
            setHostname.invoke(serviceInfo, HOST_NAME);

            // 设置主机 IP 地址
            InetAddress address = getCurrentWifiAddress();

            if (address == null) {
                throw new IllegalStateException(
                        "没有找到当前 Wi-Fi IPv4 地址"
                );
            }

            ArrayList<InetAddress> addresses =
                    new ArrayList<>();

            addresses.add(address);

            Method setHostAddresses =
        NsdServiceInfo.class.getDeclaredMethod(
                "setHostAddresses",
                java.util.List.class
        );

            setHostAddresses.setAccessible(true);
            setHostAddresses.invoke(serviceInfo, addresses);

            registrationListener =
                    new NsdManager.RegistrationListener() {

                      @Override
                      public void onServiceRegistered(
                             NsdServiceInfo serviceInfo) {

                        running = true;

                        System.out.println(
                                "MDNS_REGISTERED: "
                                        + serviceInfo.getServiceName()
                        );

                        if (statusListener != null) {
                            statusListener.onRegistered(
                                    serviceInfo.getServiceName()
                            );
                       }
                     }

                     @Override
                     public void onRegistrationFailed(
                            NsdServiceInfo serviceInfo,
                            int errorCode) {

                         running = false;

                         System.out.println(
                                 "MDNS_REGISTRATION_FAILED: "
                                         + errorCode
                         );

                        if (statusListener != null) {
                            statusListener.onRegistrationFailed(errorCode);
                         }
                    }

                        @Override
                        public void onServiceUnregistered(
                                NsdServiceInfo serviceInfo) {

                            running = false;
                        }

                        @Override
                        public void onUnregistrationFailed(
                                NsdServiceInfo serviceInfo,
                                int errorCode) {
                        }
                    };

            nsdManager.registerService(
                    serviceInfo,
                    NsdManager.PROTOCOL_DNS_SD,
                    registrationListener
            );

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            running = false;
            return false;
        }
    }

    public void stop() {

        if (!running || registrationListener == null) {
            return;
        }

        try {
            nsdManager.unregisterService(registrationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        running = false;
        registrationListener = null;
    }

    public boolean isRunning() {
        return running;
    }

private InetAddress getCurrentWifiAddress() {

    ConnectivityManager connectivityManager =
            (ConnectivityManager)
                    context.getSystemService(
                            Context.CONNECTIVITY_SERVICE
                    );

    Network[] networks =
            connectivityManager.getAllNetworks();

    for (Network network : networks) {

        NetworkCapabilities capabilities =
                connectivityManager.getNetworkCapabilities(
                        network
                );

        if (capabilities == null) {
            continue;
        }

        if (!capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
        )) {
            continue;
        }

        LinkProperties linkProperties =
                connectivityManager.getLinkProperties(
                        network
                );

        if (linkProperties == null) {
            continue;
        }

        for (LinkAddress linkAddress :
                linkProperties.getLinkAddresses()) {

            InetAddress address =
                    linkAddress.getAddress();

            if (address instanceof
                    java.net.Inet4Address) {

                return address;
            }
        }
    }

    return null;
}
    private void tryEnableHiddenApis() {

        try {

            Class<?> vmRuntime =
                    Class.forName("dalvik.system.VMRuntime");

            Method getRuntime =
                    vmRuntime.getDeclaredMethod("getRuntime");

            Object runtime =
                    getRuntime.invoke(null);

            Method setHiddenApiExemptions =
                    vmRuntime.getDeclaredMethod(
                            "setHiddenApiExemptions",
                            String[].class
                    );

            setHiddenApiExemptions.setAccessible(true);

            setHiddenApiExemptions.invoke(
                    runtime,
                    (Object) new String[]{"L"}
            );

        } catch (Exception e) {

            // 如果系统禁止，则继续运行。
            // 后续通过实际设备测试判断。
            e.printStackTrace();
        }
    }
}
