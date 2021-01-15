package de.codingair.warpsystem.spigot.features.portals.utils;

import de.codingair.warpsystem.spigot.versionfactory.VKey;
import de.codingair.warpsystem.spigot.versionfactory.specified.FactoryBuilder;

public class PortalFactory extends FactoryBuilder<Portal> {
    private static final PortalFactory F = new PortalFactory();

    private PortalFactory() {
        super(VKey.Portal);
    }

    public static Portal build() {
        return F.newInstance();
    }

    public static Portal build(String displayName) {
        return F.newInstance(displayName);
    }
}
