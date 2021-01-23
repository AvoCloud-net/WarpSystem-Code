package de.codingair.warpsystem.spigot.features.teleportcommand.listeners;

import de.codingair.warpsystem.spigot.api.events.PlayerDataUpdateEvent;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BackListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (Permissions.hasPermission(e.getEntity(), Permissions.PERMISSION_USE_TELEPORT_COMMAND_BACK_DETECT_DEATHS))
            TeleportCommandManager.getInstance().addToBackHistory(e.getEntity(), e.getEntity().getLocation(), false);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.isCancelled() || !Permissions.hasPermission(e.getPlayer(), Permissions.PERMISSION_USE_TELEPORT_COMMAND_BACK)) return;

        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN && e.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND) return;
        TeleportCommandManager.getInstance().addToBackHistory(e.getPlayer(), e.getFrom(), false);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        TeleportCommandManager.getInstance().revive(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        TeleportCommandManager.getInstance().addToBackHistory(p, p.getLocation(), true);
    }

    @EventHandler
    public void onDataUpdate(PlayerDataUpdateEvent e) {
        if(e.getData().getOldServer() != null && !e.getData().isFirstServer()) {
            Player p = e.getPlayer();
            TeleportCommandManager.getInstance().invalidateBackPosition(p);
        }
    }

}
