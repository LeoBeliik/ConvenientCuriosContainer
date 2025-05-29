package com.leobeliik.convenientcurioscontainer.compat;


import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import com.leobeliik.convenientcurioscontainer.networking.Network;
import com.leobeliik.convenientcurioscontainer.networking.SlotChanged;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.SlotModifiersUpdatedEvent;

public class ConvenientCuriosEventHandler {
    @SubscribeEvent
    public void onCuriosSlotsModified(SlotModifiersUpdatedEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.containerMenu instanceof ConvenientContainer convenientContainer) {
                if (!player.level().isClientSide()) {
                    Network.sendToPlayer(new SlotChanged(player.getUUID()), (ServerPlayer) player);
                }
                convenientContainer.addSlots();
            }
        }
    }
}
