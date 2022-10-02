package com.leobeliik.convenientcurioscontainer;

import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import com.leobeliik.convenientcurioscontainer.gui.ConvenientScreen;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import com.leobeliik.convenientcurioscontainer.networking.Network;
import com.leobeliik.convenientcurioscontainer.networking.openConvenientContainer;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.client.ClientRegistry;

@Mod("convenientcurioscontainer")
public class ConvenientCuriosContainer {
//TODO re write this mod for 1.19
    public static final String MODID = "convenientcurioscontainer";
    public static KeyMapping openConvenientKey;
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public ConvenientCuriosContainer() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::keyRegistry));
        bus.addListener(this::clientRegistry);
        Config.init();
        Registry();
    }

    private void clientRegistry(final FMLClientSetupEvent event) {
        MenuScreens.register(CURIOS_CONTAINER_CONTAINER.get(), ConvenientScreen::new);
        keyRegistry();
    }

    private void keyRegistry() {
        openConvenientKey = new KeyMapping(new TranslatableComponent("key.open_muffler_gui").getKey(),
                InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.misc");
        ClientRegistry.registerKeyBinding(openConvenientKey);
    }

    private void Registry() {
        Network.registerMessages();
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Item> CURIOS_CONTAINER_ITEM = ITEMS.register("curios_container", () ->
            new ConvenientItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));

    public static final RegistryObject<MenuType<ConvenientContainer>> CURIOS_CONTAINER_CONTAINER = CONTAINERS.register(
            "curios_container", () -> IForgeMenuType.create(ConvenientContainer::new));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent //on mod keybind press
    public void onKeyInput(InputEvent event) {
        if (openConvenientKey.consumeClick()) {
            Network.sendToServer(new openConvenientContainer());
        }
    }
}
