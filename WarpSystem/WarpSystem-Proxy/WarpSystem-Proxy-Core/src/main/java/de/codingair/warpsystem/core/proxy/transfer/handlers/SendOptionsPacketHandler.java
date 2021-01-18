package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.spigot.SendOptionsPacket;
import de.codingair.warpsystem.core.transfer.utils.serializeable.ServerOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SendOptionsPacketHandler implements PacketHandler<SendOptionsPacket> {

    @Override
    public void process(@NotNull SendOptionsPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        Core.getServerManager().applyOptions((Server<?>) connection, packet.getOptions());

        packet.getOptions().setSameVersion(Core.getPlugin().getVersion().equals(packet.getOptions().getVersion()));
        callEvent((Server<?>) connection, packet.getOptions());
    }

    public abstract void callEvent(Server<?> connection, ServerOptions options);
}
