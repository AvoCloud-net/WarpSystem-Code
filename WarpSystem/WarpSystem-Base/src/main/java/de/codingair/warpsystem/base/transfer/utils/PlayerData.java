package de.codingair.warpsystem.base.transfer.utils;

import de.codingair.packetmanagement.utils.ByteMask;
import de.codingair.packetmanagement.utils.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerData implements Serializable {
    private String name;
    private UUID id;

    private String server;
    private boolean vanished;

    public PlayerData() {
    }

    public PlayerData(String name, UUID id, String server) {
        this.name = name;
        this.id = id;
        this.server = server;
        this.vanished = false;
    }

    public PlayerData(String name, UUID id) {
        this(name, id, null);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        out.writeLong(this.id.getMostSignificantBits());
        out.writeLong(this.id.getLeastSignificantBits());

        out.writeUTF(this.server);

        ByteMask mask = new ByteMask();
        mask.setBit(0, vanished);
        mask.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.id = new UUID(in.readLong(), in.readLong());

        this.server = in.readUTF();

        ByteMask mask = new ByteMask();
        mask.read(in);
        this.vanished = mask.getBit(0);
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public String getServer() {
        return server;
    }

    public PlayerData setServer(String server) {
        this.server = server;
        return this;
    }

    public boolean isVanished() {
        return vanished;
    }

    public PlayerData setVanished(boolean vanished) {
        this.vanished = vanished;
        return this;
    }
}
