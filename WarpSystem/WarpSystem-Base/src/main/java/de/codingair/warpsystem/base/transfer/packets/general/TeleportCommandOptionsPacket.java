package de.codingair.warpsystem.base.transfer.packets.general;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.ByteMask;
import de.codingair.warpsystem.base.transfer.utils.TeleportCommandOptions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TeleportCommandOptionsPacket implements Packet {
    private TeleportCommandOptions options = new TeleportCommandOptions();

    public TeleportCommandOptionsPacket() {
    }

    public TeleportCommandOptionsPacket(boolean back, boolean tp, boolean tpAll, boolean tpToggle, boolean tpa, boolean tpaHere, boolean tpaAll, boolean tpaToggle) {
        options.setBit(0, back);
        options.setBit(1, tp);
        options.setBit(2, tpAll);
        options.setBit(3, tpToggle);
        options.setBit(4, tpa);
        options.setBit(5, tpaHere);
        options.setBit(6, tpaAll);
        options.setBit(7, tpaToggle);
    }

    public TeleportCommandOptionsPacket(TeleportCommandOptions options) {
        this.options = options;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        this.options.write(out);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.options.read(in);
    }

    public TeleportCommandOptions getOptions() {
        return options;
    }
}
