package de.codingair.warpsystem.bungee.features.spawn.listeners;

import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.base.transfer.packets.general.TeleportSpawnPacket;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.base.utils.ServerProvideOptionsEvent;
import de.codingair.warpsystem.bungee.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.bungee.transfer.handlers.SendGlobalSpawnOptionsPacketHandler;
import de.codingair.warpsystem.bungee.transfer.handlers.TeleportSpawnPacketHandler;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerListener implements Listener {
    public ServerListener() {
        WarpSystem.getDataHandler().registerHandler(SendGlobalSpawnOptionsPacket.class, new SendGlobalSpawnOptionsPacketHandler());
        WarpSystem.getDataHandler().registerHandler(TeleportSpawnPacket.class, new TeleportSpawnPacketHandler());
    }

    @EventHandler
    public void onInit(ServerProvideOptionsEvent e) {
        if(!e.getOptions().sameVersion()) return;
        WarpSystem.getDataHandler().send(SpawnManager.getInstance().getInfoPacket(), e.getInfo(), Direction.DOWN);
    }
}
