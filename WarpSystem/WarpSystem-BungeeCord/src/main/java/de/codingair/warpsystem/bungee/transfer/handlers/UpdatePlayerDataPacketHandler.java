package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.UpdatePlayerDataPacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdatePlayerDataPacketHandler implements PacketHandler<UpdatePlayerDataPacket> {
    @Override
    public void process(@NotNull UpdatePlayerDataPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        WarpSystem.getInstance().getPlayerListener().onUpdate(packet, (ServerInfo) connection);
    }
}
