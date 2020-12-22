package de.codingair.warpsystem.base.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.features.cooldown.Cooldown;
import de.codingair.warpsystem.base.features.cooldown.ICooldownManager;
import de.codingair.warpsystem.base.transfer.packets.spigot.CooldownDataPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.CooldownPacket;

public class CooldownPacketHandler implements PacketHandler<CooldownPacket> {
    private final ICooldownManager manager;

    public CooldownPacketHandler(ICooldownManager manager) {
        this.manager = manager;
    }

    @Override
    public void process(CooldownPacket packet, Proxy proxy) {
        manager.addCooldown(packet.getCooldown());

    }
}
