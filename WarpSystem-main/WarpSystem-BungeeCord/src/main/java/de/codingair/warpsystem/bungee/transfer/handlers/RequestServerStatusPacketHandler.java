package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.impl.BooleanPacket;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RequestServerStatusPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RequestServerStatusPacketHandler implements ResponsiblePacketHandler<RequestServerStatusPacket, BooleanPacket> {
    @Override
    public @NotNull CompletableFuture<BooleanPacket> response(@NotNull RequestServerStatusPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        ServerInfo info = WarpSystem.proxy().getServerInfo(packet.getServer());

        if(info == null) return CompletableFuture.completedFuture(new BooleanPacket(false));
        else {
            ServerPing ping = WarpSystem.getInstance().getServerManager().getLastPing(info);
            return CompletableFuture.completedFuture(new BooleanPacket(ping != null && ping.getStatus()));
        }
    }
}
