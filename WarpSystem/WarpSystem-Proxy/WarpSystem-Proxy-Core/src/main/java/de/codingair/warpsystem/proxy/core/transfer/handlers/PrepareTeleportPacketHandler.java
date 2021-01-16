package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.codingapi.utils.Value;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.LongPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.proxy.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.features.TeleportHandler;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Players;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareTeleportPacketHandler implements ResponsiblePacketHandler<PrepareTeleportPacket, LongPacket> {
    @Override
    public @NotNull CompletableFuture<LongPacket> response(@NotNull PrepareTeleportPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player sender = Players.getPlayer(packet.getSender());
        Player targetPlayer = packet.getSender().equalsIgnoreCase(packet.getTarget()) ? sender : Players.getPlayer(packet.getTarget());

        TeleportHandler handler = Core.getPlugin().getHandler(TeleportHandler.class);
        
        if (targetPlayer == null || handler.isAccessible(targetPlayer.getServer())) {
            return CompletableFuture.completedFuture(new LongPacket((((long) 0) << 32)));
        }

        Server<?> target = targetPlayer.getServer();

        String recipient = packet.getRecipient();
        if (recipient == null) {
            //forward to all
            Value<Integer> handled = new Value<>(0);
            Value<Integer> sent = new Value<>(0);

            Core.getServerManager().getOnlineServer().forEach(s -> {
                if (s.equals(connection)) return;
                handled.setValue(handled.getValue() + s.getOnlineCount());
                if (handler.isAccessible(s)) return;

                //tp all
                s.getOnlinePlayers().forEach(player -> {
                    if (handler.deniesForceTps(player)) return;

                    sent.setValue(sent.getValue() + 1);
                    TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(packet.getSender(), player.getName(), targetPlayer.getName(), false);
                    Core.getPlugin().dataHandler().send(ptpPacket, target, Direction.DOWN);
                    player.connect(target);
                });
            });

            return CompletableFuture.completedFuture(new LongPacket((((long) handled.getValue()) << 32) | (sent.getValue() & 0xffffffffL)));
        } else {
            //only recipient
            Player player = Players.getPlayer(packet.getRecipient());

            if (player == null || handler.isAccessible(player.getServer())) {
                //not online/accessible
                return CompletableFuture.completedFuture(new LongPacket(0));
            } else if (handler.deniesForceTps(player) && !player.equals(sender)) {
                //auto deny
                return CompletableFuture.completedFuture(new LongPacket(1L << 32));
            } else {
                if (packet.isCoordsPacket()) {
                    TeleportPlayerToCoordsPacket ptcPacket = new TeleportPlayerToCoordsPacket(
                            packet.getSender(), player.getName(),
                            packet.getX(), packet.getY(), packet.getZ(),
                            false, false, false);

                    Core.getPlugin().dataHandler().send(ptcPacket, target, Direction.DOWN);
                } else {
                    TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(packet.getSender(), player.getName(), targetPlayer.getName(), true);
                    Core.getPlugin().dataHandler().send(ptpPacket, target, Direction.DOWN);
                }
                player.connect(target);

                return CompletableFuture.completedFuture(new LongPacket((((long) 1) << 32) | (1 & 0xffffffffL)));
            }
        }
    }
}
