package com.leobeliik.convenientcurioscontainer.common;

import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import com.leobeliik.convenientcurioscontainer.network.Networking;
import com.leobeliik.convenientcurioscontainer.network.PageChange;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.CONVENIENT_MENU;

public class ConvenientMenu extends AbstractContainerMenu {
    private final ItemStackHandler ccItemHandler;
    private final Player player;
    public final CuriosContainer container;

    public ConvenientMenu(int containerId, Inventory playerInv) {
        this(containerId, playerInv, new ItemStackHandler(54));
    }

    ConvenientMenu(int containerId, Inventory playerInv, ItemStackHandler ccItemHandler) {
        super(CONVENIENT_MENU.get(), containerId);
        this.player = playerInv.player;
        this.container = new CuriosContainer(containerId, playerInv);
        this.ccItemHandler = ccItemHandler;
        fillSlots();
    }

    private void fillSlots() {
        addContainerSlots();
        addPlayerInvSlots();
        addCuriosSlots();
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
                addSlot(new SlotItemHandler(ccItemHandler, j + i * 9, j * 18 + 8, i * 18 + 18));
            }
        }
    }

    private void addPlayerInvSlots() {
        //add inventory slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(player.getInventory(), j + i * 9 + 9, j * 18 + 8, i * 18 + 140));
            }
        }
        //add toolbar slot
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(player.getInventory(), i, i * 18 + 8, 198));
        }
    }

    @Override
    public void clicked(int slot, int mouseClick, @NotNull ClickType type, @NotNull Player player) {
        //prevent moving the ccc item
        if (slot >= 54 && slot < 90 && getSlot(slot).hasItem()) {
            if (getSlot(slot).getItem().getItem() instanceof ConvenientItem) return;
        }

        if (mouseClick == 1 && slot >= 0 && slot < 54 && getSlot(slot).hasItem()) {
            if (type == ClickType.PICKUP) {
                swapCurios(getSlot(slot), player, false);
                return;
            } else if (type == ClickType.QUICK_MOVE) {
                swapCurios(getSlot(slot), player, true);
                return;
            }
        }

        super.clicked(slot, mouseClick, type, player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemStackCopy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStackOG = slot.getItem();
            itemStackCopy = itemStackOG.copy();
            int size = 54;
            int curioSlotSize = slots.size() - 90;
            if (index < size) {
                if (!moveItemStackTo(itemStackOG, size, slots.size() - curioSlotSize, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemStackOG, 0, size, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStackOG.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStackCopy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
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

        fillSlots();
    }

    public void ClearSlots() {
        slots.clear();
        container.resetSlots();
        fillSlots();
    }

    private void swapCurios(Slot slot, Player player, boolean secondSlot) {
        ItemStack curiosItem = null;
        ItemStack containerItem = slot.getItem();
        Slot curioSlot = null;
        for (Slot cs : this.slots) {
            if (cs instanceof CurioSlot && cs.mayPlace(containerItem)) {
                if (secondSlot) {
                    secondSlot = false;
                    continue;
                }
                curiosItem = cs.getItem();
                curioSlot = cs;
                break;
            }
        }
        if (curiosItem == null) {
            return;
        }
        if (!curiosItem.isEmpty() || !containerItem.isEmpty()) {
            if (curiosItem.isEmpty()) {
                if (slot.mayPickup(player)) {
                    curioSlot.set(containerItem);
                    slot.set(ItemStack.EMPTY);
                    slot.onTake(player, containerItem);
                }
            } else if (slot.mayPickup(player) && slot.mayPlace(curiosItem) && curioSlot.mayPickup(player)) {
                int l1 = slot.getMaxStackSize(curiosItem);
                if (curiosItem.getCount() > l1) {
                    slot.set(curiosItem.split(l1));
                    slot.onTake(player, containerItem);
                    if (!curioSlot.mayPlace(containerItem)) {
                        player.drop(containerItem, true);
                    }
                } else {
                    slot.set(curiosItem);
                    curioSlot.set(containerItem);
                    slot.onTake(player, containerItem);
                }
            }
        }
    }
}
