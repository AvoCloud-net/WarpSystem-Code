package de.codingair.warpsystem.core.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DeleteGlobalWarpPacket implements RequestPacket<BooleanPacket> {
    private String warp;

    public DeleteGlobalWarpPacket() {
    }

    public DeleteGlobalWarpPacket(String warp) {
        this.warp = warp;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.warp);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.warp = in.readUTF();
    }

    public String getWarp() {
        return warp;
    }
}
