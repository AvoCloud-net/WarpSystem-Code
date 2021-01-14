package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.ToggleForceTeleportsPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.features.TeleportHandler;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Players;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToggleForceTeleportsPacketHandler implements PacketHandler<ToggleForceTeleportsPacket> {
    @Override
    public void process(@NotNull ToggleForceTeleportsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        TeleportHandler handler = Core.getPlugin().getHandler(TeleportHandler.class);
        Player player = Players.getPlayer(packet.getPlayer());
        if (player != null) {
            handler.setDenyForceTps(player, packet.isAutoDenyTp());
            handler.setDenyForceTpRequests(player, packet.isAutoDenyTpa());
        }
    }
}
