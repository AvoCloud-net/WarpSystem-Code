package de.codingair.warpsystem.core.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.core.features.cooldown.Cooldown;
import de.codingair.warpsystem.core.features.cooldown.ICooldownManager;
import de.codingair.warpsystem.core.transfer.packets.spigot.CooldownDataPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CooldownDataPacketHandler implements PacketHandler<CooldownDataPacket> {
    private final ICooldownManager manager;

    public CooldownDataPacketHandler(ICooldownManager manager) {
        this.manager = manager;
    }

    @Override
    public void process(@NotNull CooldownDataPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        for (Cooldown c : packet.getCooldown()) {
            manager.addCooldown(c);
        }
    }
}
