package de.codingair.warpsystem.core.transfer.packets.general;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.ByteMask;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UpdatePlayerDataPacket implements Packet {
    private final ByteMask updated = new ByteMask();
    private final ByteMask values = new ByteMask();
    private String name;
    private String server;
    private String oldServer;

    public UpdatePlayerDataPacket() {
    }

    public UpdatePlayerDataPacket(String name) {
        this.name = name;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        this.updated.write(out);
        this.values.write(out);

        if (this.updated.getBit(1)) {
            out.writeUTF(this.server);
            out.writeUTF(this.oldServer);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.updated.read(in);
        this.values.read(in);

        if (this.updated.getBit(1)) {
            this.server = in.readUTF();
            this.oldServer = in.readUTF();
        }
    }

    public UpdatePlayerDataPacket setVanished(boolean vanished) {
        this.updated.setBit(0, true);
        this.values.setBit(0, vanished);
        return this;
    }

    public UpdatePlayerDataPacket setServer(String server, String from) {
        this.updated.setBit(1, true);
        this.server = server;
        this.oldServer = from;
        return this;
    }

    public boolean update(PlayerData data) {
        if (updated.getBit(0)) data.setVanished(this.values.getBit(0));
        if (updated.getBit(1)) {
            data.setServer(this.server);
            data.setOldServer(this.oldServer);
        }
        return updated.getByte() != 0;
    }

    public String getName() {
        return name;
    }
}
