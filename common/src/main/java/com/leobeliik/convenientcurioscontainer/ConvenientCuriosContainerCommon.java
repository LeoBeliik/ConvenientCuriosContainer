package com.leobeliik.convenientcurioscontainer;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.contents.TranslatableContents;

public class ConvenientCuriosContainerCommon {
    public static final String MODID = "convenientcurioscontainer";
    public static final KeyMapping OPEN_CONVENIENT_SCREEN_KEY = new KeyMapping(
            new TranslatableContents("key.open_convenient_screen", "Open Curios Container", TranslatableContents.NO_ARGS).getKey(),
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.categories.misc");
}
