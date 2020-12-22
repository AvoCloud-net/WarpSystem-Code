package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.spigot.MessagePacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessagePacketHandler implements PacketHandler<MessagePacket> {
    @Override
    public void process(@NotNull MessagePacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
        ProxiedPlayer player = WarpSystem.proxy().getPlayer(packet.getPlayer());

        if(player != null) {
            TextComponent tc = new TextComponent(packet.getMessage());
            tc.setColor(ChatColor.GRAY);
            player.sendMessage(tc);
        }
    }
}
