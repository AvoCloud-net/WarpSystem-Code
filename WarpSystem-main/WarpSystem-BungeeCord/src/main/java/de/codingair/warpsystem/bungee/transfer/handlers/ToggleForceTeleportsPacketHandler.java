package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.ToggleForceTeleportsPacket;
import de.codingair.warpsystem.bungee.api.Players;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToggleForceTeleportsPacketHandler implements PacketHandler<ToggleForceTeleportsPacket> {
    @Override
    public void process(@NotNull ToggleForceTeleportsPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        ProxiedPlayer player = Players.getPlayer(packet.getPlayer());
        if(player != null) {
            TeleportManager.getInstance().setDenyForceTps(player, packet.isAutoDenyTp());
            TeleportManager.getInstance().setDenyForceTpRequests(player, packet.isAutoDenyTpa());
        }
    }
}
