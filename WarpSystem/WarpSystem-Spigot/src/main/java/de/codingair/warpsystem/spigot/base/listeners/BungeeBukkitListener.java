package de.codingair.warpsystem.spigot.base.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.warpsystem.core.transfer.packets.proxy.InitialPacket;
import de.codingair.warpsystem.core.transfer.packets.proxy.PrepareLoginMessagePacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.RequestInitialPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.utils.ConnectionPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.transfer.handlers.InitialPacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class BungeeBukkitListener implements Listener {
    private final Cache<String, String> loginMessage = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
    private String[] notice = null;

    public BungeeBukkitListener() {
        WarpSystem.getDataHandler().registerHandler(InitialPacket.class, new InitialPacketHandler(this));
        WarpSystem.getDataHandler().registerHandler(PrepareLoginMessagePacket.class, (packet, proxy, connection, direction) -> BungeeBukkitListener.this.process(packet));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.isOnline()) return; //filter fake NPCs

        String message = loginMessage.getIfPresent(p.getName());
        if (message != null) {
            loginMessage.invalidate(p.getName());
            p.sendMessage(message);
        }

        if (WarpSystem.getInstance().isUseProxy()) {
            //request initial packet //do NOT check if already connected -> initial packet for every proxy server
            Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), () -> tryConnection(p, 0));
        }

        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            if (notice != null && (p.hasPermission(Permissions.PERMISSION_NOTIFY) || p.isOp())) p.sendMessage(notice);
        }, 20 * 4L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (getOnlinePlayers().count() <= 1 && WarpSystem.getInstance().isProxyConnected()) {
            WarpSystem.getInstance().setOnProxy(false, null);
        }
    }

    public void updateNotice(String... notice) {
        this.notice = notice;
    }

    public void process(PrepareLoginMessagePacket packet) {
        if (packet.getMessage() == null) return;

        Player p = Bukkit.getPlayer(packet.getPlayer());
        if (p != null) p.sendMessage(packet.getMessage());
        else loginMessage.put(packet.getPlayer(), packet.getMessage());
    }

    @NotNull
    private Stream<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(Player::isOnline);
    }

    private void tryConnection(Player p, int counter) {
        if (p == null || !p.isOnline() || counter == 10) return;

        WarpSystem.getDataHandler().send(new ConnectionPacket(), p, 500).whenComplete((success, t) -> {
            if (success != null) {
                WarpSystem.getDataHandler().send(new RequestInitialPacket(), p);
            } else {
                tryConnection(p, counter + 1);
            }
        });
    }
}
