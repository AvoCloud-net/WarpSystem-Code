package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.Value;
import de.codingair.packetmanagement.exceptions.Escalation;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.packets.impl.LongPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.base.handlers.ServerHandler;
import de.codingair.warpsystem.core.proxy.features.TeleportHandler;
import de.codingair.warpsystem.core.proxy.redis.RedisCore;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Players;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.proxy.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.core.transfer.packets.proxy.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareTeleportPacketHandler implements ResponsibleMultiLayerPacketHandler<PrepareTeleportPacket, LongPacket> {
    @Override
    public @NotNull CompletableFuture<LongPacket> response(@NotNull PrepareTeleportPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player sender = Players.getPlayer(packet.getSender());

        TeleportHandler handler = Core.getPlugin().getHandler(TeleportHandler.class);

        Server<?> target;
        String targetName;

        if (!packet.isCoordsPacket()) {
            Player targetPlayer = packet.getSender().equalsIgnoreCase(packet.getTarget()) ? sender : Players.getPlayer(packet.getTarget());
            if (targetPlayer == null) {
                //redis
                System.out.println("(" + direction.name() + ") /tpa " + packet.getTarget() + " -> redis");
                PlayerData data = Core.getPlugin().getPlayerData().getCache(packet.getTarget());
                target = Core.getPlugin().getServer(data.getServer());
                targetName = data.getName();

                if (target == null) return CompletableFuture.completedFuture(new LongPacket((((long) 0) << 32)));
            } else if (!handler.isAccessible(targetPlayer.getServer())) {
                return CompletableFuture.completedFuture(new LongPacket((((long) 0) << 32)));
            } else {
                target = targetPlayer.getServer();
                targetName = targetPlayer.getName();
            }
        } else {
            //always to server of sender
            if (sender == null) {
                //redis
                PlayerData data = Core.getPlugin().getPlayerData().getCache(packet.getSender());
                target = Core.getPlugin().getServer(data.getServer());
                targetName = data.getName();

                if (target == null) return CompletableFuture.completedFuture(new LongPacket((((long) 0) << 32)));
            } else if (!handler.isAccessible(sender.getServer())) {
                return CompletableFuture.completedFuture(new LongPacket((((long) 0) << 32)));
            } else {
                target = sender.getServer();
                targetName = sender.getName();
            }
        }

        String recipient = packet.getRecipient();
        System.out.println("recipient: " + recipient);
        if (recipient == null) {
            //forward to all
            if (direction == Direction.DOWN) Core.getPlugin().dataHandler().send(packet, null, Direction.UP); //redis

            Value<Integer> handled = new Value<>(0);
            Value<Integer> sent = new Value<>(0);

            Core.getServerManager().getOnlineServer().forEach(s -> {
                if (s.equals(connection)) return;
                handled.setValue(handled.getValue() + s.getOnlineCount());
                if (!handler.isAccessible(s)) return;

                //tp all
                s.getOnlinePlayers().forEach(player -> {
                    if (handler.deniesForceTps(player)) return;

                    sent.setValue(sent.getValue() + 1);
                    TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(packet.getSender(), player.getName(), targetName, false);
                    Core.getPlugin().dataHandler().send(ptpPacket, target, Direction.DOWN);
                    player.connect(target);
                });
            });

            return CompletableFuture.completedFuture(new LongPacket((((long) handled.getValue()) << 32) | (sent.getValue() & 0xffffffffL)));
        } else {
            //only recipient
            Player player = Players.getPlayer(packet.getRecipient());
            System.out.println("Player: " + player);

            if (player == null) {
                //redis
                if (direction == Direction.DOWN) throw new Escalation(this, Direction.UP, packet, err -> new LongPacket(0), RedisCore.TIME_OUT);
                else return CompletableFuture.completedFuture(new LongPacket(0));
            } else if (!handler.isAccessible(player.getServer())) {
                //not online/accessible
                return CompletableFuture.completedFuture(new LongPacket(0));
            } else if (handler.deniesForceTps(player) && !player.equals(sender)) {
                //auto deny
                return CompletableFuture.completedFuture(new LongPacket(1L << 32));
            } else {
                System.out.println("send " + player.getName() + " to " + target.getName());
                ServerHandler.sendPlayerTo(target, player, new Callback<Server<?>>() {
                    @Override
                    public void accept(Server<?> target) {
                        if (packet.isCoordsPacket()) {
                            TeleportPlayerToCoordsPacket ptcPacket = new TeleportPlayerToCoordsPacket(
                                    packet.getSender(), player.getName(),
                                    packet.getX(), packet.getY(), packet.getZ(),
                                    false, false, false);

                            Core.getPlugin().dataHandler().send(ptcPacket, target, Direction.DOWN);
                        } else {
                            TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(packet.getSender(), player.getName(), targetName, true);
                            Core.getPlugin().dataHandler().send(ptpPacket, target, Direction.DOWN);
                        }
                    }
                });

                return CompletableFuture.completedFuture(new LongPacket((((long) 1) << 32) | (1 & 0xffffffffL)));
            }
        }
    }
}
