package de.codingair.warpsystem.bungee.features.teleport;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportCommandOptionsPacket;
import de.codingair.warpsystem.base.transfer.utils.TeleportCommandOptions;
import de.codingair.warpsystem.base.utils.Manager;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TeleportManager implements Manager {
    private final HashMap<ServerInfo, TeleportCommandOptions> commandOptions = new HashMap<>();
    private final Set<String> denyForceTpRequests = new HashSet<>();
    private final Set<String> denyForceTps = new HashSet<>();

    public static TeleportManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.TELEPORT);
    }

    @Override
    public boolean load(boolean loader) {
        if(!loader) WarpSystem.log("  > Initializing TeleportManager");

        WarpSystem.proxy().getPluginManager().registerListener(WarpSystem.getInstance(), new TeleportCommandListener());
        return true;
    }

    @Override
    public void save(boolean saver) {
    }

    @Override
    public void destroy() {
    }

    public void registerOptions(ServerInfo info, TeleportCommandOptionsPacket options) {
        this.commandOptions.put(info, options.getOptions());
        WarpSystem.getInstance().getServerManager().getOnlineServer().filter(s -> !s.equals(info)).forEach(s -> WarpSystem.getDataHandler().send(options, s, Direction.DOWN));
    }

    public TeleportCommandOptions getOptions(ServerInfo info) {
        return this.commandOptions.get(info);
    }

    public boolean isAccessible(ServerInfo info) {
        return getOptions(info) == null;
    }

    public boolean deniesForceTps(ProxiedPlayer player) {
        return this.denyForceTps.contains(player.getName());
    }

    public void setDenyForceTps(ProxiedPlayer player, boolean deny) {
        if(deny) this.denyForceTps.add(player.getName());
        else this.denyForceTps.remove(player.getName());
    }

    public boolean deniesForceTpRequests(ProxiedPlayer player) {
        return this.denyForceTpRequests.contains(player.getName());
    }

    public void setDenyForceTpRequests(ProxiedPlayer player, boolean deny) {
        if(deny) this.denyForceTpRequests.add(player.getName());
        else this.denyForceTpRequests.remove(player.getName());
    }
}
