package com.leobeliik.convenientcurioscontainer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = ConvenientCuriosContainer.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ConvenientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static Set<Item> forbiddenTrinkets;

    private static final ModConfigSpec.ConfigValue<List<? extends String>> FORBIDDEN_TRINKETS = BUILDER
            .translation("config.forbidden_items")
            .comment("Blacklisted Items - add the name of the item to blacklist, modid:item_name format, separated with comma.",
                    "Example: \"curios:amulet\", \"curios:ring\".")
            .defineListAllowEmpty("forbiddenTrinkets", Collections.emptyList(), () -> "", o -> o instanceof String);

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        forbiddenTrinkets = FORBIDDEN_TRINKETS.get().stream()
                .map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName)))
                .collect(Collectors.toSet());
    }

    public static Set<Item> getForbiddenTrinkets() {
        return forbiddenTrinkets;
    }
}
