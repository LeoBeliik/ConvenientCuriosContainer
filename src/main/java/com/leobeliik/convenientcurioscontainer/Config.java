package com.leobeliik.convenientcurioscontainer;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import java.util.Collections;
import java.util.List;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.MODID;

@Mod.EventBusSubscriber
public class Config {

    private static ForgeConfigSpec COMMON_CONFIG;
    private static ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> forbiddenTrinkets;
    private static ForgeConfigSpec.BooleanValue enableDarkMode;

    static void init() {
        buildConfig();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    }

    private static void buildConfig() {
        String CATEGORY_GENERAL = "general";

        COMMON_BUILDER.comment("general settings").push(CATEGORY_GENERAL);

        forbiddenTrinkets = COMMON_BUILDER.comment("Blacklisted Items - add the name of the item to blacklist, modid:item_name format, separated with comma.",
                        "Example: \"curios:amulet\", \"curios:ring\".")
                .defineList("forbiddenTrinkets", Collections.emptyList(), o -> o instanceof String);

        enableDarkMode = COMMON_BUILDER.comment("Set to true to enable dark mode in the mod's GUI screen").define("enableDarkMode", false);

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static List<? extends String> getForbiddenTrinkets() {
        return forbiddenTrinkets.get();
    }

    public static ResourceLocation getEnableDarkMode() {
        String darkMode = enableDarkMode.get() ? "textures/gui/convenient_screen_dark.png" : "textures/gui/convenient_screen.png";
        return ResourceLocation.fromNamespaceAndPath(MODID, darkMode);
    }
}
