package com.LeoBeliik.convenientcurioscontainer;

import com.LeoBeliik.convenientcurioscontainer.gui.CuriosContainerContainer;
import com.LeoBeliik.convenientcurioscontainer.gui.CuriosContainerScreen;
import com.LeoBeliik.convenientcurioscontainer.items.CuriosContainerItem;
import com.LeoBeliik.convenientcurioscontainer.networking.Network;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("convenientcurioscontainer")
public class ConvenientCuriosContainer {

    public static final String MODID = "convenientcurioscontainer";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public ConvenientCuriosContainer() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientRegistry);
        Registry();
    }

    private void clientRegistry(final FMLClientSetupEvent event) {
        ScreenManager.register(CURIOS_CONTAINER_CONTAINER.get(), CuriosContainerScreen::new);
    }

    private void Registry() {
        Network.registerMessages();
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Item> CURIOS_CONTAINER_ITEM = ITEMS.register("curios_container", () ->
            new CuriosContainerItem(new Item.Properties().tab(ItemGroup.TAB_FOOD).stacksTo(1)));

    public static final RegistryObject<ContainerType<CuriosContainerContainer>> CURIOS_CONTAINER_CONTAINER = CONTAINERS.register(
            "curios_container", () -> IForgeContainerType.create(CuriosContainerContainer::new));
}
