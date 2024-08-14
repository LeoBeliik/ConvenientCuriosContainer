package com.leobeliik.convenientcurioscontainer.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.MODID;

public class Networking {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(PageChange.TYPE, PageChange.CODEC, PageChange::handle);
        registrar.playToServer(SwitchCCC.TYPE, SwitchCCC.CODEC, SwitchCCC::handle);
    }

    public static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.sendToServer(msg);
    }
}
