package com.leobeliik.convenientcurioscontainer.platform;

import com.leobeliik.convenientcurioscontainer.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public MenuType getMenu() {
        return null;
    }

    @Override
    public List<Slot> curiosContainer(Player player) {
        return Collections.emptyList();
    }

    @Override
    public boolean mayPlaceItem(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isCurioSlot(Slot slot) {
        return false;
    }
}
