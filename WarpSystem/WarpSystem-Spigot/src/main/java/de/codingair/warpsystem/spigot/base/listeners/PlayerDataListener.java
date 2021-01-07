package de.codingair.warpsystem.spigot.base.listeners;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerDataListener implements Listener {

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        WarpSystem.getInstance().getPlayerDataManager().updateVisibility(e.getPlayer(), e.getNewGameMode() == GameMode.SPECTATOR);
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent e) {
        if(e.getEntity() instanceof PlayerEvent && e.getModifiedType() == PotionEffectType.INVISIBILITY) {
                WarpSystem.getInstance().getPlayerDataManager().updateVisibility((Player) e.getEntity(), e.getAction() == EntityPotionEffectEvent.Action.ADDED);
        }
    }
}
