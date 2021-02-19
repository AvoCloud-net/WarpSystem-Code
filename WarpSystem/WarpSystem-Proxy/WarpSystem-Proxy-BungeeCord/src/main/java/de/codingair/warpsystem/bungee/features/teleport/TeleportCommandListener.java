package de.codingair.warpsystem.bungee.features.teleport;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.events.ServerProvideOptionsEvent;
import de.codingair.warpsystem.bungee.utils.BungeePlayer;
import de.codingair.warpsystem.core.transfer.packets.spigot.ToggleForceTeleportsPacket;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TeleportCommandListener implements Listener {

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        BungeePlayer p = new BungeePlayer(e.getPlayer());
        TeleportManager.getInstance().setDenyForceTps(p, false);
        TeleportManager.getInstance().setDenyForceTpRequests(p, false);
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent e) {
        BungeePlayer p = new BungeePlayer(e.getPlayer());
        sendToggleInfo(p);
    }

    protected void sendToggleInfo(BungeePlayer p) {
        boolean tp, tpa;
        if ((tp = TeleportManager.getInstance().deniesForceTps(p)) | (tpa = TeleportManager.getInstance().deniesForceTpRequests(p)))
            WarpSystem.getDataHandler().send(new ToggleForceTeleportsPacket(p.getName(), tp, tpa), p.getServer(), Direction.DOWN);
    }

    @EventHandler
    public void onProvideOptions(ServerProvideOptionsEvent e) {
        e.getServer().getOnlinePlayers().forEach(p -> sendToggleInfo((BungeePlayer) p));
    }
}
