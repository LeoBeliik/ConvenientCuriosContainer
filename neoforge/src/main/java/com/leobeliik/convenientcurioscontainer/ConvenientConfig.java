package com.leobeliik.convenientcurioscontainer;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.*;
import java.util.stream.Collectors;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;

@EventBusSubscriber(modid = MODID)
public class ConvenientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static Set<Optional<Holder.Reference<Item>>> forbiddenTrinkets;
    private static Set<Optional<Holder.Reference<Item>>> allowedTrinkets;
    private static boolean enableDarkMode;

    private static final ModConfigSpec.ConfigValue<List<? extends String>> FORBIDDEN_TRINKETS = BUILDER
            .translation("config.forbidden_items")
            .comment("Blacklisted Items - add the name of the item to blacklist, modid:item_name format, separated with comma.",
                    "Example: \"curios:amulet\", \"curios:ring\".")
            .defineListAllowEmpty("forbiddenTrinkets", Collections.emptyList(), () -> "", o -> o instanceof String);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> ALLOWED_TRINKETS = BUILDER
            .translation("config.allowed_items")
            .comment("Whitelisted Items - add the name of the item to whitelist, modid:item_name format, separated with comma.",
                    "Example: \"curios:amulet\", \"curios:ring\".")
            .defineListAllowEmpty("allowedTrinkets", Collections.emptyList(), () -> "", o -> o instanceof String);

    private static ModConfigSpec.BooleanValue DARK_MODE = BUILDER
            .translation("config.dark_mode")
            .comment("Set to true to enable dark mode in the mod's GUI screen")
            .define("enableDarkMode", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        forbiddenTrinkets = FORBIDDEN_TRINKETS.get().stream()
                .map(itemName -> BuiltInRegistries.ITEM.get(Identifier.parse(itemName)))
                .collect(Collectors.toSet());

        allowedTrinkets = ALLOWED_TRINKETS.get().stream()
                .map(itemName -> BuiltInRegistries.ITEM.get(Identifier.parse(itemName)))
                .collect(Collectors.toSet());

        enableDarkMode = DARK_MODE.getAsBoolean();
    }

    public static Set<Item> getForbiddenTrinkets() {
        Set<Item> s = new HashSet<>();
        forbiddenTrinkets.forEach(f -> f.ifPresent(itemReference -> s.add(itemReference.value())));
        return s;
    }

    public static Set<Item> getAllowedTrinkets() {
        Set<Item> s = new HashSet<>();
        allowedTrinkets.forEach(f -> f.ifPresent(itemReference -> s.add(itemReference.value())));
        return s;
    }

    public static boolean getEnableDarkMode() {
        return enableDarkMode;
    }
}
