package de.codingair.warpsystem.bungee.features.teleport;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.spigot.ToggleForceTeleportsPacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TeleportCommandListener implements Listener {

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        TeleportManager.getInstance().setDenyForceTps(e.getPlayer(), false);
        TeleportManager.getInstance().setDenyForceTpRequests(e.getPlayer(), false);
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent e) {
        boolean tp, tpa;
        if((tp = TeleportManager.getInstance().deniesForceTps(e.getPlayer())) | (tpa = TeleportManager.getInstance().deniesForceTpRequests(e.getPlayer())))
            WarpSystem.getDataHandler().send(new ToggleForceTeleportsPacket(e.getPlayer().getName(), tp, tpa), e.getPlayer().getServer().getInfo(), Direction.DOWN);
    }
}
