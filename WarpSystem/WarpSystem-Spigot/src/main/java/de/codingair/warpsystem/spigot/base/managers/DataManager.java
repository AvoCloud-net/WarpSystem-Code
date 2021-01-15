package de.codingair.warpsystem.spigot.base.managers;

import de.codingair.warpsystem.base.utils.Manager;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;

import java.util.Collection;
import java.util.HashMap;

public class DataManager {
    private final HashMap<FeatureType, Manager> managers = new HashMap<>();

    public DataManager() {
        for (FeatureType.Priority value : FeatureType.Priority.values()) {
            if (value == FeatureType.Priority.DISABLED) continue;

            for (FeatureType ft : FeatureType.values(value)) {
                try {
                    this.managers.put(ft, ft.createInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void preLoad() {
        for (Manager manager : this.managers.values()) {
            manager.preLoad();
        }
    }

    public void removeDisabled() {
        for (FeatureType.Priority value : FeatureType.Priority.values()) {
            if (value == FeatureType.Priority.DISABLED) continue;

            for (FeatureType ft : FeatureType.values(value)) {
                if (!ft.isActive()) {
                    Manager m = null;

                    for (Manager manager : this.managers.values()) {
                        if (manager.getClass().equals(ft.getManagerClass())) {
                            m = manager;
                            break;
                        }
                    }

                    if (m != null) this.managers.remove(ft);
                }
            }
        }
    }

    public boolean load() {
        boolean success = true;
        for (Manager manager : this.managers.values()) {
            if (!manager.load(false)) success = false;
        }

        WarpSystem.getInstance().getFileManager().getFile("Config").saveConfig();

        return success;
    }

    public void save(boolean saver) {
        for (Manager manager : this.managers.values()) {
            manager.save(saver);
        }
    }

    public <T extends Manager> T getManager(FeatureType type) {
        return (T) this.managers.get(type);
    }

    public Collection<Manager> getManagers() {
        return managers.values();
    }
}
