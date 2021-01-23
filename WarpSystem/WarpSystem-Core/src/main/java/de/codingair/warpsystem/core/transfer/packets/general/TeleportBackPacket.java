package de.codingair.warpsystem.core.transfer.packets.general;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.BytePacket;
import de.codingair.packetmanagement.utils.ByteMask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportBackPacket implements RequestPacket<BytePacket> {
    private String name;
    private boolean bypassCooldownCheck;
    private boolean switching;

    public TeleportBackPacket() {
    }

    public TeleportBackPacket(String name, boolean bypassCooldownCheck) {
        this.name = name;
        this.bypassCooldownCheck = bypassCooldownCheck;
    }

    public TeleportBackPacket(String name, boolean bypassCooldownCheck, boolean switching) {
        this.name = name;
        this.bypassCooldownCheck = bypassCooldownCheck;
        this.switching = switching;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);

        ByteMask mask = new ByteMask();
        mask.setBit(0, this.bypassCooldownCheck);
        mask.setBit(1, this.switching);
        mask.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();

        ByteMask mask = new ByteMask();
        mask.read(in);

        this.bypassCooldownCheck = mask.getBit(0);
        this.switching = mask.getBit(1);
    }

    public String getName() {
        return name;
    }

    public boolean isBypassCooldownCheck() {
        return bypassCooldownCheck;
    }

    public boolean isSwitching() {
        return switching;
    }

    public enum Result {
        SUCCESS,
        SERVER_NOT_AVAILABLE,
        PLAYER_NOT_AVAILABLE,
        NO_LAST_POSITION;

        public static Result fromId(byte id) {
            if (id < 0) return null;
            Result[] r = Result.values();
            if(id >= r.length) return null;
            else return r[id];
        }

        public byte id() {
            return (byte) ordinal();
        }
    }
}
