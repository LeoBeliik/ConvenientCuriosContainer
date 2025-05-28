package com.leobeliik.convenientcurioscontainer.networking;

import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

@SuppressWarnings("SameReturnValue")
public class SlotChanged {

    public SlotChanged() {}

    SlotChanged(FriendlyByteBuf friendlyByteBuf) {}

    void toBytes(FriendlyByteBuf buf) {}

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                AbstractContainerMenu container = player.containerMenu;

                if (container instanceof ConvenientContainer menu) {
                    menu.addSlots();
                }
            }
        });
        return true;
    }
}
