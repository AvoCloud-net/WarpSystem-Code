package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.server.AsyncCatcher;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomLocationCache;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.RandomLocationCalculator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomLocationCacheImpl implements RandomLocationCache {
    private final Map<UUID, ArrayBlockingQueue<RandomLocationCalculator>> randomLocations = new HashMap<>();
    private final LinkedBlockingQueue<UUID> workingQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean working = new AtomicBoolean();
    private boolean restrictionWarningSent = false;

    public RandomLocationCacheImpl(int startDelay, Map<String, Integer> preloadOption) {
        init(startDelay, preloadOption);
    }

    private void init(int startDelay, Map<String, Integer> preloadOption) {
        Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
            for (Map.Entry<String, Integer> e : preloadOption.entrySet()) {
                World w = Bukkit.getWorld(e.getKey());
                if (w == null) {
                    WarpSystem.getInstance().getLogger().warning("Could not find world '" + e.getKey() + "'. RTP preloading will not be available for this world.");
                    continue;
                }

                randomLocations.put(w.getUID(), new ArrayBlockingQueue<>(e.getValue()));
            }

            List<UUID> queue = new ArrayList<>();
            for (Map.Entry<UUID, ArrayBlockingQueue<RandomLocationCalculator>> e : randomLocations.entrySet()) {
                World w = Bukkit.getWorld(e.getKey());
                Objects.requireNonNull(w);

                ArrayBlockingQueue<RandomLocationCalculator> q = e.getValue();
                for (int i = 0; i < q.remainingCapacity(); i++) {
                    queue.add(w.getUID());
                }
            }

            Collections.shuffle(queue);  // shuffle to preload worlds equally
            workingQueue.addAll(queue);

            WarpSystem.log("Random TP preloading is active now.");

            checkWork();
        }, startDelay * 20L);
    }

    private void checkWork() {
        if (working.get() || workingQueue.isEmpty()) return;
        World w = Bukkit.getWorld(workingQueue.remove());

        if (w == null) {
            checkWork();
            return;
        }

        preload(w, 0);
    }

    private void preload(World w, int tries) {
        if (tries >= 5) {
            if (!restrictionWarningSent) {
                WarpSystem.getInstance().getLogger().warning("Cannot find a valid random location after 5 tries. Please lower your RTP restrictions.");
                restrictionWarningSent = true;
            }

            workingQueue.add(w.getUID());
            checkWork();
            return;
        }

        working.set(true);
        RandomLocationCalculator c = RandomTeleportManager.getInstance().newCalculator(null, w, new Callback<RandomLocationCalculator>() {
            @Override
            public void accept(RandomLocationCalculator c) {
                if (!WarpSystem.getInstance().isEnabled()) return;
                AsyncCatcher.runSync(WarpSystem.getInstance(), () -> {
                    if (c.getResult() != null) {
                        storeLocation(w, c);
                        working.set(false);
                        checkWork();
                    } else preload(w, tries + 1);
                });
            }
        });

        if (!WarpSystem.getInstance().isEnabled()) return;
        Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), c);
    }

    private void storeLocation(World w, RandomLocationCalculator c) {
        randomLocations.get(w.getUID()).add(c);
    }

    private RandomLocationCalculator poll(World target) {
        Queue<RandomLocationCalculator> q = randomLocations.get(target.getUID());
        if (q == null || q.isEmpty()) return null;

        RandomLocationCalculator c = q.remove();
        workingQueue.add(target.getUID());
        checkWork();
        return c;
    }

    public CompletableFuture<Location> getAsync(Player player, World target) {
        RandomLocationCalculator c = poll(target);
        if (c == null) return CompletableFuture.completedFuture(null);

        Location l = Location.getByLocation(c.getResult());

        c.applyPlayer(player);

        CompletableFuture<Location> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(),
                () -> c.isProtectedAsync(Location.getByLocation(l)).thenAccept(
                        isProtected -> AsyncCatcher.runSync(WarpSystem.getInstance(), () -> {
                                    if (isProtected) future.complete(null);
                                    else future.complete(l);
                                }
                        )
                )
        );
        return future;
    }

}
