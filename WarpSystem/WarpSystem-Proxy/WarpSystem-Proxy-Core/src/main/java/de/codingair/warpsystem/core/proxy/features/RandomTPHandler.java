package de.codingair.warpsystem.core.proxy.features;

import de.codingair.codingapi.tools.io.lib.JSONArray;
import de.codingair.codingapi.tools.io.utils.DataMask;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.spigot.QueueRTPUsagePacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.RandomTPWorldsPacket;
import de.codingair.warpsystem.core.utils.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class RandomTPHandler implements Manager {
    protected final HashMap<String, List<String>> worlds = new HashMap<>();
    protected final HashMap<String, List<UUID>> queuedEntries = new HashMap<>();

    public boolean load(boolean loader, DataMask queue, DataMask worlds) {
        if (!loader) Core.getPlugin().log("  > Loading RandomTPManager");
        destroy();

        int queueSize = 0;
        for (String server : queue.keySet(false)) {
            JSONArray l = queue.getList(server);
            List<UUID> data = new ArrayList<>();
            for (Object o : l) {
                String s = (String) o;
                data.add(UUID.fromString(s));
                queueSize++;
            }
            queuedEntries.put(server, data);
        }

        if (!loader) Core.getPlugin().log("    ...got " + queueSize + " queued random tp(s)");

        int size = 0;
        for (String key : worlds.keySet(false)) {
            JSONArray data = worlds.getList(key);
            size += data.size();
            addWorldData(key, data);
        }
        if (!loader) Core.getPlugin().log("    ...got " + size + " registered random tp world(s)");
        return true;
    }

    public void save(boolean saver, DataMask worldMask) {
        if (!saver) Core.getPlugin().log("  > Saving RandomTPManager");
        saveQueue(saver);

        worldMask.clear();
        int size = 0;
        for (String s : worlds.keySet()) {
            List<String> worlds = this.worlds.get(s);
            worldMask.put(s, worlds);
            size += worlds.size();
        }

        if (!saver) Core.getPlugin().log("    ...saved " + size + " registered random tp world(s)");
    }

    public abstract void saveQueue(boolean saver);

    protected void saveQueue(boolean saver, DataMask queue) {
        queue.clear();
        int size = 0;
        for (String server : queuedEntries.keySet()) {
            List<UUID> value = queuedEntries.get(server);
            if (value != null && !value.isEmpty()) {
                List<String> data = new ArrayList<>();
                for (UUID uuid : value) {
                    data.add(uuid.toString());
                }
                queue.put(server, data);
                size += value.size();
            }
        }

        if (!saver) Core.getPlugin().log("    ...saved " + size + " queued random tp(s)");
    }

    @Override
    public void destroy() {
        this.queuedEntries.values().forEach(List::clear);
        this.queuedEntries.clear();
        this.worlds.values().forEach(List::clear);
        this.worlds.clear();
    }

    public void addQueueEntry(UUID uuid, String server) {
        List<UUID> l = queuedEntries.computeIfAbsent(server, s -> new ArrayList<>());
        l.add(uuid);
    }

    public void updateQueue(Server info) {
        List<UUID> l = queuedEntries.remove(info.getName());
        if (l != null && !l.isEmpty()) {
            Core.getPlugin().dataHandler().send(new QueueRTPUsagePacket(l), info, Direction.DOWN);
        }
    }

    private void addWorldData(String server, List<String> worlds) {
        if (worlds == null || worlds.isEmpty()) this.worlds.remove(server);
        else this.worlds.put(server, worlds);
    }

    public void addWorldData(Server server, RandomTPWorldsPacket packet) {
        addWorldData(server.getName(), packet.getWorlds());

        packet.setServer(server.getName());
        Core.getServerManager().getOnlineServer().forEach(s -> Core.getPlugin().dataHandler().send(packet, s, Direction.DOWN));
    }

    public List<String> getWorlds(String server) {
        return this.worlds.getOrDefault(server, new ArrayList<>());
    }

    public boolean hasRegisteredServers() {
        return !this.worlds.isEmpty();
    }

    public List<String> getServer() {
        List<String> servers = new ArrayList<>();
        for (String s : this.worlds.keySet()) {
            Server info = Core.getPlugin().getServer(s);
            if (info != null && Core.getServerManager().isOnline(info)) servers.add(s);
        }
        return servers;
    }

    public HashMap<String, List<String>> getWorlds() {
        return worlds;
    }
}
