package com.leobeliik.convenientcurioscontainer.compat;

import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import com.leobeliik.convenientcurioscontainer.networking.Network;
import com.leobeliik.convenientcurioscontainer.networking.SlotChanged;
import io.wispforest.accessories.api.events.ContainersChangeCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ConvenientAccessoriesEventHandler {
    private boolean needSlotUpdate = false;

    public ConvenientAccessoriesEventHandler() {
        ContainersChangeCallback.EVENT.register((livingEntity, capability, changedContainers) -> {
            if (livingEntity instanceof Player player && player.containerMenu instanceof ConvenientContainer menu) {
                //need to check if there was a slot update but wait till accessories updates its container first
                if (!changedContainers.isEmpty()) {
                    needSlotUpdate = true;
                }

                if (needSlotUpdate && changedContainers.isEmpty()) {
                    if (!player.level().isClientSide()) {
                        Network.sendToPlayer(new SlotChanged(), (ServerPlayer) player);
                    }
                    menu.addSlots();
                    needSlotUpdate = false;
                }
            }
        });
    }
}
