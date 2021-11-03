package com.LeoBeliik.convenientcurioscontainer.networking;

import com.LeoBeliik.convenientcurioscontainer.common.ConvenientContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ScrollMessage {
    private int direction;

    public ScrollMessage(int direction) {
        this.direction = direction;
    }

    void encode(PacketBuffer buf) {
        buf.writeInt(direction);
    }

    static ScrollMessage decode(PacketBuffer buf) {
        return new ScrollMessage(buf.readInt());
    }

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                Container container = player.containerMenu;
                if (container instanceof ConvenientContainer) {
                    ((ConvenientContainer) container).scroll(direction);
                }
            }
        });
        return true;
    }
}
