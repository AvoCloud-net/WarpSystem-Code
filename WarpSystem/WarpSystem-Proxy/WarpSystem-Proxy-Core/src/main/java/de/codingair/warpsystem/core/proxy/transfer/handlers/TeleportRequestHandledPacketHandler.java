package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.handlers.MultiLayerPacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.transfer.packets.spigot.TeleportRequestHandledPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportRequestHandledPacketHandler implements MultiLayerPacketHandler<TeleportRequestHandledPacket> {
    @Override
    public void process(@NotNull TeleportRequestHandledPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player sender = Players.getPlayer(packet.getSender());
        if (sender != null) Core.getPlugin().dataHandler().send(packet, sender.getServer(), Direction.DOWN);
        else if(direction == Direction.DOWN) throw new Escalation(this, Direction.UP, packet);
    }
}
