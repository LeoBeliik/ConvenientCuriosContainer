package com.leobeliik.convenientcurioscontainer.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ConvenientMenuProvider {

    @ParametersAreNonnullByDefault
    public static MenuProvider MenuProvider(ItemStack itemStack) {
        return new MenuProvider() {
            @Nonnull
            @Override
            public Component getDisplayName() {
                return itemStack.getHoverName();
            }

            @Nonnull
            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                return new ConvenientContainer(id, inventory, getItemHandler(itemStack));
            }
        };
    }

    private static ItemStackHandler getItemHandler(ItemStack stack) {
        return (ItemStackHandler) stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElse(new ConvenientStackHandler(stack, 36));
    }
}
