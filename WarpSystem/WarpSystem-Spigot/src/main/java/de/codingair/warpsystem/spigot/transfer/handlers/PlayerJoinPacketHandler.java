package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.proxy.PlayerJoinPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerJoinPacketHandler implements PacketHandler<PlayerJoinPacket> {
    @Override
    public void process(@NotNull PlayerJoinPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        WarpSystem.getInstance().getPlayerDataManager().join(packet.getPlayer(), packet.getServer(), packet.getId());
    }
}
