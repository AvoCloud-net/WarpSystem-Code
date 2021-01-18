package de.codingair.warpsystem.core.proxy.base;

import de.codingair.warpsystem.core.proxy.utils.ProxyPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LangHandler {
    public static void initPreDefinedLanguages(ProxyPlugin plugin) throws IOException {
        List<String> languages = new ArrayList<>();
        languages.add("ENG.yml");
        languages.add("GER.yml");
        languages.add("ES.yml");
        languages.add("FRA.yml");

        File folder = new File(plugin.getDataFolder(), "/Languages/");
        if (!folder.exists()) mkDir(folder);

        for (String language : languages) {
            InputStream is = plugin.getResourceAsStream("languages/" + language);

            File file = new File(plugin.getDataFolder() + "/Languages/", language);
            if (!file.exists()) {
                file.createNewFile();
                copy(is, new FileOutputStream(file));
            }
        }
    }

    private static void mkDir(File file) {
        if (!file.getParentFile().exists()) mkDir(file.getParentFile());
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (SecurityException ex) {
                throw new IllegalArgumentException("Plugin is not permitted to create a folder!");
            }
        }
    }

    private static long copy(InputStream from, OutputStream to) throws IOException {
        if (from == null) return -1;
        if (to == null) throw new NullPointerException();

        byte[] buf = new byte[4096];
        long total = 0L;

        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                return total;
            }

            to.write(buf, 0, r);
            total += (long) r;
        }
    }
}
