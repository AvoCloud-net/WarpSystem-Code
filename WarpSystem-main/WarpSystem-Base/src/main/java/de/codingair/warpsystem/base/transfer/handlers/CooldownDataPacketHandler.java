package de.codingair.warpsystem.base.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.features.cooldown.Cooldown;
import de.codingair.warpsystem.base.features.cooldown.ICooldownManager;
import de.codingair.warpsystem.base.transfer.packets.spigot.CooldownDataPacket;

public class CooldownDataPacketHandler implements PacketHandler<CooldownDataPacket> {
    private final ICooldownManager manager;

    public CooldownDataPacketHandler(ICooldownManager manager) {
        this.manager = manager;
    }

    @Override
    public void process(CooldownDataPacket packet, Proxy proxy) {
        for(Cooldown c : packet.getCooldown()) {
            manager.addCooldown(c);
        }
    }
}
