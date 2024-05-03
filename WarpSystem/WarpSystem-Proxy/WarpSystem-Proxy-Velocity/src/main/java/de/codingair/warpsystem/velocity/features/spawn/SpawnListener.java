package de.codingair.warpsystem.velocity.features.spawn;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import de.codingair.warpsystem.velocity.base.WarpSystem;

public class SpawnListener {

    @Subscribe
    public void onInit(PlayerChooseInitialServerEvent e) {
        if (SpawnManager.getInstance().isSpawnServerProxy()) {
            String spawnServer = SpawnManager.getInstance().getSpawnServerCommand();
            WarpSystem.proxy().getServer(spawnServer).ifPresent(e::setInitialServer);
        }
    }
}
