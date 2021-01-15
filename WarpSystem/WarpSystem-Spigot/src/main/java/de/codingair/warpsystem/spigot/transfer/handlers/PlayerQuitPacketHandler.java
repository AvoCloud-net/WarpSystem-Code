package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.proxy.PlayerQuitPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerQuitPacketHandler implements PacketHandler<PlayerQuitPacket> {
    @Override
    public void process(@NotNull PlayerQuitPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        WarpSystem.getInstance().getPlayerDataManager().quit(packet.getPlayer());
    }
}
