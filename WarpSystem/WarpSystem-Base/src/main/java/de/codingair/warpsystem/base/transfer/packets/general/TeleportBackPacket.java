package de.codingair.warpsystem.base.transfer.packets.general;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportBackPacket implements RequestPacket<BooleanPacket> {
    private String name;
    private boolean bypassCooldownCheck;

    public TeleportBackPacket() {
    }

    public TeleportBackPacket(String name, boolean bypassCooldownCheck) {
        this.name = name;
        this.bypassCooldownCheck = bypassCooldownCheck;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        out.writeBoolean(this.bypassCooldownCheck);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.bypassCooldownCheck = in.readBoolean();
    }

    public String getName() {
        return name;
    }

    public boolean isBypassCooldownCheck() {
        return bypassCooldownCheck;
    }
}
