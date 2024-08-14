package com.leobeliik.convenientcurioscontainer.network;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.MODID;

public record PageChange(boolean next) implements CustomPacketPayload {
    static final CustomPacketPayload.Type<PageChange> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "page_change"));

    static final StreamCodec<RegistryFriendlyByteBuf, PageChange> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            PageChange::next,
            PageChange::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PageChange create(boolean next) {
        return new PageChange(next);
    }

    void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            AbstractContainerMenu menu = player.containerMenu;

            if (menu instanceof ConvenientMenu) {
                ((ConvenientMenu) menu).ChangePage(next);
            }
        });
    }
}