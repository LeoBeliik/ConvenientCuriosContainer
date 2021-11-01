package com.LeoBeliik.convenientcurioscontainer.items;

import com.LeoBeliik.convenientcurioscontainer.capabilities.CuriosContainerCapabilityProvider;
import com.LeoBeliik.convenientcurioscontainer.gui.CuriosContainerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class CuriosContainerItem extends Item {

    public CuriosContainerItem(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CuriosContainerCapabilityProvider();
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!(itemStack.getItem() instanceof CuriosContainerItem)) {
            return ActionResult.pass(itemStack);
        }
        CuriosContainerItem item = (CuriosContainerItem) itemStack.getItem();
        INamedContainerProvider ccProvider = new INamedContainerProvider() {
            @Nonnull
            @Override
            public ITextComponent getDisplayName() {
                return itemStack.getHoverName();
            }

            @Nonnull
            @Override
            public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                return new CuriosContainerContainer(id, inventory, item.getItemHandler(itemStack));
            }
        };
        if (!level.isClientSide()) {
            NetworkHooks.openGui((ServerPlayerEntity) player, ccProvider);
        }
        return ActionResult.success(itemStack);
    }

    private ItemStackHandler getItemHandler(ItemStack stack) {
        return (ItemStackHandler) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(new ItemStackHandler(36));
    }

}
