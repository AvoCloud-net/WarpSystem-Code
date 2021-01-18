package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.spigot.RequestServerStatusPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.utils.ServerPing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RequestServerStatusPacketHandler implements ResponsiblePacketHandler<RequestServerStatusPacket, BooleanPacket> {
    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull RequestServerStatusPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Server server = Core.getPlugin().getServer(packet.getServer());

        if (server == null) {
            System.out.println("Server '" + packet.getServer() + "' is null");
            return CompletableFuture.completedFuture(new BooleanPacket(false));
        } else {
            ServerPing ping = Core.getServerManager().getLastPing(server);
            System.out.println("Check last ping for '" + packet.getServer() + "': " + (ping != null) + " && " + (ping != null && ping.getStatus()));
            return CompletableFuture.completedFuture(new BooleanPacket(ping != null && ping.getStatus()));
        }
    }
}
