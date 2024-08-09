package com.leobeliik.convenientcurioscontainer.network;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenuProvider;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.MODID;

public record SwitchCCC(boolean open) implements CustomPacketPayload {
    static final CustomPacketPayload.Type<SwitchCCC> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "open_ccc"));

    static final StreamCodec<RegistryFriendlyByteBuf, SwitchCCC> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SwitchCCC::open,
            SwitchCCC::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static SwitchCCC create(boolean open) {
        return new SwitchCCC(open);
    }

    void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();
            ItemStack offItem = player.getItemInHand(InteractionHand.OFF_HAND);
            player.getInventory().items.stream().filter(itemStack -> itemStack.getItem() instanceof ConvenientItem)
                    .findFirst().ifPresent(itemStack -> interact(player, itemStack));
        });
    }

    private void interact(ServerPlayer player, ItemStack stack) {
        if (open) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
            player.openMenu(ConvenientMenuProvider.MenuProvider(stack));
        } else {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0));
        }
    }
}
