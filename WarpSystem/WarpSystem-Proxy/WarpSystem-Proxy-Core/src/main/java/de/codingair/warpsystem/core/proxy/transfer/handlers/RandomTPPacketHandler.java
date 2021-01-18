package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.base.handlers.ServerHandler;
import de.codingair.warpsystem.core.transfer.packets.spigot.RandomTPPacket;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Player;
import de.codingair.warpsystem.core.proxy.utils.Server;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RandomTPPacketHandler implements ResponsiblePacketHandler<RandomTPPacket, BooleanPacket> {
    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull RandomTPPacket packet, @NotNull Proxy proxy, Object connection, @NotNull Direction direction) {
        Player pp = Core.getPlugin().getPlayer(packet.getPlayer());
        Server<?> target = Core.getPlugin().getServer(packet.getServer());

        if (target == null || !Core.getServerManager().isOnline(target)) return CompletableFuture.completedFuture(new BooleanPacket(false));

        if (pp != null) {
            packet.setServer(((Server<?>) connection).getName());
            ServerHandler.sendPlayerTo(target, pp, new Callback<Server<?>>() {
                @Override
                public void accept(Server<?> server) {
                    Core.getPlugin().dataHandler().send(packet, server, Direction.DOWN);
                }
            });

            return CompletableFuture.completedFuture(new BooleanPacket(true));
        } else return CompletableFuture.completedFuture(new BooleanPacket(false));
    }
}
