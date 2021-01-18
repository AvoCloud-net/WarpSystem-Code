package de.codingair.warpsystem.core.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.warpsystem.core.transfer.utils.serializeable.ServerOptions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendOptionsPacket implements Packet {
    private ServerOptions options;

    public SendOptionsPacket() {
    }

    public SendOptionsPacket(ServerOptions options) {
        this.options = options;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        options.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        options = new ServerOptions();
        options.read(in);
    }

    public ServerOptions getOptions() {
        return options;
    }
}
