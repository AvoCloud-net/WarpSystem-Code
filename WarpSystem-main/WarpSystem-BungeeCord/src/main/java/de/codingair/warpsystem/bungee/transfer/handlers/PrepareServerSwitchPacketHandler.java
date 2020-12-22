package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.bungee.PrepareLoginMessagePacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.PrepareServerSwitchPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.managers.ServerManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareServerSwitchPacketHandler implements ResponsiblePacketHandler<PrepareServerSwitchPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull PrepareServerSwitchPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        ProxiedPlayer pp = WarpSystem.proxy().getPlayer(packet.getPlayer());
        ServerInfo info = WarpSystem.proxy().getServerInfo(packet.getServer());

        if(pp == null || info == null) return CompletableFuture.completedFuture(new IntegerPacket(1));
        else {
            if(pp.getServer().getInfo() == info) return CompletableFuture.completedFuture(new IntegerPacket(2));

            if(WarpSystem.getInstance().getServerManager().isOnline(info)) {
                ServerPing ping = WarpSystem.getInstance().getServerManager().getLastPing(info);

                if(ping == null) return CompletableFuture.completedFuture(new IntegerPacket(4));
                else {
                    if(packet.isIgnoreLimit() || info.getPlayers().size() < ping.getMaxPlayers()) {
                        CompletableFuture<IntegerPacket> future = new CompletableFuture<>();

                        ServerManager.sendPlayerTo(info, pp, new Callback<ServerInfo>() {
                            @Override
                            public void accept(ServerInfo object) {
                                WarpSystem.getDataHandler().send(new PrepareLoginMessagePacket(pp.getName(), packet.getMessage()), info, Direction.DOWN);
                                future.complete(new IntegerPacket(0));
                            }
                        });

                        return future;
                    } else return CompletableFuture.completedFuture(new IntegerPacket(5));
                }
            } else return CompletableFuture.completedFuture(new IntegerPacket(3));
        }
    }
}
