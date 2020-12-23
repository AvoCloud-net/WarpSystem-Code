package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RandomTPPacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.managers.ServerManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RandomTPPacketHandler implements ResponsiblePacketHandler<RandomTPPacket, BooleanPacket> {
    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull RandomTPPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        ProxiedPlayer pp = WarpSystem.proxy().getPlayer(packet.getPlayer());
        ServerInfo target = WarpSystem.proxy().getServerInfo(packet.getServer());

        if(target == null || !WarpSystem.getInstance().getServerManager().isOnline(target)) return CompletableFuture.completedFuture(new BooleanPacket(false));

        if(pp != null) {
            packet.setServer(((ServerInfo) connection).getName());
            ServerManager.sendPlayerTo(target, pp, new Callback<ServerInfo>() {
                @Override
                public void accept(ServerInfo server) {
                    WarpSystem.getDataHandler().send(packet, server, Direction.DOWN);
                }
            });

            return CompletableFuture.completedFuture(new BooleanPacket(true));
        } else return CompletableFuture.completedFuture(new BooleanPacket(false));
    }
}
