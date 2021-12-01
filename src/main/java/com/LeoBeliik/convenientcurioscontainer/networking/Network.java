package com.LeoBeliik.convenientcurioscontainer.networking;

import com.LeoBeliik.convenientcurioscontainer.ConvenientCuriosContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
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

        INSTANCE.messageBuilder(ScrollMessage.class, nextID())
                .encoder(ScrollMessage::encode)
                .decoder(ScrollMessage::decode)
                .consumer(ScrollMessage::handle)
                .add();
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
