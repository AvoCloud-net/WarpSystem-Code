package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.proxy.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.PrepareTeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.bungee.api.Players;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareTeleportPlayerToPlayerPacketHandler implements ResponsiblePacketHandler<PrepareTeleportPlayerToPlayerPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull PrepareTeleportPlayerToPlayerPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        ProxiedPlayer player = Players.getPlayer(packet.getPlayer());
        ProxiedPlayer target = Players.getPlayer(packet.getDestinationPlayer());

        if(player == null || target == null) return CompletableFuture.completedFuture(new IntegerPacket(1));
        else {
            TeleportPlayerToPlayerPacket tpPacket = new TeleportPlayerToPlayerPacket(player.getName(), player.getName(), target.getName());
            tpPacket.setCosts(packet.getCosts());
            WarpSystem.getDataHandler().send(tpPacket, target.getServer().getInfo(), Direction.DOWN);

            CompletableFuture<IntegerPacket> future = new CompletableFuture<>();
            if(!player.getServer().getInfo().equals(target.getServer().getInfo())) {
                player.connect(target.getServer().getInfo(), (connected, throwable) -> {
                    if(!connected) future.complete(new IntegerPacket(2));
                    else future.complete(new IntegerPacket(0));
                });
            } else return CompletableFuture.completedFuture(new IntegerPacket(0));

            return future;
        }
    }
}
