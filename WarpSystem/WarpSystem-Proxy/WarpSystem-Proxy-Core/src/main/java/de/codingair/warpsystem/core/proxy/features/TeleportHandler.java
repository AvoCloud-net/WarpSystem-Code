package de.codingair.warpsystem.core.proxy.features;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportCommandOptionsPacket;
import de.codingair.warpsystem.core.transfer.utils.TeleportCommandOptions;
import de.codingair.warpsystem.core.utils.Manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TeleportHandler implements Manager {
    protected final HashMap<String, TeleportCommandOptions> commandOptions = new HashMap<>();
    protected final Set<String> denyForceTpRequests = new HashSet<>();
    protected final Set<String> denyForceTps = new HashSet<>();

    public boolean load(boolean loader) {
        if (!loader) Core.getPlugin().log("  > Initializing TeleportManager");
        return true;
    }

    @Override
    public void save(boolean saver) {
    }

    @Override
    public void destroy() {
    }

    public void registerOptions(Server<?> server, TeleportCommandOptionsPacket packet) {
        this.commandOptions.put(server.getName(), packet.getOptions());

        packet.setServer(server.getName());
        Core.getServerManager().getOnlineServer().filter(s -> !s.equals(server)).forEach(s -> Core.getPlugin().dataHandler().send(packet, s, Direction.DOWN));
        commandOptions.entrySet().stream().filter(e -> !e.getKey().equals(server.getName())).forEach(e -> Core.getPlugin().dataHandler().send(new TeleportCommandOptionsPacket(e.getKey(), e.getValue()), server, Direction.DOWN));
        Core.getPlugin().dataHandler().send(packet, null, Direction.UP);
    }

    //redis
    public void registerOptions(TeleportCommandOptionsPacket packet) {
        if(this.commandOptions.putIfAbsent(packet.getServer(), packet.getOptions()) != null) return;
        Core.getServerManager().getOnlineServer().filter(s -> !s.getName().equals(packet.getServer())).forEach(s -> Core.getPlugin().dataHandler().send(packet, s, Direction.DOWN));
    }

    public TeleportCommandOptions getOptions(Server<?> info) {
        return this.commandOptions.get(info.getName());
    }

    public HashMap<String, TeleportCommandOptions> getCommandOptions() {
        return commandOptions;
    }

    public boolean isAccessible(Server<?> info) {
        return getOptions(info) != null;
    }

    public boolean deniesForceTps(Player player) {
        return this.denyForceTps.contains(player.getName());
    }

    public void setDenyForceTps(Player player, boolean deny) {
        if (deny) this.denyForceTps.add(player.getName());
        else this.denyForceTps.remove(player.getName());
    }

    public boolean deniesForceTpRequests(Player player) {
        return deniesForceTpRequests(player.getName());
    }

    public boolean deniesForceTpRequests(String player) {
        return this.denyForceTpRequests.contains(player);
    }

    public void setDenyForceTpRequests(Player player, boolean deny) {
        if (deny) this.denyForceTpRequests.add(player.getName());
        else this.denyForceTpRequests.remove(player.getName());
    }
}
