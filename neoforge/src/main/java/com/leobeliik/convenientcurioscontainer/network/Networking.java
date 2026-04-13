package com.leobeliik.convenientcurioscontainer.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {

    @SubscribeEvent
    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0");

        registrar.playToServer(SwitchCCC.TYPE, SwitchCCC.CODEC, NetworkCCCHandler.getInstance()::handleSwitch);
        registrar.playToServer(PageChange.TYPE, PageChange.CODEC, NetworkCCCHandler.getInstance()::handlePageChange);
        registrar.playToClient(SlotChanged.TYPE, SlotChanged.CODEC, NetworkCCCHandler.getInstance()::handleSlotChanged);
    }
}

