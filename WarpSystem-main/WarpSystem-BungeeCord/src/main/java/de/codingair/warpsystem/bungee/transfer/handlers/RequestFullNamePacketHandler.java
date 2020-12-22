package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.StringPacket;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RequestFullNamePacket;
import de.codingair.warpsystem.bungee.api.Players;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RequestFullNamePacketHandler implements ResponsiblePacketHandler<RequestFullNamePacket, StringPacket> {
    @Override
    public @NotNull CompletableFuture<StringPacket> response(@NotNull RequestFullNamePacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        ProxiedPlayer pp = Players.getPlayer(packet.getName());
        return CompletableFuture.completedFuture(new StringPacket(pp == null ? null : pp.getName()));
    }
}
