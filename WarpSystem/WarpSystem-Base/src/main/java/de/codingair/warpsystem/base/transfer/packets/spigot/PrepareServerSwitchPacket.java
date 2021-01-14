package de.codingair.warpsystem.base.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareServerSwitchPacket implements RequestPacket<IntegerPacket> {
    private String player;
    private String server;
    private String message = null;
    private boolean ignoreLimit = false;

    public PrepareServerSwitchPacket() {
    }

    public PrepareServerSwitchPacket(String player, String server) {
        this.player = player;
        this.server = server;
    }

    public PrepareServerSwitchPacket(String player, String server, String message, boolean ignoreLimit) {
        this.player = player;
        this.server = server;
        this.message = message;
        this.ignoreLimit = ignoreLimit;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(player);
        out.writeUTF(server);

        byte options = (byte) (message != null ? 1 : 0);
        if (ignoreLimit) options |= 1 << 1;
        out.writeByte(options);

        if (message != null) out.writeUTF(message);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        player = in.readUTF();
        server = in.readUTF();

        byte options = in.readByte();
        if ((options & 1) != 0) message = in.readUTF();
        this.ignoreLimit = (options & (1 << 1)) != 0;
    }

    public String getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public String getMessage() {
        return message;
    }

    public boolean isIgnoreLimit() {
        return ignoreLimit;
    }
}
