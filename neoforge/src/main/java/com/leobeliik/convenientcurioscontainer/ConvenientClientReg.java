package com.leobeliik.convenientcurioscontainer;

import com.leobeliik.convenientcurioscontainer.network.SwitchCCC;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.OPEN_CONVENIENT_SCREEN_KEY;

@Mod(value = MODID, dist = Dist.CLIENT)
public class ConvenientClientReg {
    public ConvenientClientReg(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::registerBindings);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_CONVENIENT_SCREEN_KEY);
    }


    @SubscribeEvent
    public void onKeyPressed(InputEvent.Key event) {
        if (OPEN_CONVENIENT_SCREEN_KEY.consumeClick()) {
            ClientPacketDistributor.sendToServer(new SwitchCCC(true));
        }
    }
}
