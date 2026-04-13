package com.leobeliik.convenientcurioscontainer;

/*import com.google.common.collect.ImmutableSet;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotGroup;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.data.SlotGroupLoader;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories.menu.AccessoriesInternalSlot;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;*/
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import java.util.*;

public abstract class Compat {

    public static List<Slot> getCurioSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        var curiosHandler = CuriosApi.getCuriosInventory(player).orElse(null);
        int tall = 0, wide = 0;
        if (curiosHandler != null) {
            Map<String, ICurioStacksHandler> curioMap = curiosHandler.getCurios();
            for (String identifier : curioMap.keySet()) {
                ICurioStacksHandler stacksHandler = curioMap.get(identifier);
                IDynamicStackHandler stackHandler = stacksHandler.getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++, tall++) {
                    if (tall != 0 && tall % 11 == 0) {
                        wide--;
                        tall = 0;

                        if (wide == -3) wide = 0;
                    }
                    slots.add(new CurioSlot(player, stackHandler, i, identifier,
                            wide * 18 - 20,
                            tall * 18 + 18, stacksHandler.getActiveStates(), stacksHandler.getRenders(),
                            stacksHandler.canToggleRendering(), false) {
                        @Override
                        public String toString() {
                            return "curios.identifier." + identifier;
                        }
                    });
                }
            }
        }

        return slots;
    }

    /*public static List<Slot> getAccessoriesSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        ImmutableSet<SlotType> usedSlots = ImmutableSet.copyOf(AccessoriesAPI.getUsedSlotsFor(player));

        var slotTypes = SlotGroupLoader.getGroups(player.level(), true).stream()
                .sorted(Comparator.comparingInt(SlotGroup::order).reversed())
                .flatMap(slotGroup -> {
                    if (slotGroup.name().equals(Accessories.MODID)) return Stream.of();
                    return slotGroup.slots().stream()
                            .map(s -> {
                                SlotType slotType = SlotTypeLoader.getSlotType(player.level(), s);
                                return !usedSlots.contains(slotType) ? null : slotType; //no need to show unused slots afaik
                            })
                            .filter(Objects::nonNull)
                            .sorted(Comparator.comparingInt(SlotType::order).reversed());
                }).toList();

        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability != null) {
            int tall = 0, wide = 0;

            for (SlotType slot : slotTypes) {
                AccessoriesContainer accessoryContainer = capability.getContainers().get(slot.name());

                if (accessoryContainer == null || accessoryContainer.slotType() == null) continue;

                for (int i = 0; i < accessoryContainer.getSize(); i++, tall++) {
                    if (tall != 0 && tall % 11 == 0) {
                        wide--;
                        tall = 0;
                    }

                    slots.add(new AccessoriesInternalSlot(accessoryContainer, false, i, wide * 18 - 20, tall * 18 + 18){
                        @Override
                        public String toString() {
                            return slotType().translation();
                        }
                    });
                }
            }
        }

        return slots;
    }*/
}
