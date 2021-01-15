package de.codingair.warpsystem.spigot.transfer.handlers;

import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.Proxy;
import de.codingair.warpsystem.base.transfer.packets.proxy.UpdateGlobalWarpPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdateGlobalWarpPacketHandler implements PacketHandler<UpdateGlobalWarpPacket> {
    @Override
    public void process(@NotNull UpdateGlobalWarpPacket packet, @NotNull Proxy proxy, @Nullable Object connection, @NotNull Direction direction) {
        switch (packet.getAction()) {
            case ADD:
                ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().put(packet.getName(), packet.getServer());
                break;

            case UPDATE_POSITION:
                ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().replace(packet.getName(), packet.getServer());
                break;

            case DELETE:
                ((GlobalWarpManager) WarpSystem.getInstance().getDataManager().getManager(FeatureType.GLOBAL_WARPS)).getGlobalWarps().remove(packet.getName());
                for (Icon warpIcon : IconManager.getInstance().getIcons()) {
                    if (warpIcon.getAction(Action.WARP) != null) {
                        if (warpIcon.getAction(WarpAction.class).getValue().getType() == DestinationType.GlobalWarp &&
                                warpIcon.getAction(WarpAction.class).getValue().getId().equalsIgnoreCase(packet.getName()))
                            warpIcon.getAction(WarpAction.class).setValue(null);
                    }
                }
                break;
        }
    }
}
