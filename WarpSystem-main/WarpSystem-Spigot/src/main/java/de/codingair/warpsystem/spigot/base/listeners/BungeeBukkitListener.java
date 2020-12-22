package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.codingapi.tools.time.TimeMap;
import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.bungee.InitialPacket;
import de.codingair.warpsystem.base.transfer.packets.bungee.PrepareLoginMessagePacket;
import de.codingair.warpsystem.base.transfer.packets.general.PrepareCoordinationTeleportPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.transfer.handlers.InitialPacketHandler;
import de.codingair.warpsystem.spigot.transfer.handlers.PrepareCoordinationTeleportPacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BungeeBukkitListener implements Listener {
    private String[] notice = null;
    private final TimeMap<String, String> loginMessage = new TimeMap<>();

    public BungeeBukkitListener() {
        WarpSystem.getDataHandler().registerHandler(InitialPacket.class, new InitialPacketHandler(this));
        WarpSystem.getDataHandler().registerHandler(PrepareCoordinationTeleportPacket.class, new PrepareCoordinationTeleportPacketHandler());
        WarpSystem.getDataHandler().registerHandler(PrepareLoginMessagePacket.class, (packet, proxy) -> BungeeBukkitListener.this.process(packet));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String message = loginMessage.remove(e.getPlayer());
        if(message != null) e.getPlayer().sendMessage(message);

        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            if(notice != null && (e.getPlayer().hasPermission(WarpSystem.PERMISSION_NOTIFY) || e.getPlayer().isOp())) e.getPlayer().sendMessage(notice);
        }, 20 * 4L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(Bukkit.getOnlinePlayers().size() <= 1 && WarpSystem.getInstance().isOnBungeeCord()) {
            WarpSystem.getInstance().setOnBungeeCord(false);
        }
    }

    public void updateNotice(String... notice) {
        this.notice = notice;
    }

    public void process(PrepareLoginMessagePacket packet) {
        if(Bukkit.getPlayer(packet.getPlayer()) != null) {
            Bukkit.getPlayer(packet.getPlayer()).sendMessage(packet.getMessage());
        } else loginMessage.put(packet.getPlayer(), packet.getMessage(), 10000);
    }
}
