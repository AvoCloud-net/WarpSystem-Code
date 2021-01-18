package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.transfer.packets.spigot.TeleportRequestHandledPacket;
import de.codingair.warpsystem.core.proxy.utils.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportRequestHandledPacketHandler implements PacketHandler<TeleportRequestHandledPacket> {
    @Override
    public void process(@NotNull TeleportRequestHandledPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player sender = Players.getPlayer(packet.getSender());
        if (sender != null) Core.getPlugin().dataHandler().send(packet, sender.getServer(), Direction.DOWN);
    }
}
