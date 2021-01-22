package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.features.TeleportHandler;
import de.codingair.warpsystem.core.proxy.redis.RedisCore;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportCommandOptionsPacket;
import de.codingair.warpsystem.core.transfer.packets.proxy.InitialPacket;
import de.codingair.warpsystem.core.transfer.utils.TeleportCommandOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class InitialPacketHandler implements PacketHandler<InitialPacket> {
    @Override
    public void process(@NotNull InitialPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if (direction == Direction.UP) {
            RedisCore.recognize(packet.getServerName());

            //player data
            Core.getPlugin().getPlayerData().buildPlayerDataPackets(p -> Core.getPlugin().dataHandler().send(p, null, Direction.UP));

            //teleport options
            TeleportHandler handler = Core.getPlugin().getHandler(TeleportHandler.class);
            for (Map.Entry<String, TeleportCommandOptions> e : handler.getCommandOptions().entrySet()) {
                Core.getPlugin().dataHandler().send(new TeleportCommandOptionsPacket(e.getKey(), e.getValue()), null, Direction.UP);
            }
        }
    }
}
