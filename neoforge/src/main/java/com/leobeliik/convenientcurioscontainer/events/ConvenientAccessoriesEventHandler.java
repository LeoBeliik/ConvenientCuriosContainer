/*
package com.leobeliik.convenientcurioscontainer.events;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import com.leobeliik.convenientcurioscontainer.network.SlotChanged;
import com.leobeliik.convenientcurioscontainer.network.Networking;
import io.wispforest.accessories.api.events.ContainersChangeCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ConvenientAccessoriesEventHandler {
    private boolean needSlotUpdate = false;

    public ConvenientAccessoriesEventHandler() {
        ContainersChangeCallback.EVENT.register((livingEntity, capability, changedContainers) -> {
            if (livingEntity instanceof Player player && player.containerMenu instanceof ConvenientMenu menu) {
                //need to check if there was a slot update but wait till accessories updates its container first
                if (!changedContainers.isEmpty()) {
                    needSlotUpdate = true;
                }

                if (needSlotUpdate && changedContainers.isEmpty()) {
                    if (!player.level().isClientSide())
                        Networking.sendToClient((ServerPlayer) player, new SlotChanged());
                    menu.addSlots();
                    needSlotUpdate = false;
                }
            }
        });
    }
}
*/
