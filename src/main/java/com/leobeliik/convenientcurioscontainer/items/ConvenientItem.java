package com.leobeliik.convenientcurioscontainer.items;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenuProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ConvenientItem extends Item {
    public ConvenientItem(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
        if (!level.isClientSide) {
            ServerPlayer sender = (ServerPlayer) player;
            MenuProvider menuProvider = ConvenientMenuProvider.MenuProvider(stack);
            sender.openMenu(menuProvider);
        }
        return InteractionResultHolder.fail(stack);
    }
}
