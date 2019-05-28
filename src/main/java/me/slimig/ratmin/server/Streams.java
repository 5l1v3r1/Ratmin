package me.slimig.ratmin.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Streams {
    private PrintWriter writer;
    private BufferedReader reader;

    public Streams(Socket socket) throws IOException {
        if (socket == null) throw new IllegalArgumentException();
        writer = new PrintWriter(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public boolean sendMsg(String msg){
        try {
            writer.println(msg);
            writer.flush();
        }
        catch (Exception e) { return false; }
        return true;
    }

    public String readMsg(){
        try {
            String msg = reader.readLine();
            return msg;
        }
        catch (Exception e) { return null; }
    }
}
