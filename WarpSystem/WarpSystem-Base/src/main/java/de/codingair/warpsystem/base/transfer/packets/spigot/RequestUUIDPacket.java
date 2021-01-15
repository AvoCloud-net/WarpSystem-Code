package de.codingair.warpsystem.base.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.SendUUIDPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestUUIDPacket implements RequestPacket<SendUUIDPacket> {
    private String player;

    public RequestUUIDPacket() {
    }

    public RequestUUIDPacket(String player) {
        this.player = player;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }
}
