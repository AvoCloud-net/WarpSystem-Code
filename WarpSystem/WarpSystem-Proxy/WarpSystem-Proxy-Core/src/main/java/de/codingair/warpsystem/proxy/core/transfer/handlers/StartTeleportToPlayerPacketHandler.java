package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.StartTeleportToPlayerPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Players;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class StartTeleportToPlayerPacketHandler implements ResponsiblePacketHandler<StartTeleportToPlayerPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull StartTeleportToPlayerPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player player = Players.getPlayer(packet.getPlayer());

        if (player != null)
            return Core.getPlugin().dataHandler().send(new StartTeleportToPlayerPacket(packet.getPlayer(), packet.getTo(), packet.getToDisplayName(), packet.getTeleportRequestSender()), player.getServer(), Direction.DOWN);
        else return CompletableFuture.completedFuture(new IntegerPacket(1));
    }
}
