package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.bungee.SendServerPropertiesPacket;
import de.codingair.warpsystem.base.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.signs.managers.SignManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ServerManager {
    private final HashMap<String, ServerPing> properties = new HashMap<>();

    public ServerManager() {
        WarpSystem.getDataHandler().registerHandler(SendServerPropertiesPacket.class, new PacketHandler<SendServerPropertiesPacket>() {
            @Override
            public void process(@NotNull SendServerPropertiesPacket packet, @NotNull Proxy proxy, @Nullable Object connection) {
                properties.putAll(packet.getProperties());
                packet.getProperties().clear();
                onUpdate();
            }
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
