package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.RequestInitialPacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequestInitialPacketHandler implements PacketHandler<RequestInitialPacket> {
    @Override
    public void process(@NotNull RequestInitialPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        WarpSystem.getInstance().getServerManager().sendInitialPacket((ServerInfo) connection);
    }
}
