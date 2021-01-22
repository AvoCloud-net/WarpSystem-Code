package de.codingair.warpsystem.spigot.base.managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.warpsystem.core.transfer.packets.general.UpdatePlayerDataPacket;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import de.codingair.warpsystem.spigot.api.events.PlayerFinalJoinEvent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class PlayerDataManager implements Listener {
    private final Cache<String, String> abbreviations = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    private final ConcurrentHashMap<String, PlayerData> cached = new ConcurrentHashMap<>();

    public UUID get(Player player) {
        return player.getUniqueId();
    }

    public void join(String name, String server, UUID id) {
        cached.put(name.toLowerCase(), new PlayerData(name, id, server));
    }

    public void quit(String name) {
        cached.remove(name.toLowerCase());
    }

    public Stream<PlayerData> getCached() {
        if (cached.isEmpty()) {
            return Bukkit.getOnlinePlayers().stream().map(player -> new PlayerData(player.getName(), get(player))
                    .setVanished(player.getGameMode() == GameMode.SPECTATOR));
        } else return cached.values().stream();
    }

    public PlayerData getCache(Player player) {
        if (player == null) return null;

        if (WarpSystem.getInstance().isOnProxy()) return cached.get(player.getName().toLowerCase());
        else return new PlayerData(player.getName(), player.getUniqueId());
    }

    public boolean isPresent(String name) {
        return getCache(name) != null;
    }

    public PlayerData getCache(String name) {
        if (name == null) return null;

        PlayerData data = getCacheExact(name);
        if (data != null) return data;

        String lowerName = name.toLowerCase(Locale.ENGLISH);
        String abbreviation = abbreviations.getIfPresent(lowerName);
        if (abbreviation != null) return getCacheExact(abbreviation);

        int delta = 2147483647;
        for (String player : cached.keySet()) {
            if (player.startsWith(lowerName)) {
                int curDelta = Math.abs(player.length() - lowerName.length());
                if (curDelta < delta) {
                    abbreviation = player;
                    delta = curDelta;
                }

                if (curDelta == 0) break;
            }
        }

        if (abbreviation != null) abbreviations.put(lowerName, abbreviation);
        return getCacheExact(abbreviation);
    }

    public PlayerData getCacheExact(String name) {
        if (name == null) return null;

        if (WarpSystem.getInstance().isOnProxy()) return cached.get(name.toLowerCase());
        else {
            Player player = Bukkit.getPlayer(name);
            if (player == null) return null;
            return new PlayerData(player.getName(), player.getUniqueId());
        }
    }

    public void update(UpdatePlayerDataPacket packet, Player connection) {
        PlayerData data = cached.get(packet.getName().toLowerCase());
        if (data == null) return;
        packet.update(data);

        Player player = Bukkit.getPlayer(data.getId());
        if (player != null) {
            //check attributes
            updatePlayer(connection, data, player);
        }
    }

    private void updatePlayer(Player connection, PlayerData data, Player player) {
        UpdatePlayerDataPacket response = null;

        boolean vanished;
        if (data.isVanished() != (vanished = (player.getGameMode() == GameMode.SPECTATOR))) {
            response = new UpdatePlayerDataPacket(player.getName());
            response.setVanished(vanished);
            data.setVanished(vanished);
        }

        if (response != null) WarpSystem.getDataHandler().send(response, connection);
    }

    public void updateVisibility(Player player, boolean vanished) {
        PlayerData data = getCache(player);
        if (data == null) return;

        if (data.isVanished() != vanished) {
            data.setVanished(vanished);
            WarpSystem.getDataHandler().send(new UpdatePlayerDataPacket(player.getName()).setVanished(vanished), player);
        }
    }

    public void flush() {
        cached.clear();
    }

    public void apply(Collection<PlayerData> data, Player connection) {
        data.forEach(entry -> {
            cached.put(entry.getName().toLowerCase(), entry);

            Player p = Bukkit.getPlayerExact(entry.getName());
            if(p != null) updatePlayer(connection, entry, p);
        });
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        UUID id = get(e.getPlayer());
        if (id != null) Bukkit.getPluginManager().callEvent(new PlayerFinalJoinEvent(e.getPlayer(), id, true));
    }
}
