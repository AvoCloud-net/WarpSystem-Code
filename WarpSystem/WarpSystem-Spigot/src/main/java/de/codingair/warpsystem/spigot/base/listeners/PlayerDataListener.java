package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class PlayerDataListener implements Listener {

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        WarpSystem.getInstance().getPlayerDataManager().updateVisibility(e.getPlayer(), e.getNewGameMode() == GameMode.SPECTATOR);
    }
}
