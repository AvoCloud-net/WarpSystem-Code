package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.warpsystem.spigot.base.utils.teleport.process.TeleportStage;

public class TeleportPostProcessing extends TeleportStage {
    @Override
    protected void start() {
        if (options.withPostInvulnerability() && options.getOriginalDestination().usesBukkitTeleportation()) {
            player.setNoDamageTicks((int) (options.postInvulnerabilityDuration() * 20));
        }

        end();
    }

    @Override
    protected void destroy() {
    }
}
