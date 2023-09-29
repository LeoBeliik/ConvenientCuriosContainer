package com.leobeliik.convenientcurioscontainer.items;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenuProvider;
import com.leobeliik.convenientcurioscontainer.common.ConvenientStackHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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
            ItemStackHandler stacks = new ItemStackHandler(36);
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
        MenuProvider menuProvider = ConvenientMenuProvider.MenuProvider(itemStack);

        if (!level.isClientSide) {
            ServerPlayer sender = (ServerPlayer) player;
            sender.openMenu(menuProvider);
        }

        return InteractionResultHolder.fail(itemStack);
    }

}
