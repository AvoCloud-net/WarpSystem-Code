package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TeleportUtils {
    private TeleportUtils() {
    }

    private static double getHeight(HumanEntity e) {
        if (Version.get().isBiggerThan(11)) return e.getHeight();
        else return 2D;
    }

    public static <A extends Location> CompletableFuture<A> prepareLocation(@Nullable A l, Player player) {
        return prepareLocation(l, player, false);
    }

    public static <A extends Location> CompletableFuture<A> prepareLocation(@Nullable A l, Player player, boolean sync) {
        if (l == null || player.isFlying()) return CompletableFuture.completedFuture(l);

        Location clone = l.clone();

        Material m = clone.getBlock().getType();
        while (!m.isSolid() && !Environment.isWaterFluid(clone.getBlock()) && clone.getY() >= 0) {
            clone.setY(clone.getY() - 1);
            m = clone.getBlock().getType();
        }

        if (clone.getY() < 0) {
            enableFlightIfPossible(player, 2); //> 1 -> trigger
            return CompletableFuture.completedFuture(l);
        }

        do {
            clone.setY(clone.getY() + 1);
            m = clone.getBlock().getType();
        } while ((m.isSolid() || Environment.isWaterFluid(clone.getBlock())) && clone.getY() < 200);

        if (enableFlightIfPossible(player, l.getY() - clone.getY())) return CompletableFuture.completedFuture(l);

        l.setY(clone.getY());

        CompletableFuture<A> future = new CompletableFuture<>();
        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            searchSafe(l, player, sync).whenComplete((safe, t) -> {
                if (t != null) t.printStackTrace();
                if (safe != null) {
                    l.setX(safe.getX());
                    l.setY(safe.getY());
                    l.setZ(safe.getZ());
                }

                future.complete(l);
            });
        } else future.complete(l);

        return future;
    }

    private static boolean enableFlightIfPossible(Player player, double diff) {
        if (player.getAllowFlight()) {
            if (diff > 1D) {
                player.setFlying(true);
                return true;
            }
        }

        return false;
    }

    public static CompletableFuture<Location> searchSafe(@NotNull Location location, @Nullable HumanEntity e, boolean sync) {
        CompletableFuture<Location> future = new CompletableFuture<>();

        if (isSafe(location, e)) {
            future.complete(location);
            return future;
        }

        Runnable runnable = () -> {
            Location copy = location.clone();

            for (int y = 0; y < 5; y++) {
                for (int vY = -1; vY < 2; vY += 2) {
                    copy.setY(location.getY() + y * vY);

                    for (int x = 0; x < 5; x++) {
                        for (int vX = -1; vX < 2; vX += 2) {
                            copy.setX(location.getX() + x * vX);

                            for (int z = 0; z < 5; z++) {
                                for (int vZ = -1; vZ < 2; vZ += 2) {
                                    copy.setZ(location.getZ() + z * vZ);

                                    if (isSafe(copy, e)) {
                                        if (sync) future.complete(copy);
                                        else Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> future.complete(copy));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (sync) future.complete(null);
            else Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> future.complete(null));
        };

        if (sync) runnable.run();
        else Bukkit.getScheduler().runTaskAsynchronously(WarpSystem.getInstance(), runnable);

        return future;
    }

    public static boolean isSafe(@NotNull Location location, @Nullable HumanEntity e) {
        int size = e == null ? 2 : (int) Math.ceil(getHeight(e));

        Location copy = location.clone();

        copy.subtract(0, 1, 0);
        Block b = copy.getBlock();
        if (!b.getType().isSolid() && !Environment.isWaterFluid(b) || !isSafe(b)) return false;
        copy.add(0, 1, 0);

        for (int i = 0; i < size; i++) {
            copy.add(0, i, 0);
            b = copy.getBlock();

            if (!isSafe(b) || !Environment.canBeEntered(b.getType())) return false;
        }

        return true;
    }

    public static boolean isSafe(@NotNull Block b) {
        List<String> unsafe = new ArrayList<>();

        unsafe.add("VOID");
        unsafe.add("LAVA");
        unsafe.add("FIRE");
        unsafe.add("MAGMA");

        for (String s : unsafe) {
            if (b.getType().name().toUpperCase().contains(s)) return false;
        }

        return true;
    }
}
