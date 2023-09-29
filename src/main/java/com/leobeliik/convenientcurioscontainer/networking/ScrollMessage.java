package com.leobeliik.convenientcurioscontainer.networking;

import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.network.CustomPayloadEvent;

@SuppressWarnings("SameReturnValue")
public class ScrollMessage {
    private int direction;

    public ScrollMessage(int direction) {
        this.direction = direction;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(direction);
    }

    static ScrollMessage decode(FriendlyByteBuf buf) {
        return new ScrollMessage(buf.readInt());
    }

    boolean handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                AbstractContainerMenu container = player.containerMenu;
                if (container instanceof ConvenientContainer) {
                    ((ConvenientContainer) container).scroll(direction);
                }
            }
        });
        return true;
    }
}
