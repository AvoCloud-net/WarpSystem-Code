package de.codingair.warpsystem.spigot.versionfactory.featureobjects;

import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;

public class WarpSign extends de.codingair.warpsystem.spigot.features.signs.utils.WarpSign {
    public WarpSign() {
        super();
    }

    public WarpSign(Location location, Destination destination) {
        super(location, destination);
    }
}
