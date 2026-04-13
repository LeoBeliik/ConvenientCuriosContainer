package com.leobeliik.convenientcurioscontainer.items;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenuProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;

public class ConvenientItem extends Item {

    public static void setModel(ItemStack stack, boolean open) {
        String name = open ? "convenient_container_open" : "convenient_container";
        stack.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MODID, name));
    }

    public ConvenientItem() {
        super(new Item.Properties().stacksTo(1).setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(MODID, "convenient_container"))));
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        //change sprite to open
        setModel(stack, true);
        if (!level.isClientSide()) {
            ServerPlayer sender = (ServerPlayer) player;
            sender.openMenu(ConvenientMenuProvider.MenuProvider(stack));
        }
        return InteractionResult.SUCCESS;
    }
}
