package com.leobeliik.convenientcurioscontainer.networking;

import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PageChange {

    private boolean next;

    public PageChange(boolean next) {
        this.next = next;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(next);
    }

    static PageChange decode(FriendlyByteBuf buf) {
        return new PageChange(buf.readBoolean());
    }

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                AbstractContainerMenu menu = player.containerMenu;

                if (menu instanceof ConvenientContainer) {
                    ((ConvenientContainer) menu).ChangePage(next);
                }
            }
        });
        return true;
    }
}
