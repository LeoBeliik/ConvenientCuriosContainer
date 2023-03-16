package com.leobeliik.convenientcurioscontainer;

import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import com.leobeliik.convenientcurioscontainer.gui.ConvenientScreen;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import com.leobeliik.convenientcurioscontainer.networking.Network;
import com.leobeliik.convenientcurioscontainer.networking.openConvenientContainer;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.event.SlotModifiersUpdatedEvent;

@Mod("convenientcurioscontainer")
public class ConvenientCuriosContainer {
    public static final String MODID = "convenientcurioscontainer";
    public static KeyMapping openConvenientKey;
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);


    public ConvenientCuriosContainer() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::keyRegistry));
        bus.addListener(this::clientRegistry);
        bus.addListener(this::onCreativeModeTabBuildContents);
        Config.init();
        Registry();
    }

    private void keyRegistry(RegisterKeyMappingsEvent event) {
        openConvenientKey = new KeyMapping(new TranslatableContents("key.open_convenient_gui", null, TranslatableContents.NO_ARGS).getKey(),
                InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.misc");
        event.register(openConvenientKey);
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
            new ConvenientItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<MenuType<ConvenientContainer>> CURIOS_CONTAINER_CONTAINER = CONTAINERS.register(
            "curios_container", () -> IForgeMenuType.create(ConvenientContainer::new));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent //on mod keybind press
    public void onKeyInput(InputEvent event) {
        if (openConvenientKey.consumeClick()) {
            Network.sendToServer(new openConvenientContainer());
        }
    }

    @SubscribeEvent
    public void onCreativeModeTabBuildContents(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(new ItemStack(CURIOS_CONTAINER_ITEM.get()));
        }
    }

    @SubscribeEvent
    public void onCuriosSlotsModified(SlotModifiersUpdatedEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.containerMenu instanceof ConvenientContainer convenientContainer) {
                convenientContainer.clearSlots();
            }
        }
    }
}
