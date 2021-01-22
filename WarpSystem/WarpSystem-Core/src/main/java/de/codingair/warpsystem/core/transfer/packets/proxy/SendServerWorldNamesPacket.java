package de.codingair.warpsystem.core.transfer.packets.proxy;

import de.codingair.packetmanagement.packets.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SendServerWorldNamesPacket implements Packet {
    private HashMap<String, Set<String>> worlds;

    public SendServerWorldNamesPacket() {
    }

    public SendServerWorldNamesPacket(ConcurrentHashMap<String, Set<String>> worlds) {
        this.worlds = new HashMap<>(worlds);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte((byte) worlds.size());
        for (Map.Entry<String, Set<String>> e : worlds.entrySet()) {
            out.writeUTF(e.getKey());
            out.writeByte(e.getValue().size());
            for (String s : e.getValue()) {
                out.writeUTF(s);
            }
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        int size = in.readUnsignedByte();
        this.worlds = new HashMap<>(size, 1F);
        for (int i = 0; i < size; i++) {
            String server = in.readUTF();
            int worlds = in.readUnsignedByte();

            Set<String> set = new HashSet<>(worlds, 1F);
            for (int j = 0; j < worlds; j++) {
                set.add(in.readUTF());
            }

            this.worlds.put(server, set);
        }
    }

    public HashMap<String, Set<String>> getWorlds() {
        return worlds;
    }
}
