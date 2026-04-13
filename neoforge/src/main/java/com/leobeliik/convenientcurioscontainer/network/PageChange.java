package com.leobeliik.convenientcurioscontainer.network;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;

public record PageChange(boolean next) implements CustomPacketPayload {
    static final Type<PageChange> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "page_change"));

    static final StreamCodec<RegistryFriendlyByteBuf, PageChange> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            PageChange::next,
            PageChange::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
