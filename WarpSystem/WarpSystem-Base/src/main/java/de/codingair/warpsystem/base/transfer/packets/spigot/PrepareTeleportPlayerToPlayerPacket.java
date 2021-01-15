package de.codingair.warpsystem.base.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrepareTeleportPlayerToPlayerPacket implements RequestPacket<IntegerPacket> {
    private String player;
    private String destinationPlayer;
    private int costs = 0;

    public PrepareTeleportPlayerToPlayerPacket() {
    }

    public PrepareTeleportPlayerToPlayerPacket(String player, String destinationPlayer) {
        this.player = player;
        this.destinationPlayer = destinationPlayer;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(player);
        out.writeUTF(destinationPlayer);
        out.writeInt(this.costs);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.player = in.readUTF();
        this.destinationPlayer = in.readUTF();
        this.costs = in.readInt();
    }

    public String getPlayer() {
        return player;
    }

    public String getDestinationPlayer() {
        return destinationPlayer;
    }

    public int getCosts() {
        return costs;
    }

    public PrepareTeleportPlayerToPlayerPacket setCosts(int costs) {
        this.costs = costs;
        return this;
    }
}
