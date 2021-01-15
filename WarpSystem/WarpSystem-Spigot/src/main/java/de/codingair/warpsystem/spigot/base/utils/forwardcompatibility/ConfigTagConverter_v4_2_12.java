package de.codingair.warpsystem.spigot.base.utils.forwardcompatibility;

import de.codingair.warpsystem.spigot.base.WarpSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigTagConverter_v4_2_12 {
    public ConfigTagConverter_v4_2_12() {
        try {
            Path path = new File(WarpSystem.getInstance().getDataFolder(), "Config.yml").toPath();
            byte[] input = Files.readAllBytes(path);

            if(input != null) {
                String s = new String(input);
                if(s.contains("\n  BungeeCord:")) {
                    s = s.replace("BungeeCord", "Proxy");
                    Files.write(path, s.getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
