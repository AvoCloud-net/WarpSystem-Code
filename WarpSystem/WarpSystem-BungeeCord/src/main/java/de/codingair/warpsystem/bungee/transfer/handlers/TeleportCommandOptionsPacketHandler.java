package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportCommandOptionsPacket;
import de.codingair.warpsystem.bungee.features.teleport.TeleportManager;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportCommandOptionsPacketHandler implements PacketHandler<TeleportCommandOptionsPacket> {
    @Override
    public void process(@NotNull TeleportCommandOptionsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        TeleportManager.getInstance().registerOptions((ServerInfo) connection, packet);
    }
}
