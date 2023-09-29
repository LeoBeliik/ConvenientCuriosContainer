package com.leobeliik.convenientcurioscontainer.networking;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenuProvider;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

@SuppressWarnings("SameReturnValue")
public class openConvenientContainer {

    private int itemSlot;

    public openConvenientContainer(int itemSlot) {
        this.itemSlot = itemSlot;
    }

    openConvenientContainer(FriendlyByteBuf friendlyByteBuf) {}

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(itemSlot);
    }

    static openConvenientContainer decode(FriendlyByteBuf buf) {
        return new openConvenientContainer(buf.readInt());
    }

    boolean handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                ItemStack mainItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                ItemStack offItem = player.getItemInHand(InteractionHand.OFF_HAND);
                NonNullList<ItemStack> inv = player.getInventory().items;

                if (mainItem.getItem() instanceof ConvenientItem) {
                    player.openMenu(ConvenientMenuProvider.MenuProvider(mainItem));
                } else if (offItem.getItem() instanceof ConvenientItem) {
                    player.openMenu(ConvenientMenuProvider.MenuProvider(offItem));
                } else {
                    inv.stream().filter(itemStack -> itemStack.getItem() instanceof ConvenientItem).findFirst()
                            .ifPresent(itemStack -> player.openMenu(ConvenientMenuProvider.MenuProvider(itemStack)));
                }
            }
        });
        return true;
    }
}
