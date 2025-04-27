package com.leobeliik.convenientcurioscontainer.events;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import com.leobeliik.convenientcurioscontainer.network.Networking;
import com.leobeliik.convenientcurioscontainer.network.SlotChanged;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.SlotModifiersUpdatedEvent;

public class ConvenientCuriosEventHandler {
    @SubscribeEvent
    public void onCuriosSlotsModified(SlotModifiersUpdatedEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.containerMenu instanceof ConvenientMenu convenientContainer) {
                convenientContainer.addSlots();
                if (!player.level().isClientSide())
                    Networking.sendToClient((ServerPlayer) player, new SlotChanged());
            }
        }
    }
}
