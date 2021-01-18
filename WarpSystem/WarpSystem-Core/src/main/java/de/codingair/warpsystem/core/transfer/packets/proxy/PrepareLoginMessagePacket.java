package de.codingair.warpsystem.core.transfer.packets.proxy;

import de.codingair.packetmanagement.packets.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareLoginMessagePacket implements Packet {
    private String player;
    private String message;

    public PrepareLoginMessagePacket() {
    }

    public PrepareLoginMessagePacket(String player, String message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(player);
        out.writeBoolean(message != null);
        if (message != null) out.writeUTF(message);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        player = in.readUTF();
        if (in.readBoolean()) message = in.readUTF();
        else message = null;
    }

    public String getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }
}
