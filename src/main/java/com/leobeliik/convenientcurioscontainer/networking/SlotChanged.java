package com.leobeliik.convenientcurioscontainer.networking;

import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("SameReturnValue")
public class SlotChanged {

    private UUID playerUUID;

    public SlotChanged(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
    }

    static SlotChanged decode(FriendlyByteBuf buf) {
        return new SlotChanged(buf.readUUID());
    }

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                //we need to find the player because Accessories event for some reason doens't work with ctx.get().getSender()
                Player player = level.getPlayerByUUID(playerUUID);
                if (player != null) {
                    AbstractContainerMenu container = player.containerMenu;

                    if (container instanceof ConvenientContainer menu) {
                        menu.addSlots();
                    }
                }
            }
        });
        return true;
    }
}
