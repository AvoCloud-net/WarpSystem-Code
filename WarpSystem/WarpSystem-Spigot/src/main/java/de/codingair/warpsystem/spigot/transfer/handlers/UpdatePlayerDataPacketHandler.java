package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.transfer.packets.general.UpdatePlayerDataPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdatePlayerDataPacketHandler implements PacketHandler<UpdatePlayerDataPacket> {
    @Override
    public void process(@NotNull UpdatePlayerDataPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        WarpSystem.getInstance().getPlayerDataManager().update(packet);
    }
}
