package de.codingair.warpsystem.core.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.LongPacket;
import de.codingair.packetmanagement.utils.ByteMask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareTeleportPacket implements RequestPacket<LongPacket> {
    private String sender, recipient, target;
    private Double x, y, z;
    private Float yaw, pitch;
    private String server, world;

    public PrepareTeleportPacket() {
        recipient = null;
    }

    public PrepareTeleportPacket(@NotNull String sender, @Nullable String recipient, @NotNull String target) {
        this.sender = sender;
        this.recipient = recipient;
        this.target = target;
    }

    public PrepareTeleportPacket(@NotNull String sender, @NotNull String recipient, Double x, Double y, Double z, Float yaw, Float pitch, String server, String world) {
        this.sender = sender;
        this.recipient = recipient;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.server = server;
        this.world = world;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        ByteMask mask = new ByteMask();
        mask.setBit(0, target == null); //coordinates only

        if(target != null) {
            //no coordinates
            mask.setBit(1, !sender.equalsIgnoreCase(target));
            mask.setBit(2, recipient != null);
        } else {
            mask.setBit(1, x != null);
            mask.setBit(2, yaw != null);
            mask.setBit(3, server != null);
            mask.setBit(4, world != null);
        }

        mask.write(out);
        out.writeUTF(sender);

        if (target == null) {
            if(x != null) {
                out.writeDouble(x);
                out.writeDouble(y);
                out.writeDouble(z);
            }

            if(yaw != null) {
                out.writeFloat(yaw);
                out.writeFloat(pitch);
            }

            if(server != null) out.writeUTF(server);
            if(world != null) out.writeUTF(world);

            out.writeUTF(recipient);
        } else {
            if (!sender.equalsIgnoreCase(target)) out.writeUTF(target);
            if (recipient != null) out.writeUTF(recipient);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        ByteMask mask = new ByteMask();
        mask.read(in);

        this.sender = in.readUTF();
        if (mask.getBit(0)) {
            //target is null

            if(mask.getBit(1)) {
                this.x = in.readDouble();
                this.y = in.readDouble();
                this.z = in.readDouble();
            }

            if(mask.getBit(2)) {
                this.yaw = in.readFloat();
                this.pitch = in.readFloat();
            }

            if(mask.getBit(3)) this.server = in.readUTF();
            if(mask.getBit(4)) this.world = in.readUTF();

            this.recipient = in.readUTF();
        } else {
            if (mask.getBit(1)) this.target = in.readUTF();
            else this.target = this.sender;
            if (mask.getBit(2)) this.recipient = in.readUTF();
        }
    }

    public boolean isCoordsPacket() {
        return target == null;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getTarget() {
        return target;
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

    public Float getYaw() {
        return yaw;
    }

    public Float getPitch() {
        return pitch;
    }

    public String getServer() {
        return server;
    }

    public String getWorld() {
        return world;
    }

    public enum Result {
        SUCCESS,
        PLAYER_NOT_ONLINE,
        SERVER_NOT_ONLINE,
        TELEPORT_DENIED
    }
}
