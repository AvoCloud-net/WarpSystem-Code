package de.codingair.warpsystem.bungee.transfer.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportSpawnPacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.language.Lang;
import de.codingair.warpsystem.bungee.base.managers.ServerManager;
import de.codingair.warpsystem.bungee.features.spawn.managers.SpawnManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportSpawnPacketHandler implements PacketHandler<TeleportSpawnPacket> {
    @Override
    public void process(@NotNull TeleportSpawnPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        ProxiedPlayer player = WarpSystem.proxy().getPlayer(packet.getPlayer());
        ServerInfo server = WarpSystem.proxy().getServerInfo(packet.isRespawn() ? SpawnManager.getInstance().getRespawn() : SpawnManager.getInstance().getSpawn());

        if(player != null && server != null) {
            if(WarpSystem.getInstance().getServerManager().isOnline(server)) {
                ServerManager.sendPlayerTo(server, player, new Callback<ServerInfo>() {
                    @Override
                    public void accept(ServerInfo server) {
                        WarpSystem.getDataHandler().send(packet, server, Direction.DOWN);
                    }
                });
            } else player.sendMessage(new TextComponent(Lang.getPrefix() + Lang.get("Server_Is_Not_Online")));
        }
    }
}
