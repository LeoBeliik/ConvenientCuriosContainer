package com.leobeliik.convenientcurioscontainer.networking;

import com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {
    private static SimpleChannel INSTANCE;
    private static int id = 0;

    private static int nextID() {
        return id++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ConvenientCuriosContainer.MODID),
                () -> "1.0",
                s -> true,
                s -> true);

        INSTANCE.messageBuilder(OpenConvenientContainer.class, nextID(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenConvenientContainer::new)
                .encoder(OpenConvenientContainer::toBytes)
                .consumerNetworkThread(OpenConvenientContainer::handle)
                .add();

        INSTANCE.messageBuilder(SlotChanged.class, nextID())
                .encoder(SlotChanged::encode)
                .decoder(SlotChanged::decode)
                .consumerNetworkThread(SlotChanged::handle)
                .add();

        INSTANCE.messageBuilder(PageChange.class, nextID())
                .encoder(PageChange::encode)
                .decoder(PageChange::decode)
                .consumerNetworkThread(PageChange::handle)
                .add();
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
