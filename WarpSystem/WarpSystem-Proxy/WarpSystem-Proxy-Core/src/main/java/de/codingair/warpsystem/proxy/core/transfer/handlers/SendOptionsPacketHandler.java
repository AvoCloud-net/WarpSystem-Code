package de.codingair.warpsystem.proxy.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.SendOptionsPacket;
import de.codingair.warpsystem.base.transfer.utils.serializeable.ServerOptions;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.features.SpawnHandler;
import de.codingair.warpsystem.proxy.core.utils.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SendOptionsPacketHandler implements PacketHandler<SendOptionsPacket> {

    @Override
    public void process(@NotNull SendOptionsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        SpawnHandler handler = Core.getPlugin().getHandler(SpawnHandler.class);
        Core.getServerManager().applyOptions((Server) connection, packet.getOptions());

        packet.getOptions().setSameVersion(Core.getPlugin().getVersion().equals(packet.getOptions().getVersion()));
        callEvent((Server) connection, packet.getOptions());
    }

    public abstract void callEvent(Server connection, ServerOptions options);
}
