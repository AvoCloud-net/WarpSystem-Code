package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.codingapi.utils.Value;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.LongPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.proxy.TeleportPlayerToCoordsPacket;
import de.codingair.warpsystem.base.transfer.packets.proxy.TeleportPlayerToPlayerPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.bungee.api.Players;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.teleport.managers.TeleportManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareTeleportPacketHandler implements ResponsiblePacketHandler<PrepareTeleportPacket, LongPacket> {
    @Override
    public @NotNull CompletableFuture<LongPacket> response(@NotNull PrepareTeleportPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        ProxiedPlayer sender = Players.getPlayer(packet.getSender());
        ProxiedPlayer targetPlayer = packet.getSender().equalsIgnoreCase(packet.getTarget()) ? sender : Players.getPlayer(packet.getTarget());

        if(targetPlayer == null || TeleportManager.getInstance().isAccessible(targetPlayer.getServer().getInfo())) {
            return CompletableFuture.completedFuture(new LongPacket((((long) 0) << 32)));
        }

        ServerInfo target = targetPlayer.getServer().getInfo();

        String recipient = packet.getRecipient();
        if(recipient == null) {
            //forward to all
            Value<Integer> handled = new Value<>(0);
            Value<Integer> sent = new Value<>(0);

            WarpSystem.getInstance().getServerManager().getOnlineServer().forEach(s -> {
                if(s.equals(connection)) return;
                handled.setValue(handled.getValue() + s.getPlayers().size());
                if(TeleportManager.getInstance().isAccessible(s)) return;

                //tp all
                for(ProxiedPlayer player : s.getPlayers()) {
                    if(TeleportManager.getInstance().deniesForceTps(player)) return;

                    sent.setValue(sent.getValue() + 1);
                    TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(packet.getSender(), player.getName(), targetPlayer.getName(), false);
                    WarpSystem.getDataHandler().send(ptpPacket, target, Direction.DOWN);
                    player.connect(target);
                }
            });

            return CompletableFuture.completedFuture(new LongPacket((((long) handled.getValue()) << 32) | (sent.getValue() & 0xffffffffL)));
        } else {
            //only recipient
            ProxiedPlayer player = Players.getPlayer(packet.getRecipient());

            if(player == null || TeleportManager.getInstance().isAccessible(player.getServer().getInfo())) {
                //not online/accessible
                return CompletableFuture.completedFuture(new LongPacket(0));
            } else if(TeleportManager.getInstance().deniesForceTps(player) && !player.equals(sender)) {
                //auto deny
                return CompletableFuture.completedFuture(new LongPacket(1L << 32));
            } else {
                if(packet.isCoordsPacket()) {
                    TeleportPlayerToCoordsPacket ptcPacket = new TeleportPlayerToCoordsPacket(
                            packet.getSender(), player.getName(),
                            packet.getX(), packet.getY(), packet.getZ(),
                            false, false, false);

                    WarpSystem.getDataHandler().send(ptcPacket, target, Direction.DOWN);
                } else {
                    TeleportPlayerToPlayerPacket ptpPacket = new TeleportPlayerToPlayerPacket(packet.getSender(), player.getName(), targetPlayer.getName(), true);
                    WarpSystem.getDataHandler().send(ptpPacket, target, Direction.DOWN);
                }
                player.connect(target);

                return CompletableFuture.completedFuture(new LongPacket((((long) 1) << 32) | (1 & 0xffffffffL)));
            }
        }
    }
}
