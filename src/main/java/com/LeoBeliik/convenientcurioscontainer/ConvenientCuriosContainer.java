package com.LeoBeliik.convenientcurioscontainer;

import com.LeoBeliik.convenientcurioscontainer.common.ConvenientContainer;
import com.LeoBeliik.convenientcurioscontainer.gui.ConvenientScreen;
import com.LeoBeliik.convenientcurioscontainer.items.ConvenientItem;
import com.LeoBeliik.convenientcurioscontainer.networking.Network;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("convenientcurioscontainer")
public class ConvenientCuriosContainer {

    public static final String MODID = "convenientcurioscontainer";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public ConvenientCuriosContainer() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientRegistry);
        Config.init();
        Registry();
    }

    private void clientRegistry(final FMLClientSetupEvent event) {
        MenuScreens.register(CURIOS_CONTAINER_CONTAINER.get(), ConvenientScreen::new);
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
}
