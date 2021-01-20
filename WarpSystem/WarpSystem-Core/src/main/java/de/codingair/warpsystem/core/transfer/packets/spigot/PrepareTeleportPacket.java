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
    private double x, y, z;

    public PrepareTeleportPacket() {
        recipient = null;
    }

    public PrepareTeleportPacket(@NotNull String sender, @Nullable String recipient, @NotNull String target) {
        this.sender = sender;
        this.recipient = recipient;
        this.target = target;
    }

    public PrepareTeleportPacket(@NotNull String sender, @NotNull String recipient, double x, double y, double z) {
        this.sender = sender;
        this.recipient = recipient;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        ByteMask mask = new ByteMask();
        mask.setBit(0, target == null);

        if(target != null) {
            mask.setBit(1, !sender.equalsIgnoreCase(target));
            mask.setBit(1, recipient != null);
        }

        mask.write(out);
        out.writeUTF(sender);

        if (target == null) {
            out.writeDouble(x);
            out.writeDouble(y);
            out.writeDouble(z);
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
            this.x = in.readDouble();
            this.y = in.readDouble();
            this.z = in.readDouble();
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
