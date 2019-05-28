package me.slimig.ratmin.server.threads;

import java.io.IOException;
import java.net.URISyntaxException;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import me.slimig.ratmin.user_interface.Ratmin;
import me.slimig.ratmin.utils.Packets.Packet;

public class PingCheck extends Thread {
    private TimeHelper th;

    public PingCheck(String name) {
        th = new TimeHelper();
    }

    public void run() {
        // System.out.println("Running " + threadName);
        while (Thread.currentThread().isAlive()) {
            if (th.hasReached(5000)) {
                try {
                    if (Ratmin.selserver != null) {
                        if (Ratmin.selserver.isRunning())
                            if (Packet.isAbleToSend())
                                Ratmin.ping(Ratmin.selserver);
                    }
                } catch (IOException | GeoIp2Exception e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                th.reset();
            }
        }
    }
}