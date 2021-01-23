package de.codingair.warpsystem.core.transfer.utils;

import de.codingair.packetmanagement.utils.ByteMask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportCommandOptions extends ByteMask {
    private int backPositions;

    public TeleportCommandOptions() {
    }

    public TeleportCommandOptions(int options, int backPositions) {
        super((byte) options);
        this.backPositions = backPositions;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        super.write(out);
        out.writeByte(this.backPositions);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        super.read(in);
        this.backPositions = in.readUnsignedByte();
    }

    public boolean isBack() {
        return getBit(0);
    }

    public boolean isTp() {
        return getBit(1);
    }

    public boolean isTpAll() {
        return getBit(2);
    }

    public boolean isTpToggle() {
        return getBit(3);
    }

    public boolean isTpa() {
        return getBit(4);
    }

    public boolean isTpaHere() {
        return getBit(5);
    }

    public boolean isTpaAll() {
        return getBit(6);
    }

    public boolean isTpaToggle() {
        return getBit(7);
    }

    public int getBackPositions() {
        return backPositions;
    }

    @Override
    public String toString() {
        return "TeleportCommandOptions{" +
                "options=" + getByte() +
                ", isBack=" + isBack() +
                ", isTp=" + isTp() +
                ", isTpAll=" + isTpAll() +
                ", isTpToggle=" + isTpToggle() +
                ", isTpa=" + isTpa() +
                ", isTpaHere=" + isTpaHere() +
                ", isTpaAll=" + isTpaAll() +
                ", isTpaToggle=" + isTpaToggle() +
                '}';
    }
}
