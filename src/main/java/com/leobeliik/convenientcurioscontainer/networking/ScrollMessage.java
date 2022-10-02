package com.leobeliik.convenientcurioscontainer.networking;

import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

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

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
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
