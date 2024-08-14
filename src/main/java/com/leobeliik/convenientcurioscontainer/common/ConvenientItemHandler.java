package com.leobeliik.convenientcurioscontainer.common;

import com.leobeliik.convenientcurioscontainer.ConvenientConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import java.util.List;

class ConvenientItemHandler extends ItemStackHandler {
    private ItemStack cItem;

    ConvenientItemHandler(ItemStack stack) {
        super(54); //size of 54 (same as double chest)
        this.cItem = stack;
        loadItems(cItem.get(DataComponents.CONTAINER));
    }

    private void loadItems(ItemContainerContents component) {
        if (component != null) {
            List<ItemStack> s = component.stream().toList();
            for (int i = 0; i < s.size(); i++) {
                this.setStackInSlot(i, s.get(i));
            }
        }
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return CuriosApi.getCurio(stack).isPresent() && !ConvenientConfig.getForbiddenTrinkets().contains(stack.getItem());
    }

    @Override
    protected void onContentsChanged(int slot) {
        //save content onto item
        cItem.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.stacks));
        super.onContentsChanged(slot);
    }
}
