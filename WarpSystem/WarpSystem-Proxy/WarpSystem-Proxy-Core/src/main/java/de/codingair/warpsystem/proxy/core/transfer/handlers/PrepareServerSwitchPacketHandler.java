package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.IntegerPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.proxy.PrepareLoginMessagePacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.PrepareServerSwitchPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.base.handlers.ServerHandler;
import de.codingair.warpsystem.proxy.core.utils.Player;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PrepareServerSwitchPacketHandler implements ResponsiblePacketHandler<PrepareServerSwitchPacket, IntegerPacket> {
    @Override
    public @NotNull CompletableFuture<IntegerPacket> response(@NotNull PrepareServerSwitchPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Player pp = Core.getPlugin().getPlayer(packet.getPlayer());
        Server<?> info = Core.getPlugin().getServer(packet.getServer());

        if (pp == null || info == null) return CompletableFuture.completedFuture(new IntegerPacket(1));
        else {
            if (pp.getServer() == info) return CompletableFuture.completedFuture(new IntegerPacket(2));

            if (Core.getServerManager().isOnline(info)) {
                ServerPing ping = Core.getServerManager().getLastPing(info);

                if (ping == null) return CompletableFuture.completedFuture(new IntegerPacket(4));
                else {
                    if (packet.isIgnoreLimit() || info.getOnlineCount() < ping.getMaxPlayers()) {
                        CompletableFuture<IntegerPacket> future = new CompletableFuture<>();

                        ServerHandler.sendPlayerTo(info, pp, new Callback<Server<?>>() {
                            @Override
                            public void accept(Server<?> object) {
                                Core.getPlugin().dataHandler().send(new PrepareLoginMessagePacket(pp.getName(), packet.getMessage()), info, Direction.DOWN);
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
