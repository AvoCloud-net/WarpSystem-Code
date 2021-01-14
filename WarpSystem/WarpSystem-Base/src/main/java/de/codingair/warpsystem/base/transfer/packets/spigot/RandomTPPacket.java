package de.codingair.warpsystem.base.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RandomTPPacket implements RequestPacket<BooleanPacket> {
    private String player, server, world;
    private boolean byOther;

    public RandomTPPacket() {
    }

    public RandomTPPacket(String player, String server, String world, boolean byOther) {
        this.player = player;
        this.server = server;
        this.world = world;
        this.byOther = byOther;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte options = (byte) (world != null ? 1 : 0);

        out.writeByte(options);
        out.writeUTF(this.player);
        out.writeUTF(this.server);
        if (world != null) out.writeUTF(this.world);
        out.writeBoolean(byOther);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte options = in.readByte();

        this.player = in.readUTF();
        this.server = in.readUTF();
        if ((options & 1) != 0) this.world = in.readUTF();
        this.byOther = in.readBoolean();
    }

    public String getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getWorld() {
        return world;
    }

    public boolean isByOther() {
        return byOther;
    }
}
