package com.leobeliik.convenientcurioscontainer;

import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;

public class ConvenientCuriosContainerFabric implements ModInitializer {

    public static final Item CONVENIENT_ITEM = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, "convenient_container"),
            new ConvenientItem(new Item.Properties().stacksTo(1)));
    //public static MenuType<ConvenientMenu> CONVENIENT_MENU = new MenuType<>(ConvenientMenu::new, FeatureFlags.DEFAULT_FLAGS);

    @Override
    public void onInitialize() {

    }

}
