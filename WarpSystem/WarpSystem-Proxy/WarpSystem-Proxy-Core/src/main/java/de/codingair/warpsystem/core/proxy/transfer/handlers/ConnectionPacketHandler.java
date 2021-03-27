package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.SuccessPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.spigot.utils.ConnectionPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ConnectionPacketHandler implements ResponsiblePacketHandler<ConnectionPacket, SuccessPacket> {
    @Override
    public @NotNull CompletableFuture<SuccessPacket> response(@NotNull ConnectionPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        return CompletableFuture.completedFuture(new SuccessPacket());
    }
}
