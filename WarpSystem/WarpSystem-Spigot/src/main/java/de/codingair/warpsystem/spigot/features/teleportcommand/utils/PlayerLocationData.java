package de.codingair.warpsystem.spigot.features.teleportcommand.utils;

import org.bukkit.Location;

public class PlayerLocationData {
    private final Location back;
    private final Location quit;

    public PlayerLocationData(Location back, Location quit) {
        this.back = back;
        this.quit = quit;
    }

    public Location getBack() {
        return back;
    }

    public Location getQuit() {
        return quit;
    }

    public boolean valid() {
        return back != null || quit != null;
    }

    @Override
    public String toString() {
        return "PlayerLocationData{" +
                "back=" + back +
                ", quit=" + quit +
                '}';
    }
}
