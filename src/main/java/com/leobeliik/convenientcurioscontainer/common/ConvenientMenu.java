package com.leobeliik.convenientcurioscontainer.common;

import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import com.leobeliik.convenientcurioscontainer.network.Networking;
import com.leobeliik.convenientcurioscontainer.network.PageChange;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;

import javax.annotation.Nonnull;

import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.CURIOS_CONTAINER_MENU;

public class ConvenientMenu extends AbstractContainerMenu {
    private final ItemStackHandler ccItemHandler = new ItemStackHandler(54);
    private final Player player;
    private final ICuriosItemHandler curiosHandler;
    private CuriosContainer container;
    public static boolean isConvenient = false;

    public ConvenientMenu(int containerId, Inventory playerInv) {
        super(CURIOS_CONTAINER_MENU.get(), containerId);
        isConvenient = true;
        this.player = playerInv.player;
        this.curiosHandler = CuriosApi.getCuriosInventory(this.player).orElse(null);
        this.container = new CuriosContainer(containerId, playerInv);
        fillSlots(playerInv);
    }

    private void fillSlots(Inventory inventory) {
        //this.slots.clear();
        addCuriosSlots();
        addPlayerInvSlots(inventory);
        addContainerSlots();
        broadcastChanges();
    }

    private void addCuriosSlots() {
        container.slots.stream().filter(slot -> slot instanceof CurioSlot).forEach(slot -> {
            slot.index = slot.index - 46;
            addSlot(slot);
        });
    }

    private void addContainerSlots() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(ccItemHandler, j + i * 9, j * 18 + 8, i * 18 + 18) {
                    @Override
                    public boolean mayPlace(@Nonnull ItemStack stack) {
                        return isItemValid(stack);
                    }
                });
            }
        }
    }

    private void addPlayerInvSlots(Inventory inventory) {
        //add inventory slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, j * 18 + 8, i * 18 + 140));
            }
        }
        //add toolbar slot
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).getItem() instanceof ConvenientItem) {
                addSlot(new Slot(inventory, i, i * 18 + 8, 198) {
                    @Override
                    public boolean mayPickup(@NotNull Player player) {
                        return false;
                    }
                });
            } else {
                addSlot(new Slot(inventory, i, i * 18 + 8, 198));
            }
        }
    }

    private boolean isItemValid(ItemStack stack) {
        return CuriosApi.getCurio(stack).isPresent()
                /*&& !Config.getForbiddenTrinkets().contains(stack.getItem().getName(stack).toString())*/;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        //TEMPORARY
        ItemStack itemstack = ItemStack.EMPTY;
        Slot s = this.slots.get(slot);
        if (s != null && s.hasItem()) {
            ItemStack itemstack1 = s.getItem();
            itemstack = itemstack1.copy();
            if (slot < 36) {
                if (!this.moveItemStackTo(itemstack1, 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                s.setByPlayer(ItemStack.EMPTY);
            } else {
                s.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void ChangePage(boolean b) {
        slots.clear();
        if (player.level().isClientSide()) {
            Networking.sendToServer(new PageChange(b));
        }

        if (b) {
            container.nextPage();
        } else {
            container.prevPage();
        }

        fillSlots(player.getInventory());
    }

    public void ClearSlots() {
        slots.clear();
        container.resetSlots();
        fillSlots(player.getInventory());
    }

}
