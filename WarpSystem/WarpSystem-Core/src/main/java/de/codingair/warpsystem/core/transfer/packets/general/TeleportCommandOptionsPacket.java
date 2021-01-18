package de.codingair.warpsystem.core.transfer.packets.general;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.warpsystem.core.transfer.utils.TeleportCommandOptions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportCommandOptionsPacket implements Packet {
    private String server;
    private TeleportCommandOptions options = new TeleportCommandOptions();

    public TeleportCommandOptionsPacket() {
    }

    public TeleportCommandOptionsPacket(boolean back, boolean tp, boolean tpAll, boolean tpToggle, boolean tpa, boolean tpaHere, boolean tpaAll, boolean tpaToggle) {
        options.setBit(0, back);
        options.setBit(1, tp);
        options.setBit(2, tpAll);
        options.setBit(3, tpToggle);
        options.setBit(4, tpa);
        options.setBit(5, tpaHere);
        options.setBit(6, tpaAll);
        options.setBit(7, tpaToggle);
    }

    public TeleportCommandOptionsPacket(String server, TeleportCommandOptions options) {
        this.server = server;
        this.options = options;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(this.server != null);
        if (this.server != null) out.writeUTF(this.server);
        this.options.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        if (in.readBoolean()) this.server = in.readUTF();
        this.options.read(in);
    }

    public String getServer() {
        return server;
    }

    public void setServer(String name) {
        this.server = name;
    }

    public TeleportCommandOptions getOptions() {
        return options;
    }
}
