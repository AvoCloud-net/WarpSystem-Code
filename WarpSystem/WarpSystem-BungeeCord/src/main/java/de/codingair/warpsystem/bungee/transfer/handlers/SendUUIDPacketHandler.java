package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.proxy.SendUUIDPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.RequestUUIDPacket;
import de.codingair.warpsystem.bungee.api.Players;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SendUUIDPacketHandler implements ResponsiblePacketHandler<RequestUUIDPacket, SendUUIDPacket> {
    @Override
    public @NotNull CompletableFuture<SendUUIDPacket> response(@NotNull RequestUUIDPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        ProxiedPlayer pp = Players.getPlayer(packet.getPlayer());
        return CompletableFuture.completedFuture(new SendUUIDPacket(pp == null ? null : pp.getUniqueId()));
    }
}
