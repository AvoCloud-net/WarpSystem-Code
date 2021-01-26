package de.codingair.warpsystem.core.transfer.packets.proxy;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.ByteMask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportPlayerToCoordsPacket implements Packet {
    private String gate, player, destinationName = null, world = null, server = null;
    private Double x, y, z;
    private double costs = 0;
    private Float yaw, pitch;
    private boolean relativeX, relativeY, relativeZ;

    public TeleportPlayerToCoordsPacket() {
    }

    public TeleportPlayerToCoordsPacket(String gate, String player, double x, double y, double z, boolean relativeX, boolean relativeY, boolean relativeZ) {
        this.gate = gate;
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;
    }

    public TeleportPlayerToCoordsPacket(String gate, String player, String destinationName, double costs, Double x, Double y, Double z, Float yaw, Float pitch, String world, String server, boolean relativeX, boolean relativeY, boolean relativeZ) {
        this.gate = gate;
        this.player = player;
        this.destinationName = destinationName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.costs = costs;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
        this.server = server;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;

        if(this.x != null) {
            if(this.y == null) this.y = 0D;
            if(this.z == null) this.z = 0D;
        }
        if (this.yaw != null && this.pitch == null) this.pitch = 0F;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        ByteMask mask = new ByteMask();
        mask.setBit(0, relativeX);
        mask.setBit(1, relativeY);
        mask.setBit(2, relativeZ);
        mask.write(out);

        mask = new ByteMask();
        mask.setBit(0, !gate.equalsIgnoreCase(player));
        mask.setBit(1, costs != 0);
        mask.setBit(2, destinationName != null);
        mask.setBit(3, x != null);
        mask.setBit(4, yaw != null && pitch != null);
        mask.setBit(5, world != null);
        mask.setBit(6, server != null);
        mask.write(out);

        out.writeUTF(this.gate);
        if (!gate.equalsIgnoreCase(player)) out.writeUTF(this.player);
        if (costs != 0) out.writeDouble(costs);

        if (x != null) {
            out.writeDouble(this.x);
            out.writeDouble(this.y);
            out.writeDouble(this.z);
        }

        if (destinationName != null) out.writeUTF(destinationName);
        if (yaw != null) {
            out.writeFloat(yaw);
            out.writeFloat(pitch);
        }
        if (world != null) out.writeUTF(world);
        if (server != null) out.writeUTF(server);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        ByteMask mask = new ByteMask();
        mask.read(in);

        this.relativeX = mask.getBit(0);
        this.relativeY = mask.getBit(1);
        this.relativeZ = mask.getBit(2);

        mask = new ByteMask();
        mask.read(in);

        this.gate = in.readUTF();
        if(mask.getBit(0)) this.player = in.readUTF();
        else this.player = this.gate;

        if(mask.getBit(1)) this.costs = in.readDouble();
        if(mask.getBit(2)) this.destinationName = in.readUTF();
        if(mask.getBit(3)) {
            this.x = in.readDouble();
            this.y = in.readDouble();
            this.z = in.readDouble();
        }
        if(mask.getBit(4)) {
            this.yaw = in.readFloat();
            this.pitch = in.readFloat();
        }
        if(mask.getBit(5)) this.world = in.readUTF();
        if(mask.getBit(6)) this.server = in.readUTF();
    }

    public String getWorld() {
        return world;
    }

    public String getServer() {
        return server;
    }

    public String getGate() {
        return gate;
    }

    public String getPlayer() {
        return player;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public boolean isRelativeX() {
        return relativeX;
    }

    public boolean isRelativeY() {
        return relativeY;
    }

    public boolean isRelativeZ() {
        return relativeZ;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public double getCosts() {
        return costs;
    }

    public Float getYaw() {
        return yaw;
    }

    public Float getPitch() {
        return pitch;
    }
}
