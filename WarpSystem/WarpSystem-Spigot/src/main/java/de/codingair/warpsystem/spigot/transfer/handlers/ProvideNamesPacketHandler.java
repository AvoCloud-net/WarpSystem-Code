package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.proxy.ProvidePlayerDataPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProvideNamesPacketHandler implements PacketHandler<ProvidePlayerDataPacket> {
    @Override
    public void process(@NotNull ProvidePlayerDataPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        WarpSystem.getInstance().getPlayerDataManager().initialize(packet.getData());
    }
}
