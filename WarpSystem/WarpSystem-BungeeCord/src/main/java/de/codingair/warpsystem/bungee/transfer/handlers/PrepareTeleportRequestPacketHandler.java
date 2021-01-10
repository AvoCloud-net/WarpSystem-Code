package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.codingapi.utils.Value;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.LongPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.PrepareTeleportRequestPacket;
import de.codingair.warpsystem.bungee.api.Players;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.teleport.TeleportManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareTeleportRequestPacketHandler implements ResponsiblePacketHandler<PrepareTeleportRequestPacket, LongPacket> {
    @Override
    public @NotNull CompletableFuture<LongPacket> response(@NotNull PrepareTeleportRequestPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        String recipient = packet.getRecipient();

        if(recipient == null) {
            //forward to all
            CompletableFuture<LongPacket> future = new CompletableFuture<>();
            int servers = (int) (WarpSystem.getInstance().getServerManager().getOnlineServer().count() - 1);
            Value<Integer> handled = new Value<>(0);
            Value<Long> generalResult = new Value<>(0L);

            WarpSystem.getInstance().getServerManager().getOnlineServer().forEach(s -> {
                if(s.equals(connection) || TeleportManager.getInstance().isAccessible(s)) return;

                WarpSystem.getDataHandler().send(new PrepareTeleportRequestPacket(packet.getSender(), null, true), s, Direction.DOWN).thenAccept(result -> {
                    handled.setValue(handled.getValue() + 1);
                    generalResult.setValue(generalResult.getValue() + result.a());

                    if(handled.getValue() == servers) {
                        future.complete(new LongPacket(generalResult.getValue()));
                    }
                });
            });

            return future;
        } else {
            //only recipient
            ProxiedPlayer player = Players.getPlayer(packet.getRecipient());

            if(player == null || TeleportManager.getInstance().isAccessible(player.getServer().getInfo()) || WarpSystem.getInstance().getPlayerListener().isVanished(player)) {
                //not online/accessible
                return CompletableFuture.completedFuture(new LongPacket(0));
            } else if(TeleportManager.getInstance().deniesForceTpRequests(player)) {
                //auto deny
                return CompletableFuture.completedFuture(new LongPacket(-1L << 32));
            } else return WarpSystem.getDataHandler().send(new PrepareTeleportRequestPacket(packet.getSender(), player.getName(), packet.isTpToSender()), player.getServer().getInfo(), Direction.DOWN);
        }
    }
}
