package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.TeleportRequestHandledPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Players;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportRequestHandledPacketHandler implements PacketHandler<TeleportRequestHandledPacket> {
    @Override
    public void process(@NotNull TeleportRequestHandledPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player sender = Players.getPlayer(packet.getSender());
        if (sender != null) Core.getPlugin().dataHandler().send(packet, sender.getServer(), Direction.DOWN);
    }
}
