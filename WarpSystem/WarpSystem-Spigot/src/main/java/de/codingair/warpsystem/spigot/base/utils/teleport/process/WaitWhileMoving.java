package de.codingair.warpsystem.spigot.base.utils.teleport.process;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.api.destinations.utils.Result;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitWhileMoving extends TeleportStage {
    private static final long MAX_WAIT_TIME = 3000;

    protected WaitWhileMoving() {
    }

    public static BukkitRunnable wait(Player player, Callback<Result> callback) {
        long start = System.currentTimeMillis();

        BukkitRunnable r = new BukkitRunnable() {
            int notMoving = 0;
            int shakeTicks = 0;
            boolean shake = false;
            Location location = player.getLocation();

            @Override
            public void run() {
                if (!player.isOnline() || location.getWorld() != player.getWorld()) {
                    this.cancel();
                    callback.accept(Result.CANCELLED);
                    return;
                }

                if (System.currentTimeMillis() - start > MAX_WAIT_TIME) {
                    this.cancel();
                    MessageAPI.stopSendingActionBar(player);
                    callback.accept(Result.CANCELLED);
                    return;
                }

                double diff = Math.abs(location.getX() - player.getLocation().getX()) + Math.abs(location.getZ() - player.getLocation().getZ());
                double diffY = Math.abs(location.getY() - player.getLocation().getY());

                if (diff <= 0.01 && diffY < 0.11) notMoving++;
                else {
                    notMoving = 0;
                    location = player.getLocation();

                    MessageAPI.sendActionBar(player, "§7» " + (shake ? " " : "") + Lang.get("Teleport_Stop_Moving") + (shake ? " " : "") + " §7«");

                    if (shakeTicks == 3) {
                        shakeTicks = 0;
                        shake = !shake;
                    } else shakeTicks++;
                }

                if (notMoving == 2) {
                    MessageAPI.stopSendingActionBar(player);
                    this.cancel();
                    callback.accept(Result.SUCCESS);
                }
            }
        };

        r.runTaskTimer(WarpSystem.getInstance(), 2, 2);
        return r;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {
        if ((options.isCanMove() || options.isSkip() || options.getDelay(player) == 0) && options.getCosts(player) == 0) {
            end();
            return;
        }

        wait(player, new Callback<Result>() {
            @Override
            public void accept(Result result) {
                if (result == Result.SUCCESS) end();
                else cancel(result);
            }
        });
    }
}
