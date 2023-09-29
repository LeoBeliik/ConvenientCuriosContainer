package com.leobeliik.convenientcurioscontainer.networking;

import com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

public class Network {
    public static SimpleChannel INSTANCE;
    private static final int id = 1;

    public static void registerMessages() {
        INSTANCE = ChannelBuilder.named(new ResourceLocation(ConvenientCuriosContainer.MODID))
                .networkProtocolVersion(id)
                .clientAcceptedVersions(Channel.VersionTest.exact(id))
                .serverAcceptedVersions(Channel.VersionTest.exact(id)).simpleChannel();

        register(ScrollMessage.class, ScrollMessage::encode, ScrollMessage::decode, ScrollMessage::handle);
        register(openConvenientContainer.class, openConvenientContainer::encode, openConvenientContainer::decode, openConvenientContainer::handle);
    }

    private static <M> void register(Class<M> messageType, BiConsumer<M, FriendlyByteBuf> encoder,
                                     Function<FriendlyByteBuf, M> decoder,
                                     BiConsumer<M, CustomPayloadEvent.Context> messageConsumer) {
        INSTANCE.messageBuilder(messageType)
                .decoder(decoder)
                .encoder(encoder)
                .consumerNetworkThread(messageConsumer)
                .add();
    }
}
