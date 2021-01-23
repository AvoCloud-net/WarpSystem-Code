package de.codingair.warpsystem.core.transfer.utils;

import de.codingair.packetmanagement.utils.ByteMask;
import de.codingair.packetmanagement.utils.Serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerData implements Serializable {
    private String name;
    private UUID id;

    private String oldServer;
    private String server;
    private boolean firstServer;
    private boolean vanished;

    public PlayerData() {
    }

    public PlayerData(String name, UUID id, String server, boolean firstServer) {
        this.name = name;
        this.id = id;
        this.server = server;
        this.firstServer = firstServer;
        this.vanished = false;
    }

    public PlayerData(String name, UUID id, String server, String oldServer, boolean firstServer) {
        this.name = name;
        this.id = id;
        this.oldServer = oldServer;
        this.server = server;
        this.firstServer = firstServer;
    }

    public PlayerData(String name, UUID id) {
        this(name, id, null, false);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {

        out.writeUTF(this.name);
        out.writeLong(this.id.getMostSignificantBits());
        out.writeLong(this.id.getLeastSignificantBits());

        out.writeUTF(this.server);

        ByteMask mask = new ByteMask();
        mask.setBit(0, vanished);
        mask.setBit(1, oldServer != null);
        mask.setBit(2, firstServer);
        mask.write(out);

        if(oldServer != null) out.writeUTF(oldServer);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.id = new UUID(in.readLong(), in.readLong());

        this.server = in.readUTF();

        ByteMask mask = new ByteMask();
        mask.read(in);
        this.vanished = mask.getBit(0);
        if(mask.getBit(1)) this.oldServer = in.readUTF();
        this.firstServer = mask.getBit(2);
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

    public String getOldServer() {
        return oldServer;
    }

    public void setOldServer(String oldServer) {
        this.oldServer = oldServer;
    }

    public boolean isVanished() {
        return vanished;
    }

    public PlayerData setVanished(boolean vanished) {
        this.vanished = vanished;
        return this;
    }

    public boolean isFirstServer() {
        return firstServer;
    }

    public void setFirstServer(boolean firstServer) {
        this.firstServer = firstServer;
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", oldServer='" + oldServer + '\'' +
                ", server='" + server + '\'' +
                ", firstServer=" + firstServer +
                ", vanished=" + vanished +
                '}';
    }
}
