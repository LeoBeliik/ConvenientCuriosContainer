package com.leobeliik.convenientcurioscontainer.platform;

import com.leobeliik.convenientcurioscontainer.Compat;
import com.leobeliik.convenientcurioscontainer.ConvenientConfig;
import com.leobeliik.convenientcurioscontainer.network.Networking;
import com.leobeliik.convenientcurioscontainer.network.PageChange;
import com.leobeliik.convenientcurioscontainer.platform.services.IPlatformHelper;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.menu.AccessoriesInternalSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import java.util.List;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerNeoForge.CONVENIENT_MENU;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerNeoForge.isAccessoriesLoaded;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public MenuType getMenu() {
        return CONVENIENT_MENU.get();
    }

    @Override
    public List<Slot> curiosContainer(Player player) {
        return isAccessoriesLoaded ? Compat.getAccessoriesSlots(player) : Compat.getCurioSlots(player);
    }

    @Override
    public boolean mayPlaceItem(@NotNull ItemStack stack) {
        return !ConvenientConfig.getForbiddenTrinkets().contains(stack.getItem()) && (isAccessoriesLoaded ? AccessoriesAPI.getAccessory(stack) != null : CuriosApi.getCurio(stack).isPresent());
    }

    @Override
    public boolean isCurioSlot(Slot slot) {
        return isAccessoriesLoaded ? slot instanceof AccessoriesInternalSlot : slot instanceof CurioSlot;
    }

    @Override
    public void pageChange(boolean up) {
        Networking.sendToServer(new PageChange(up));
    }
}