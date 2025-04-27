package com.leobeliik.convenientcurioscontainer.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class ConvenientMenuProvider {
    public static MenuProvider MenuProvider(ItemStack itemStack) {
        return new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return itemStack.getHoverName();
            }

            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
                return new ConvenientMenu(id, inventory, itemStack);
            }
        };
    }
}
