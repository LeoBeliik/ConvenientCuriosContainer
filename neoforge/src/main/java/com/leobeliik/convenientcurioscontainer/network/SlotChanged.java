package com.leobeliik.convenientcurioscontainer.network;


import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;

public record SlotChanged() implements CustomPacketPayload {
    static final Type<SlotChanged> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "slot_change"));

    static final StreamCodec<RegistryFriendlyByteBuf, SlotChanged> CODEC = CustomPacketPayload.codec(
            SlotChanged::write,
            SlotChanged::decode
    );

    private void write(RegistryFriendlyByteBuf buf) { }

    private static SlotChanged decode(RegistryFriendlyByteBuf buf) {
        return new SlotChanged();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            AbstractContainerMenu container = player.containerMenu;

            if (container instanceof ConvenientMenu menu) {
                menu.addSlots();
            }
        });
    }
}
