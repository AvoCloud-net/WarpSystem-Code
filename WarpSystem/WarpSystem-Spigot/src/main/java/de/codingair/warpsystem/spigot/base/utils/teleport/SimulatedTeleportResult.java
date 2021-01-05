package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.warpsystem.api.Result;

public class SimulatedTeleportResult {
    private final String error;
    private final Result result;

    public SimulatedTeleportResult(String error, Result result) {
        this.error = error;
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public Result getResult() {
        return result;
    }
}
