package de.codingair.warpsystem.velocity.utils;

import com.velocitypowered.api.scheduler.ScheduledTask;
import de.codingair.warpsystem.proxy.core.utils.ScheduleTask;
import org.jetbrains.annotations.NotNull;

public class VelocityScheduleTask implements ScheduleTask {
    private final ScheduledTask task;

    public VelocityScheduleTask(@NotNull ScheduledTask task) {
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
