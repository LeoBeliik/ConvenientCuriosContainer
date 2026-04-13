package com.leobeliik.convenientcurioscontainer;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import com.leobeliik.convenientcurioscontainer.events.ConvenientCuriosEventHandler;
import com.leobeliik.convenientcurioscontainer.gui.ConvenientScreen;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import com.leobeliik.convenientcurioscontainer.network.Networking;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;

@Mod(MODID)
public class ConvenientCuriosContainerNeoForge {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredItem<Item> CONVENIENT_ITEM = ITEMS.register("convenient_container", ConvenientItem::new);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final Supplier<MenuType<ConvenientMenu>> CONVENIENT_MENU = MENUS.register("convenient_menu", () ->
            new MenuType<>((id,inv) -> new ConvenientMenu(id, inv, CONVENIENT_ITEM.get().getDefaultInstance()), FeatureFlags.DEFAULT_FLAGS));
    //public static boolean isAccessoriesLoaded;

    public ConvenientCuriosContainerNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        MENUS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(Networking::registerMessages);
        //isAccessoriesLoaded = ModList.get().isLoaded("accessories");
        /*if (isAccessoriesLoaded) {
            new ConvenientAccessoriesEventHandler();
        } else {
        }*/
        NeoForge.EVENT_BUS.register(new ConvenientCuriosEventHandler());
        modContainer.registerConfig(ModConfig.Type.COMMON, ConvenientConfig.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(CONVENIENT_ITEM);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(CONVENIENT_MENU.get(), ConvenientScreen::new);
    }
}
