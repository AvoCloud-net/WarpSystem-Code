package de.codingair.warpsystem.velocity.features.teleport;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.core.transfer.packets.spigot.ToggleForceTeleportsPacket;
import de.codingair.warpsystem.velocity.base.WarpSystem;
import de.codingair.warpsystem.velocity.base.events.ServerProvideOptionsEvent;
import de.codingair.warpsystem.velocity.utils.VelocityPlayer;
import de.codingair.warpsystem.velocity.utils.VelocityServer;

public class TeleportCommandListener {

    @Subscribe
    public void onQuit(DisconnectEvent e) {
        VelocityPlayer p = new VelocityPlayer(e.getPlayer());
        TeleportManager.getInstance().setDenyForceTps(p, false);
        TeleportManager.getInstance().setDenyForceTpRequests(p, false);
    }

    @Subscribe
    public void onSwitch(ServerConnectedEvent e) {
        VelocityPlayer p = new VelocityPlayer(e.getPlayer());
        sendToggleInfo(p, new VelocityServer(e.getServer()));
    }

    @Subscribe
    public void onProvideOptions(ServerProvideOptionsEvent e) {
        e.getServer().getOnlinePlayers().forEach(p -> sendToggleInfo((VelocityPlayer) p, e.getServer()));
    }

    protected void sendToggleInfo(VelocityPlayer p, VelocityServer server) {
        boolean tp, tpa;
        if ((tp = TeleportManager.getInstance().deniesForceTps(p)) | (tpa = TeleportManager.getInstance().deniesForceTpRequests(p)))
            WarpSystem.getInstance().getDataHandler().send(new ToggleForceTeleportsPacket(p.getName(), tp, tpa), server, Direction.DOWN);
    }
}
