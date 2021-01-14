package de.codingair.warpsystem.proxy.core.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.warpsystem.proxy.core.Core;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Players {
    private static final Cache<String, Player> CACHE = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build();

    public static Player getPlayer(String name) {
        Player found = Core.getPlugin().getPlayer(name);
        if (found != null) return found;
        String lowerName = name.toLowerCase(Locale.ENGLISH);

        found = CACHE.getIfPresent(lowerName);
        if (found != null) return found;

        int delta = 2147483647;
        for (Player player : Core.getPlugin().getOnlinePlayers().collect(Collectors.toSet())) {
            if (player.getName().toLowerCase(Locale.ENGLISH).startsWith(lowerName)) {
                int curDelta = Math.abs(player.getName().length() - lowerName.length());
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }

                if (curDelta == 0) {
                    break;
                }
            }
        }


        if (found != null) CACHE.put(lowerName, found);
        return found;
    }
}
