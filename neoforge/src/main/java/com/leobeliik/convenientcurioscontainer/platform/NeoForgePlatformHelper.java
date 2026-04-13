package com.leobeliik.convenientcurioscontainer.platform;

import com.leobeliik.convenientcurioscontainer.Compat;
import com.leobeliik.convenientcurioscontainer.ConvenientConfig;
import com.leobeliik.convenientcurioscontainer.network.PageChange;
import com.leobeliik.convenientcurioscontainer.platform.services.IPlatformHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosSlotTypes;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import java.util.List;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerNeoForge.CONVENIENT_MENU;

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
        if (FMLLoader.getCurrentOrNull() != null)
            return !FMLLoader.getCurrent().isProduction();
        return true;
    }

    @Override
    public MenuType getMenu() {
        return CONVENIENT_MENU.get();
    }

    @Override
    public List<Slot> curiosContainer(Player player) {
        return Compat.getCurioSlots(player);
        //return isAccessoriesLoaded ? Compat.getAccessoriesSlots(player) : Compat.getCurioSlots(player);
    }

    @Override
    public boolean mayPlaceItem(@NotNull ItemStack stack, LivingEntity player) {
        if (ConvenientConfig.getForbiddenTrinkets().contains(stack.getItem()))
            return false;
        else if (ConvenientConfig.getAllowedTrinkets().contains(stack.getItem()))
            return true;
        else return !CuriosSlotTypes.getItemSlotTypes(stack, player).isEmpty();
        /*stack.getComponents().stream().anyMatch(tag -> tag.toString().contains("curios") || tag.toString().contains("accessories"));*/
        //&& (isAccessoriesLoaded ? AccessoriesAPI.getAccessory(stack) != null : CuriosApi.getCurio(stack).isPresent());
    }

    @Override
    public boolean isCurioSlot(Slot slot) {
        return slot instanceof CurioSlot;
        //return isAccessoriesLoaded ? slot instanceof AccessoriesInternalSlot : slot instanceof CurioSlot;
    }

    @Override
    public void pageChange(boolean up) {
        ClientPacketDistributor.sendToServer(new PageChange(up));
    }

    @Override
    public boolean darkMode() { return ConvenientConfig.getEnableDarkMode(); }
}