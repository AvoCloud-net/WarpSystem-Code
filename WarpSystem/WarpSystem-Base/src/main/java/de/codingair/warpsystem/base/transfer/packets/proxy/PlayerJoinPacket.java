package de.codingair.warpsystem.base.transfer.packets.proxy;

import de.codingair.packetmanagement.packets.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerJoinPacket implements Packet {
    private String player;
    private UUID id;

    public PlayerJoinPacket() {
    }

    public PlayerJoinPacket(String player, UUID id) {
        this.player = player;
        this.id = id;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(player);
        out.writeLong(id.getMostSignificantBits());
        out.writeLong(id.getLeastSignificantBits());
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.id = new UUID(in.readLong(), in.readLong());
    }

    public String getPlayer() {
        return player;
    }

    public UUID getId() {
        return id;
    }
}
