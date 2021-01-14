package de.codingair.warpsystem.spigot.versionfactory.featureobjects;

import de.codingair.codingapi.tools.io.utils.DataMask;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;

public class Shortcut extends de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut {
    public Shortcut() {
    }

    public Shortcut(Destination destination, String displayName) {
        super(destination, displayName);
    }

    @Override
    public boolean read(DataMask d) throws Exception {
        boolean success = super.read(d);
        removeAction(Action.COSTS);
        setPermission(null);
        return success;
    }
}
