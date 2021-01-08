package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.warpsystem.base.transfer.packets.general.UpdatePlayerDataPacket;
import de.codingair.warpsystem.base.transfer.utils.PlayerData;
import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class PlayerDataManager implements Listener {
    private final ConcurrentHashMap<String, PlayerData> cached = new ConcurrentHashMap<>();

    public UUID get(Player player) {
        return player.getUniqueId();
    }

    public void join(String name, UUID id) {
        cached.put(name.toLowerCase(), new PlayerData(name, id));
    }

    public void quit(String name) {
        cached.remove(name.toLowerCase());
    }

    public Stream<PlayerData> getCached() {
        if(cached.isEmpty()) {
            return Bukkit.getOnlinePlayers().stream().map(player -> new PlayerData(player.getName(), get(player))
                    .setVanished(player.getGameMode() == GameMode.SPECTATOR || player.hasPotionEffect(PotionEffectType.INVISIBILITY)));
        } else return cached.values().stream();
    }

    public PlayerData getCache(Player player) {
        if(player == null) return null;

        if(WarpSystem.getInstance().isOnProxy()) return cached.get(player.getName().toLowerCase());
        else return new PlayerData(player.getName(), player.getUniqueId());
    }

    public PlayerData getCache(String name) {
        if(name == null) return null;

        if(WarpSystem.getInstance().isOnProxy()) return cached.get(name.toLowerCase());
        else {
            Player player = Bukkit.getPlayer(name);
            if(player == null) return null;
            return new PlayerData(player.getName(), player.getUniqueId());
        }
    }

    public void update(UpdatePlayerDataPacket packet) {
        PlayerData data = cached.get(packet.getName().toLowerCase());
        if(data == null) return;
        packet.update(data);

        Player player = Bukkit.getPlayer(data.getId());
        if(player != null) {
            //check attributes
            UpdatePlayerDataPacket response = null;

            if(data.isVanished() != (player.getGameMode() == GameMode.SPECTATOR || player.hasPotionEffect(PotionEffectType.INVISIBILITY))) {
                response = new UpdatePlayerDataPacket(packet.getName());
                response.setVanished(false);
            }

            if(response != null) WarpSystem.getDataHandler().send(response);
        }
    }

    public void updateVisibility(Player player, boolean vanished) {
        PlayerData data = getCache(player);
        if(data == null) return;

        if(data.isVanished() != vanished) {
            data.setVanished(vanished);
            WarpSystem.getDataHandler().send(new UpdatePlayerDataPacket(player.getName()).setVanished(vanished));
        }
    }

    public void flush() {
        cached.clear();
    }

    public void initialize(Collection<PlayerData> data) {
        data.forEach(entry -> cached.put(entry.getName().toLowerCase(), entry));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        UUID id = get(e.getPlayer());
        if(id != null) Bukkit.getPluginManager().callEvent(new PlayerFinalJoinEvent(e.getPlayer(), id, true));
    }
}
