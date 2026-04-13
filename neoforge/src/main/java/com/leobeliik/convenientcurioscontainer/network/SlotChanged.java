package com.leobeliik.convenientcurioscontainer.network;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import javax.annotation.Nonnull;

import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;

public record SlotChanged() implements CustomPacketPayload {
    static final Type<SlotChanged> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "slot_change"));

    static final StreamCodec<RegistryFriendlyByteBuf, SlotChanged> CODEC =
            new StreamCodec<>() {
                @Nonnull
                @Override
                public SlotChanged decode(@Nonnull RegistryFriendlyByteBuf rfbb) {
                    return new SlotChanged();
                }

                @Override
                public void encode(@Nonnull RegistryFriendlyByteBuf rfbb, @Nonnull SlotChanged slotChanged) {}
            };

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
