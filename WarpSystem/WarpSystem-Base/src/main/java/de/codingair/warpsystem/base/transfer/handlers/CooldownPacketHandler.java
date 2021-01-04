package de.codingair.warpsystem.base.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.features.cooldown.ICooldownManager;
import de.codingair.warpsystem.base.transfer.packets.spigot.CooldownPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CooldownPacketHandler implements PacketHandler<CooldownPacket> {
    private final ICooldownManager manager;

    public CooldownPacketHandler(ICooldownManager manager) {
        this.manager = manager;
    }

    @Override
    public void process(@NotNull CooldownPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        manager.addCooldown(packet.getCooldown());

    }
}
