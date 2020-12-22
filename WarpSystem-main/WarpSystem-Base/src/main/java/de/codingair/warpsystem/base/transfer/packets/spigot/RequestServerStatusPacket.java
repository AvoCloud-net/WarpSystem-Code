package de.codingair.warpsystem.base.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestServerStatusPacket implements RequestPacket<BooleanPacket> {
    private String server;

    public RequestServerStatusPacket() {
    }

    public RequestServerStatusPacket(String server) {
        this.server = server;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(server);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        server = in.readUTF();
    }

    public String getServer() {
        return server;
    }
}
