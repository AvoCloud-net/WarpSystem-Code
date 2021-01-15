package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.StringPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RequestFullNamePacket;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Players;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RequestFullNamePacketHandler implements ResponsiblePacketHandler<RequestFullNamePacket, StringPacket> {
    @Override
    public @NotNull CompletableFuture<StringPacket> response(@NotNull RequestFullNamePacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player pp = Players.getPlayer(packet.getName());
        return CompletableFuture.completedFuture(new StringPacket(pp == null ? null : pp.getName()));
    }
}
