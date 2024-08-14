package com.leobeliik.convenientcurioscontainer;

import com.leobeliik.convenientcurioscontainer.network.Networking;
import com.leobeliik.convenientcurioscontainer.network.SwitchCCC;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.CONVENIENT_ITEM;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.MODID;

@Mod(value = MODID, dist = Dist.CLIENT)
public class ConvenientClientReg {
    public static final Lazy<KeyMapping> OPEN_CONVENIENT_SCREEN_KEY = Lazy.of(() -> new KeyMapping(new TranslatableContents("key.open_convenient_screen", null,
            TranslatableContents.NO_ARGS).getKey(), InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.misc"));

    public ConvenientClientReg(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::registerBindings);
        modEventBus.addListener(this::onClientReg);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void onClientReg(FMLClientSetupEvent clientBus) {
        clientBus.enqueueWork(this::registerItemModelProperties);
    }

    private void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_CONVENIENT_SCREEN_KEY.get());
    }

    private void registerItemModelProperties() {
        ItemProperties.register(CONVENIENT_ITEM.get(), ResourceLocation.fromNamespaceAndPath(MODID, "open"), (stack, a, b, c) ->
                stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.DEFAULT).value());
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.Key event) {
        if (OPEN_CONVENIENT_SCREEN_KEY.get().consumeClick()) {
            Networking.sendToServer(new SwitchCCC(true));
        }
    }
}
