package de.codingair.warpsystem.spigot.features.signs.utils;

import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.versionfactory.VKey;
import de.codingair.warpsystem.spigot.versionfactory.specified.FactoryBuilder;

public class WarpSignFactory extends FactoryBuilder<WarpSign> {
    private static final WarpSignFactory F = new WarpSignFactory();

    private WarpSignFactory() {
        super(VKey.WarpSign);
    }

    public static WarpSign build() {
        return F.newInstance();
    }

    public static WarpSign build(Location location, Destination destination) {
        return F.newInstance(location, destination);
    }
}
