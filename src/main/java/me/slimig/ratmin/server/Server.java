package me.slimig.ratmin.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket server;
    private ConcurrentHashMap <Socket, Streams> dialog = new ConcurrentHashMap <Socket, Streams>();
    private ThreadPool tp;
    private boolean running = false;

    public Server(int port) throws IOException{
        server = new ServerSocket(port);
        tp = new ThreadPool(server, dialog);
    }

    public boolean isRunning(){
        return running;
    }

    public void startServer(){
        if (isRunning()) throw new IllegalStateException("Server already running");
        running = true;
        tp.start();
    }

    public void stopServer() throws IOException{
        if (!isRunning()) throw new IllegalStateException("Server already idle");
        running = false;
        server.close();
        tp.shutdownNow();
    }

    public ConcurrentHashMap <Socket, Streams> getMap(){
        return dialog;
    }
}
