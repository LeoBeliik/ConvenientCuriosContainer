package com.leobeliik.convenientcurioscontainer;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
public class Config {

    private static ForgeConfigSpec COMMON_CONFIG;
    private static ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> forbiddenTrinkets;

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

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static List<? extends String> getForbiddenTrinkets() {
        return forbiddenTrinkets.get();
    }
}
