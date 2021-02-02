package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.proxy.InitialPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.SendOptionsPacket;
import de.codingair.warpsystem.core.transfer.utils.serializeable.ServerOptions;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.listeners.BungeeBukkitListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class InitialPacketHandler implements PacketHandler<InitialPacket> {
    private final BungeeBukkitListener listener;

    public InitialPacketHandler(BungeeBukkitListener listener) {
        this.listener = listener;
    }

    @Override
    public void process(@NotNull InitialPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        WarpSystem.getInstance().setCurrentServer(packet.getServerName());

        String version = packet.getVersion();
        if (!WarpSystem.getInstance().getServer().spigot().getConfig().getBoolean("settings.bungeecord")) {

            listener.updateNotice("§8[§cWarpSystem§8] §fFound a §eproxy §fbut it's §cdisabled in your spigot.yml§f! Please §nenable§f this option to use proxy-features!");

            WarpSystem.getInstance().getLogger().log(Level.WARNING, "Found a proxy but it's disabled in your spigot.yml! Please enable this option to use proxy-features!");
        } else {
            if (version.equals(WarpSystem.getInstance().getDescription().getVersion())) {
                if (WarpSystem.getInstance().getBungeePluginVersion() == null || !WarpSystem.getInstance().getBungeePluginVersion().equals(version)) {
                    WarpSystem.getInstance().getLogger().log(Level.INFO, "Found a valid proxy > Init proxy-features (Server: '" + WarpSystem.getInstance().getCurrentServer() + "')");
                }

                listener.updateNotice((String[]) null);
                Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> WarpSystem.getInstance().setOnProxy(true, (Player) connection), 2L);
                WarpSystem.getDataHandler().send(new SendOptionsPacket(new ServerOptions(WarpSystem.getInstance().getDescription().getVersion(), WarpSystem.opt().getFetchUpdateOption())), (Player) connection);
            } else if (WarpSystem.getInstance().getBungeePluginVersion() == null || WarpSystem.getInstance().getBungeePluginVersion().equals(WarpSystem.getInstance().getDescription().getVersion())) {
                listener.updateNotice("",
                        "§c§l§nWarpSystem",
                        "",
                        "§7WarpSystem §cversion §7of your proxy and spigot server are §cdifferent§7!",
                        "§7Please §cupdate §7the WarpSystem §con your proxy §7(" + version + ") §cand on this server §7(" + WarpSystem.getInstance().getDescription().getVersion() + ")",
                        "");
                WarpSystem.getInstance().getLogger().log(Level.WARNING, "WarpSystem version of your proxy and the spigot server are different!");
                WarpSystem.getInstance().getLogger().log(Level.INFO, "Please update the WarpSystem on your proxy (" + version + ") and on this server (" + WarpSystem.getInstance().getDescription().getVersion() + ")");
            }

            WarpSystem.getInstance().setBungeePluginVersion(version);
        }
    }
}
