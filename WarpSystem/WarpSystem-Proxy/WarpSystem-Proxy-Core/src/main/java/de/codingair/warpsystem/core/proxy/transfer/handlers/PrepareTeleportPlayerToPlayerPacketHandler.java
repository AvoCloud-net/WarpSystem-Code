package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.transfer.packets.proxy.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.PrepareTeleportPlayerToPlayerPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareTeleportPlayerToPlayerPacketHandler implements ResponsiblePacketHandler<PrepareTeleportPlayerToPlayerPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull PrepareTeleportPlayerToPlayerPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player player = Players.getPlayer(packet.getPlayer());
        Player target = Players.getPlayer(packet.getDestinationPlayer());

        if (player == null || target == null) return CompletableFuture.completedFuture(new IntegerPacket(1));
        else {
            TeleportPlayerToPlayerPacket tpPacket = new TeleportPlayerToPlayerPacket(player.getName(), player.getName(), target.getName());
            tpPacket.setCosts(packet.getCosts());
            Core.getPlugin().dataHandler().send(tpPacket, target.getServer(), Direction.DOWN);

            CompletableFuture<IntegerPacket> future = new CompletableFuture<>();
            if (!player.getServer().equals(target.getServer())) {
                player.connect(target.getServer()).whenComplete((connected, throwable) -> {
                    if (!connected) future.complete(new IntegerPacket(2));
                    else future.complete(new IntegerPacket(0));
                });
            } else return CompletableFuture.completedFuture(new IntegerPacket(0));

            return future;
        }
    }
}
