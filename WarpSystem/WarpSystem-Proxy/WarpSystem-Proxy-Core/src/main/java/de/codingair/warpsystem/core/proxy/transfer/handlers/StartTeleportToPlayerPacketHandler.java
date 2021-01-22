package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.transfer.packets.general.StartTeleportToPlayerPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class StartTeleportToPlayerPacketHandler implements ResponsibleMultiLayerPacketHandler<StartTeleportToPlayerPacket, IntegerPacket> {

    @Override
    public boolean answer(@NotNull StartTeleportToPlayerPacket packet, @NotNull Proxy proxy, @NotNull Direction direction) {
        //redis
        //we might not be able to handle this packet!
        return direction == Direction.DOWN || Players.getPlayer(packet.getPlayer()) != null;
    }

    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull StartTeleportToPlayerPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player player = Players.getPlayer(packet.getPlayer());

        if (player != null)
            return Core.getPlugin().dataHandler().send(new StartTeleportToPlayerPacket(packet.getPlayer(), packet.getTo(), packet.getToDisplayName(), packet.getTeleportRequestSender()), player.getServer(), Direction.DOWN);
        else if(direction == Direction.DOWN) throw new Escalation(this, Direction.UP, packet, err -> new IntegerPacket(1));
        else return CompletableFuture.completedFuture(new IntegerPacket(1));
    }
}
