package com.leobeliik.convenientcurioscontainer.common;

import com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer;
import com.leobeliik.convenientcurioscontainer.compat.CompatHelper;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ConvenientContainer extends AbstractContainerMenu {
    private final Player player;
    private final ItemStackHandler ccItemHandler;
    private final ItemStack curioItem;
    private int currentPage = 1, maxPage = 1;
    public int curiosSize = 0;

    public ConvenientContainer(int windowId, Inventory playerInv, FriendlyByteBuf data) {
        this(windowId, playerInv, new ItemStackHandler(54), null);
    }

    ConvenientContainer(int id, Inventory inventory, ItemStackHandler ccItemHandler, ItemStack curioItem) {
        super(ConvenientCuriosContainer.CURIOS_CONTAINER_CONTAINER.get(), id);
        this.player = inventory.player;
        this.ccItemHandler = ccItemHandler;
        this.curioItem = curioItem;
        addSlots();
    }

    public void addSlots() {
        super.slots.clear();
        addContainerSlots();
        addPlayerInvSlots();
        addCuriosSlots();
        broadcastChanges();
    }

    private void addContainerSlots() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(ccItemHandler, j + i * 9, j * 18 + 8, i * 18 + 18) {
                    @Override
                    public boolean mayPlace(@Nonnull ItemStack stack) {
                        return CompatHelper.mayPlaceItem(stack) /* && !ConvenientConfig.getForbiddenTrinkets().contains(stack.getItem())*/;
                    }
                });
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

    private void addCuriosSlots() {
        List<Slot> slots = CompatHelper.curiosContainer(player);
        int pageSize = 33;
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, slots.size());
        this.maxPage = CompatHelper.ceilDiv(slots.size(), pageSize);
        this.curiosSize = slots.size();
        slots.subList(startIndex, endIndex).forEach(this::addSlot);
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
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

    /*
     * This was stolen from Quark
     * https://github.com/VazkiiMods/Quark/blob/25e736f8828c8909ab765b9f2ea457d3b188902c/src/main/java/vazkii/quark/addons/oddities/inventory/BackpackMenu.java#L121
     * I'll move all this logic to Forge Capabilities in the next version (2.0)
     */
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int start, int length, boolean r) {
        boolean successful = false;
        int i = !r ? start : length - 1;
        int iterOrder = !r ? 1 : -1;

        Slot slot;
        ItemStack existingStack;

        if (stack.isStackable()) while (stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
            slot = slots.get(i);

            existingStack = slot.getItem();

            if (!existingStack.isEmpty()) {
                int maxStack = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                int rmv = Math.min(maxStack, stack.getCount());

                if (slot.mayPlace(cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && ItemStack.isSameItemSameTags(stack, existingStack)) {
                    int existingSize = existingStack.getCount() + stack.getCount();

                    if (existingSize <= maxStack) {
                        stack.setCount(0);
                        existingStack.setCount(existingSize);
                        slot.set(existingStack);
                        successful = true;
                    } else if (existingStack.getCount() < maxStack) {
                        stack.shrink(maxStack - existingStack.getCount());
                        existingStack.setCount(maxStack);
                        slot.set(existingStack);
                        successful = true;
                    }
                }
            }
            i += iterOrder;
        }
        if (stack.getCount() > 0) {
            i = !r ? start : length - 1;
            while (stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
                slot = slots.get(i);
                existingStack = slot.getItem();

                if (existingStack.isEmpty()) {
                    int maxStack = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                    int rmv = Math.min(maxStack, stack.getCount());

                    if (slot.mayPlace(cloneStack(stack, rmv))) {
                        existingStack = stack.split(rmv);
                        slot.set(existingStack);
                        successful = true;
                    }
                }
                i += iterOrder;
            }
        }
        return successful;
    }

    private static ItemStack cloneStack(ItemStack stack, int size) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack copy = stack.copy();
        copy.setCount(size);
        return copy;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void clicked(int slot, int mouseClick, ClickType type, Player player) {
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

    private void swapCurios(Slot slot, Player player, boolean secondSlot) {
        ItemStack curiosItem = null;
        ItemStack containerItem = slot.getItem();
        Slot curioSlot = null;
        for (Slot cs : this.slots) {
            if (CompatHelper.isCurioSlot(cs) && cs.mayPlace(containerItem)) {
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void ChangePage(boolean b) {
        slots.clear();
        if (player.level().isClientSide()) {
            CompatHelper.pageChange(b);
        }

        if (b && currentPage < maxPage) currentPage++;
        if (!b && currentPage > 1) currentPage--;
        addSlots();
    }

    public int getMaxPage() {
        return maxPage;
    }

    @Override
    public void removed(Player player) {
        if (!player.level().isClientSide && curioItem != null) {
            curioItem.getOrCreateTag().putInt("CustomModelData", 0);
        }
    }
}
