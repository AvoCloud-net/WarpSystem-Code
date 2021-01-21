package de.codingair.warpsystem.core.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SendWorldNamesPacket implements Packet {
    private Set<String> worlds;

    public SendWorldNamesPacket() {
    }

    public SendWorldNamesPacket(Set<String> worlds) {
        this.worlds = worlds;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(worlds.size());
        for (String world : worlds) {
            out.writeUTF(world);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        int size = in.readUnsignedByte();

        this.worlds = new HashSet<>(size, 1F);
        for (int i = 0; i < size; i++) {
            this.worlds.add(in.readUTF());
        }
    }

    public Set<String> getWorlds() {
        return worlds;
    }
}
