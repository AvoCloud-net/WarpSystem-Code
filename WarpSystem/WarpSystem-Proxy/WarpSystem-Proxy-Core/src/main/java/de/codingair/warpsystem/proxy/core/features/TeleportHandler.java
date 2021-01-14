package de.codingair.warpsystem.proxy.core.features;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportCommandOptionsPacket;
import de.codingair.warpsystem.base.transfer.utils.TeleportCommandOptions;
import de.codingair.warpsystem.base.utils.Manager;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TeleportHandler implements Manager {
    protected final HashMap<Server, TeleportCommandOptions> commandOptions = new HashMap<>();
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

    public void registerOptions(Server info, TeleportCommandOptionsPacket options) {
        this.commandOptions.put(info, options.getOptions());

        options.setServer(info.getName());
        Core.getServerManager().getOnlineServer().forEach(s -> Core.getPlugin().dataHandler().send(options, s, Direction.DOWN));
    }

    public TeleportCommandOptions getOptions(Server info) {
        return this.commandOptions.get(info);
    }

    public boolean isAccessible(Server info) {
        return getOptions(info) == null;
    }

    public boolean deniesForceTps(Player player) {
        return this.denyForceTps.contains(player.getName());
    }

    public void setDenyForceTps(Player player, boolean deny) {
        if (deny) this.denyForceTps.add(player.getName());
        else this.denyForceTps.remove(player.getName());
    }

    public boolean deniesForceTpRequests(Player player) {
        return this.denyForceTpRequests.contains(player.getName());
    }

    public void setDenyForceTpRequests(Player player, boolean deny) {
        if (deny) this.denyForceTpRequests.add(player.getName());
        else this.denyForceTpRequests.remove(player.getName());
    }
}
