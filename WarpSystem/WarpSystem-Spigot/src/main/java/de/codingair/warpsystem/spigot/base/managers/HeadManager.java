package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.warpsystem.spigot.api.players.Head;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class HeadManager {
    private static final int MAX_COUNTER = 20;
    private ConfigFile file = null;
    private int queueCounter = 0;
    private BukkitRunnable savingQueue = null;

    public void onEnable() {
        if (this.file == null) this.file = WarpSystem.getInstance().getFileManager().loadFile("PlayerSkins", "Memory/");
    }

    public Head getHead(UUID uuid) {
        return new Head(getSkinId(uuid));
    }

    public String getSkinId(UUID uuid) {
        if (uuid == null) return null;
        return this.file.getConfig().getString(uuid.toString());
    }

    /*
        Called in UUIDListener after getting an unique Id.
     */
    public boolean update(Player player, UUID uuid) {
        if (uuid == null || !player.isOnline()) return false;

        Head head = new Head(player);

        String id = this.file.getConfig().getString(uuid.toString());
        if (!Objects.equals(head.getId(), id)) {
            this.file.getConfig().set(uuid.toString(), head.getId());
            queueFileSave();
            return true;
        }

        return false;
    }

    private void queueFileSave() {
        if (savingQueue != null) {
            savingQueue.cancel();
            queueCounter++;
        }

        savingQueue = new BukkitRunnable() {
            @Override
            public void run() {
                file.saveConfig();
                savingQueue = null;
            }
        };

        if (queueCounter >= MAX_COUNTER) {
            queueCounter = 0;
            savingQueue.runTaskAsynchronously(WarpSystem.getInstance());
        } else savingQueue.runTaskLaterAsynchronously(WarpSystem.getInstance(), 100L);
    }
}
