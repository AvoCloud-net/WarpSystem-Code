package de.codingair.warpsystem.base.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomTPWorldsPacket implements Packet {
    private HashMap<String, List<String>> worlds;

    public RandomTPWorldsPacket() {
    }

    public RandomTPWorldsPacket(String server, List<String> worlds) {
        this.worlds = new HashMap<>(1, 1);
        this.worlds.put(server, worlds);
    }

    public RandomTPWorldsPacket(List<String> worlds) {
        this("", worlds);
    }

    public RandomTPWorldsPacket(HashMap<String, List<String>> data) {
        this.worlds = new HashMap<>(data);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(this.worlds.size());

        for (Map.Entry<String, List<String>> e : this.worlds.entrySet()) {
            out.writeUTF(e.getKey());

            out.writeByte(e.getValue().size());
            for (String world : e.getValue()) {
                out.writeUTF(world);
            }
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        int size = in.readUnsignedByte();
        this.worlds = new HashMap<>(size, 1);

        for (int i = 0; i < size; i++) {
            String server = in.readUTF();

            int count = in.readUnsignedByte();
            List<String> worlds = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                worlds.add(in.readUTF());
            }

            this.worlds.put(server, worlds);
        }
    }

    public String getServer() {
        if (this.worlds.size() != 1) throw new IllegalStateException("There are less or more entries than 1: " + this.worlds.size());
        return this.worlds.keySet().stream().findAny().get();
    }

    public void setServer(String server) {
        if (this.worlds.size() != 1) throw new IllegalStateException("There are less or more entries than 1: " + this.worlds.size());
        this.worlds.put(server, this.worlds.remove(getServer()));
    }

    public List<String> getWorlds() {
        if (this.worlds.size() != 1) throw new IllegalStateException("There are less or more entries than 1: " + this.worlds.size());
        return worlds.get("");
    }

    public void setWorlds(HashMap<String, List<String>> worlds) {
        if (this.worlds.size() != 1) throw new IllegalStateException("There are less or more entries than 1: " + this.worlds.size());
        this.worlds = worlds;
    }

    public HashMap<String, List<String>> getData() {
        return worlds;
    }
}
