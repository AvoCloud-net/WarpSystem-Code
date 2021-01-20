package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.codingapi.utils.Value;
import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.packets.impl.LongPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.features.TeleportHandler;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.spigot.PrepareTeleportRequestPacket;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareTeleportRequestPacketHandler implements ResponsibleMultiLayerPacketHandler<PrepareTeleportRequestPacket, LongPacket> {
    @Override
    public @NotNull CompletableFuture<LongPacket> response(@NotNull PrepareTeleportRequestPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        TeleportHandler handler = Core.getPlugin().getHandler(TeleportHandler.class);
        String recipient = packet.getRecipient();

        if (recipient == null) {
            //forward to all
            if (direction == Direction.DOWN) Core.getPlugin().dataHandler().send(packet, null, Direction.UP); //forward to redis

            CompletableFuture<LongPacket> future = new CompletableFuture<>();
            int servers = (int) (Core.getServerManager().getOnlineServer().count() - 1);
            Value<Integer> handled = new Value<>(0);
            Value<Long> generalResult = new Value<>(0L);

            Core.getServerManager().getOnlineServer().forEach(s -> {
                if (s.equals(connection) || handler.isAccessible(s)) return;

                Core.getPlugin().dataHandler().send(new PrepareTeleportRequestPacket(packet.getSender(), null, true), s, Direction.DOWN).thenAccept(result -> {
                    handled.setValue(handled.getValue() + 1);
                    generalResult.setValue(generalResult.getValue() + result.a());

                    if (handled.getValue() == servers) {
                        future.complete(new LongPacket(generalResult.getValue()));
                    }
                });
            });

            return future;
        } else {
            //only recipient
            Player player = Players.getPlayer(packet.getRecipient());
            PlayerData playerData = Core.getPlugin().getPlayerData().getCache(packet.getRecipient());

            if (playerData == null) {
                //not online
                return CompletableFuture.completedFuture(new LongPacket(0));
            }

            Server<?> server = Core.getPlugin().getServer(playerData.getServer());

            if (handler.isAccessible(server) || playerData.isVanished()) {
                //not accessible
                return CompletableFuture.completedFuture(new LongPacket(0));
            }

            if (handler.deniesForceTpRequests(playerData.getName())) {
                //auto deny
                return CompletableFuture.completedFuture(new LongPacket(-1L << 32));
            }

            if (player == null) {
                //redis
                if (direction == Direction.DOWN) throw new Escalation(this, Direction.UP, packet, err -> new LongPacket(-1L << 32));
                else return CompletableFuture.completedFuture(new LongPacket(0));
            } else return Core.getPlugin().dataHandler().send(new PrepareTeleportRequestPacket(packet.getSender(), player.getName(), packet.isTpToSender()), player.getServer(), Direction.DOWN);
        }
    }
}
