package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportCommandOptionsPacket;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportCommandOptionsPacketHandler implements PacketHandler<TeleportCommandOptionsPacket> {
    @Override
    public void process(@NotNull TeleportCommandOptionsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        TeleportCommandManager man = TeleportCommandManager.getInstance();
        if(man != null) man.registerServerOptions(packet.getServer(), packet.getOptions());
    }
}
