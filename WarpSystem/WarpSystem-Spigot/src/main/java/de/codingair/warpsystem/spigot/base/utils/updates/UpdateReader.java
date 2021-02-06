package de.codingair.warpsystem.spigot.base.utils.updates;

import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class UpdateReader {
    public static void start() {
        Value<BukkitTask> task = new Value<>(null);
        Runnable runnable = () -> {
            WarpSystem.updateAvailable = WarpSystem.getInstance().getUpdateNotifier().read();

            if (WarpSystem.updateAvailable) {
                WarpSystem.log("-----< WarpSystem >-----");
                WarpSystem.log("New update available [" + WarpSystem.getInstance().getUpdateNotifier().getUpdateInfo() + "].");
                WarpSystem.log("Download it on\n\n" + WarpSystem.getInstance().getUpdateNotifier().getDownload() + "\n");
                WarpSystem.log("------------------------");

                Notifier.notifyPlayers(null);
                task.getValue().cancel();
            }
        };

        task.setValue(Bukkit.getScheduler().runTaskTimerAsynchronously(WarpSystem.getInstance(), runnable, 20L * 60 * 2, 20L * 60 * 60)); //check every hour on GitHub
    }
}
