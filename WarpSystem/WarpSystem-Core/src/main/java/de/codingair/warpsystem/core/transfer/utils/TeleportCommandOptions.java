package de.codingair.warpsystem.core.transfer.utils;

import de.codingair.packetmanagement.utils.ByteMask;

public class TeleportCommandOptions extends ByteMask {

    public TeleportCommandOptions() {
    }

    public TeleportCommandOptions(int options) {
        super((byte) options);
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
