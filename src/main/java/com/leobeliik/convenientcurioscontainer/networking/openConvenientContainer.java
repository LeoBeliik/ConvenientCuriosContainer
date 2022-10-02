package com.leobeliik.convenientcurioscontainer.networking;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenuProvider;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import java.util.function.Supplier;

public class openConvenientContainer {

    private int itemSlot;

    public openConvenientContainer() {}

    openConvenientContainer(FriendlyByteBuf friendlyByteBuf) {}

    void toBytes(FriendlyByteBuf buf) {}

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack mainItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                ItemStack offItem = player.getItemInHand(InteractionHand.OFF_HAND);
                NonNullList<ItemStack> inv = player.getInventory().items;

                if (mainItem.getItem() instanceof ConvenientItem) {
                    NetworkHooks.openGui(player, ConvenientMenuProvider.MenuProvider(mainItem));
                } else if (offItem.getItem() instanceof ConvenientItem) {
                    NetworkHooks.openGui(player, ConvenientMenuProvider.MenuProvider(offItem));
                } else {
                    inv.stream().filter(itemStack -> itemStack.getItem() instanceof ConvenientItem).findFirst()
                            .ifPresent(itemStack -> NetworkHooks.openGui(player, ConvenientMenuProvider.MenuProvider(itemStack)));
                }
            }
        });
        return true;
    }
}
