package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.warpsystem.base.transfer.packets.proxy.SendServerPropertiesPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.signs.managers.SignManager;
import org.bukkit.Bukkit;

import java.util.HashMap;

public class ServerManager {
    private final HashMap<String, ServerPing> properties = new HashMap<>();

    public ServerManager() {
        WarpSystem.getDataHandler().registerHandler(SendServerPropertiesPacket.class, (packet, proxy, connection, direction) -> {
            properties.putAll(packet.getProperties());
            packet.getProperties().clear();
            onUpdate();
        });
    }

    public ServerPing getProperties(String server) {
        return properties.get(server.toLowerCase());
    }

    public void onUpdate() {
        Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
            if(FeatureType.SIGNS.isActive()) SignManager.getInstance().updateAll();
        });
    }
}
