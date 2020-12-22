package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.SendOptionsPacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.utils.ServerProvideOptionsEvent;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SendOptionsPacketHandler implements PacketHandler<SendOptionsPacket> {
    @Override
    public void process(@NotNull SendOptionsPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        ((WarpSystem) proxy).getServerManager().applyOptions((ServerInfo) connection, packet.getOptions());
        packet.getOptions().setSameVersion(WarpSystem.getInstance().getDescription().getVersion().equals(packet.getOptions().getVersion()));
        WarpSystem.getInstance().getProxy().getPluginManager().callEvent(new ServerProvideOptionsEvent((ServerInfo) connection, packet.getOptions()));
    }
}
