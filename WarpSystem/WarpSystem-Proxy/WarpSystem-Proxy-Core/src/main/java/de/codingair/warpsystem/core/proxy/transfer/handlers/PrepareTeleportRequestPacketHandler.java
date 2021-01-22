package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.exceptions.TimeOutException;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.packets.impl.LongPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.features.TeleportHandler;
import de.codingair.warpsystem.core.proxy.redis.RedisCore;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.spigot.PrepareTeleportRequestPacket;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PrepareTeleportRequestPacketHandler implements ResponsibleMultiLayerPacketHandler<PrepareTeleportRequestPacket, LongPacket> {

    @Override
    public boolean answer(@NotNull PrepareTeleportRequestPacket packet, @NotNull Proxy proxy, @NotNull Direction direction) {
        //redis
        //we might not be able to handle this packet!
        return direction == Direction.DOWN || packet.getRecipient() == null || Players.getPlayer(packet.getRecipient()) != null;
    }

    @Override
    public @NotNull CompletableFuture<LongPacket> response(@NotNull PrepareTeleportRequestPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        TeleportHandler handler = Core.getPlugin().getHandler(TeleportHandler.class);
        if (handler == null) return CompletableFuture.completedFuture(new LongPacket(0));

        String recipient = packet.getRecipient();

        if (recipient == null) {
            //forward to all
            CompletableFuture<LongPacket> future = new CompletableFuture<>();
            AtomicLong l = new AtomicLong();

            if (direction == Direction.DOWN && RedisCore.ready()) {
                //forward to redis
                Core.getPlugin().dataHandler().send(packet.mergeFuture(
                        RedisCore.core().getProxies().size(),
                        (longPacket, longPacket2) -> new LongPacket(longPacket.a() + longPacket2.a())
                ), null, Direction.UP, 400).whenComplete((result, err) -> {
                    if (err instanceof TimeOutException) {
                        result = ((TimeOutException) err).getMerged(new LongPacket(0));
                    } else if (err != null) {
                        err.printStackTrace();
                    }

                    l.getAndAdd(result.a());
                    future.complete(new LongPacket(l.get()));
                });
            }

            AtomicInteger i = new AtomicInteger();
            int servers = (int) Core.getServerManager().getOnlineServer().filter(s -> !s.isEmpty() && !s.equals(connection) && handler.isAccessible(s)).count();
            Core.getServerManager().getOnlineServer().filter(s -> !s.isEmpty() && !s.equals(connection) && handler.isAccessible(s)).forEach(s -> {
                Core.getPlugin().dataHandler().send(new PrepareTeleportRequestPacket(packet.getSender(), null, true), s, Direction.DOWN).thenAccept(result -> {
                    l.getAndAdd(result.a());

                    //complete redis
                    if (direction == Direction.UP || !RedisCore.ready()) {
                        if (i.incrementAndGet() == servers) {
                            future.complete(new LongPacket(l.get()));
                        }
                    }
                });
            });

            if (servers == 0 && (!RedisCore.ready() || direction == Direction.UP)) {
                return CompletableFuture.completedFuture(new LongPacket(0));
            }
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

            if (!handler.isAccessible(server) || playerData.isVanished()) {
                //not accessible
                return CompletableFuture.completedFuture(new LongPacket(0));
            }

            if (handler.deniesForceTpRequests(playerData.getName())) {
                //auto deny
                return CompletableFuture.completedFuture(new LongPacket(-1L << 32));
            }

            if (player == null) {
                //redis
                if (direction == Direction.DOWN) {
                    throw new Escalation(this, Direction.UP, packet, err -> {
                        err.printStackTrace();
                        return new LongPacket(-1L << 32);
                    });
                }
                else return CompletableFuture.completedFuture(new LongPacket(0));
            } else return Core.getPlugin().dataHandler().send(new PrepareTeleportRequestPacket(packet.getSender(), player.getName(), packet.isTpToSender()), player.getServer(), Direction.DOWN);
        }
    }
}
