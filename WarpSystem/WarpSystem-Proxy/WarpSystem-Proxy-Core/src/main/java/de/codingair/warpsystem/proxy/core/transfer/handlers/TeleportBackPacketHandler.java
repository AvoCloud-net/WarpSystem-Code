package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportBackPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Players;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TeleportBackPacketHandler implements ResponsiblePacketHandler<TeleportBackPacket, BooleanPacket> {
    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull TeleportBackPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player player = Players.getPlayer(packet.getName());

        if(player == null) return CompletableFuture.completedFuture(new BooleanPacket(false));
        else return Core.getPlugin().dataHandler().send(packet, player.getServer(), Direction.DOWN);
    }
}
