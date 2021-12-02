package com.LeoBeliik.convenientcurioscontainer.items;

import com.LeoBeliik.convenientcurioscontainer.common.ConvenientContainer;
import com.LeoBeliik.convenientcurioscontainer.common.ConvenientStackHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class ConvenientItem extends Item {

    public ConvenientItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        ConvenientStackHandler handler = new ConvenientStackHandler(stack, 36);

        if (nbt != null && nbt.contains("Parent")) {
            CompoundTag itemData = nbt.getCompound("Parent");
            ItemStackHandler stacks = new ItemStackHandler();
            stacks.deserializeNBT(itemData);
            nbt.remove("Parent");
        }
        return handler;
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        MenuProvider ccProvider = new MenuProvider() {
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
        if (!level.isClientSide()) {
            NetworkHooks.openGui((ServerPlayer) player, ccProvider);
        }
        return InteractionResultHolder.success(itemStack);
    }

    private ItemStackHandler getItemHandler(ItemStack stack) {
        return (ItemStackHandler) stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElse(new ConvenientStackHandler(stack, 36));
    }

}
