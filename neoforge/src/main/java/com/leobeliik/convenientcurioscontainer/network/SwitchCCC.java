package com.leobeliik.convenientcurioscontainer.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;

public record SwitchCCC(boolean open) implements CustomPacketPayload {
    static final Type<SwitchCCC> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "open_ccc"));
    static final StreamCodec<RegistryFriendlyByteBuf, SwitchCCC> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SwitchCCC::open,
            SwitchCCC::new
    );

    @SuppressWarnings("NullableProblems")
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
