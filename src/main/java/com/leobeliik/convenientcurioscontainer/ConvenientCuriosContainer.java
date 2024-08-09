package com.leobeliik.convenientcurioscontainer;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import com.leobeliik.convenientcurioscontainer.gui.ConvenientScreen;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import com.leobeliik.convenientcurioscontainer.network.Networking;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.theillusivec4.curios.api.event.SlotModifiersUpdatedEvent;
import java.util.function.Supplier;

@Mod(ConvenientCuriosContainer.MODID)
public class ConvenientCuriosContainer {
    public static final String MODID = "convenientcurioscontainer";

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    static final DeferredItem<Item> CURIOS_CONTAINER_ITEM = ITEMS.register("curios_container", () ->
            new ConvenientItem(new Item.Properties().stacksTo(1)));

    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final Supplier<MenuType<ConvenientMenu>> CURIOS_CONTAINER_MENU = MENUS.register("curios_container", () ->
            new MenuType(ConvenientMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public ConvenientCuriosContainer(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(Networking::registerMessages);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        MENUS.register(modEventBus);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(CURIOS_CONTAINER_MENU.get(), ConvenientScreen::new);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
            event.accept(CURIOS_CONTAINER_ITEM);
    }

    @SubscribeEvent
    public void onCuriosSlotsModified(SlotModifiersUpdatedEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.containerMenu instanceof ConvenientMenu convenientContainer) {
                convenientContainer.ClearSlots();
            }
        }
    }
}
