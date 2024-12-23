package de.codingair.warpsystem.spigot.base.utils.effects;

import de.codingair.codingapi.particles.Particle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RotatingParticleSpiral extends BukkitRunnable {
    private static final double CHANGE = 0.2;
    private static final double HEIGHT = 2.4;

    private final List<Player> players;
    private final Location loc;
    private double r = 0;
    private double rMult = 1;
    private double y = 0;
    private double spin = 0;

    public RotatingParticleSpiral(Player player, Location loc, boolean forVisiblePlayers) {
        players = new ArrayList<>();
        players.add(player);

        if (forVisiblePlayers && !player.hasPotionEffect(PotionEffectType.INVISIBILITY) && player.getGameMode() != GameMode.SPECTATOR) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!p.equals(player) && p.getWorld().equals(player.getWorld()) && p.canSee(player)) players.add(p);
            });
        }

        this.loc = loc;
    }

    @Override
    public void run() {
        if (r < 0) {
            this.cancel();
            return;
        }

        boolean edge = true;
        for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 4) {
            double x = r * Math.cos(theta + spin);
            double z = r * Math.sin(theta + spin);

            loc.add(x, y, z);
            if (edge) players.forEach(Particle.FIREWORKS_SPARK.getParticlePacket(loc)::send);
            else players.forEach(Particle.SPELL_WITCH.getParticlePacket(loc)::send);
            loc.subtract(x, y, z);

            edge = !edge;
        }

        y += CHANGE;
        if (y <= HEIGHT / 2) {
            r += 1.25 * CHANGE * rMult;
            rMult -= CHANGE;
        } else {
            r -= 1.25 * CHANGE * rMult;
            rMult += CHANGE;
        }

        spin -= 0.2;
    }
}
