package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.SuccessPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.transfer.packets.spigot.PerformCommandOnProxyPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PerformCommandOnProxyPacketHandler implements ResponsiblePacketHandler<PerformCommandOnProxyPacket, SuccessPacket> {
    @Override
    public @NotNull CompletableFuture<SuccessPacket> response(@NotNull PerformCommandOnProxyPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player p = Players.getPlayer(packet.getPlayer());
        if (p == null) return CompletableFuture.completedFuture(new SuccessPacket(false));

        CompletableFuture<SuccessPacket> future = new CompletableFuture<>();
        p.performCommand(packet.getCommand()).thenAccept(success -> future.complete(new SuccessPacket(success)));
        return future;
    }
}
