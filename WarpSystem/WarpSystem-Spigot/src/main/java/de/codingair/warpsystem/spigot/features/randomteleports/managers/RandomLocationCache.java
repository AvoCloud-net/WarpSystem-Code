package de.codingair.warpsystem.spigot.features.randomteleports.managers;

import de.codingair.codingapi.tools.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface RandomLocationCache {

    CompletableFuture<Location> getAsync(Player player, World target);

}
