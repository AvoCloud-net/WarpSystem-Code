package de.codingair.warpsystem.bungee.utils;

import de.codingair.warpsystem.proxy.core.utils.ScheduleTask;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

public class BungeeScheduleTask implements ScheduleTask {
    private final ScheduledTask task;

    public BungeeScheduleTask(@NotNull ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }

    public ScheduledTask getTask() {
        return task;
    }
}
