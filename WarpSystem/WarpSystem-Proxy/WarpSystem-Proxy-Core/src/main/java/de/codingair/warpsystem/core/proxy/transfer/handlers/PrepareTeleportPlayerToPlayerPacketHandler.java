package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.base.handlers.ServerHandler;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.proxy.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.PrepareTeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareTeleportPlayerToPlayerPacketHandler implements ResponsibleMultiLayerPacketHandler<PrepareTeleportPlayerToPlayerPacket, IntegerPacket> {

    @Override
    public boolean answer(@NotNull PrepareTeleportPlayerToPlayerPacket packet, @NotNull Proxy proxy, @NotNull Direction direction) {
        //redis
        //we might not be able to handle this packet!
        return direction == Direction.DOWN || Players.getPlayer(packet.getPlayer()) != null;
    }

    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull PrepareTeleportPlayerToPlayerPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player player = Players.getPlayer(packet.getPlayer());
        PlayerData target = Core.getPlugin().getPlayerData().getCache(packet.getDestinationPlayer());

        if (player == null) {
            if (direction == Direction.DOWN) throw new Escalation(this, Direction.UP, packet, err -> new IntegerPacket(1));
            else return CompletableFuture.completedFuture(new IntegerPacket(1));
        }

        if (target == null) return CompletableFuture.completedFuture(new IntegerPacket(1));
        Server<?> targetServer = Core.getPlugin().getServer(target.getServer());
        if (targetServer == null) return CompletableFuture.completedFuture(new IntegerPacket(1));

        TeleportPlayerToPlayerPacket tpPacket = new TeleportPlayerToPlayerPacket(player.getName(), player.getName(), target.getName());
        tpPacket.setCosts(packet.getCosts());

        CompletableFuture<IntegerPacket> future = new CompletableFuture<>();
        
        if (!player.getServer().equals(targetServer)) {
            ServerHandler.sendPlayerTo(player, targetServer).whenComplete((res, t) -> {
                if(t != null) t.printStackTrace();
                else if(res.isConnected()) {
                    future.complete(new IntegerPacket(0));
                    Core.getPlugin().dataHandler().send(tpPacket, targetServer, Direction.DOWN);
                    return;
                }

                future.complete(new IntegerPacket(1));
            });
        } else {
            Core.getPlugin().dataHandler().send(tpPacket, targetServer, Direction.DOWN);
            return CompletableFuture.completedFuture(new IntegerPacket(0));
        }

        return future;
    }
}
