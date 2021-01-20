package de.codingair.warpsystem.core.proxy.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.transfer.packets.proxy.InitialPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InitialPacketHandler implements PacketHandler<InitialPacket> {
    @Override
    public void process(@NotNull InitialPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        if (direction == Direction.UP) {
            System.out.println("Another proxy has been connected! (" + packet.getServerName() + ")");
            //redis
            Core.getPlugin().getPlayerData().buildPlayerDataPackets(p -> {
                System.out.println("Send data");
                Core.getPlugin().dataHandler().send(p, null, Direction.UP);
            });
        }
    }
}
