package com.leobeliik.convenientcurioscontainer.items;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenuProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConvenientItem extends Item {

    public ConvenientItem(Properties properties) {
        super(properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        //change sprite to open
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
        if (!level.isClientSide) {
            ServerPlayer sender = (ServerPlayer) player;
            sender.openMenu(ConvenientMenuProvider.MenuProvider(stack));
        }
        return InteractionResultHolder.fail(stack);
    }
}
