package de.codingair.warpsystem.core.transfer.packets.spigot;


import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.SuccessPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PerformCommandOnProxyPacket implements RequestPacket<SuccessPacket> {
    private String player;
    private String command;

    public PerformCommandOnProxyPacket() {
    }

    public PerformCommandOnProxyPacket(String player, String command) {
        this.player = player;
        this.command = command;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.player);
        out.writeUTF(this.command);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.command = in.readUTF();
    }

    public String getPlayer() {
        return player;
    }

    public String getCommand() {
        return command;
    }
}
