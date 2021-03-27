package de.codingair.warpsystem.core.transfer.packets.spigot.utils;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.SuccessPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A packet to check whether the plugin messaging channel is ready.
 */
public class ConnectionPacket implements RequestPacket<SuccessPacket> {
    @Override
    public void write(DataOutputStream out) throws IOException {
    }

    @Override
    public void read(DataInputStream in) throws IOException {
    }
}
