package de.codingair.warpsystem.base.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.warpsystem.base.transfer.utils.serializeable.SGlobalWarp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PublishGlobalWarpPacket implements RequestPacket<BooleanPacket> {
    public SGlobalWarp warp;
    private boolean overwrite = false;

    public PublishGlobalWarpPacket() {
    }

    public PublishGlobalWarpPacket(SGlobalWarp warp) {
        this.warp = warp;
    }

    public PublishGlobalWarpPacket(SGlobalWarp warp, boolean overwrite) {
        this.warp = warp;
        this.overwrite = overwrite;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        this.warp.write(out);
        out.writeBoolean(overwrite);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.warp = new SGlobalWarp();
        this.warp.read(in);
        this.overwrite = in.readBoolean();
    }

    public boolean isOverwrite() {
        return overwrite;
    }
}
