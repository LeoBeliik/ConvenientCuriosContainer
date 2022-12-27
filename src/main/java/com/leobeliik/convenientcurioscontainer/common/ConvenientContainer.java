package com.leobeliik.convenientcurioscontainer.common;

import com.leobeliik.convenientcurioscontainer.Config;
import com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer;
import com.leobeliik.convenientcurioscontainer.common.slots.ConvenientCosmeticSlots;
import com.leobeliik.convenientcurioscontainer.common.slots.ConvenientCurioSlots;
import com.leobeliik.convenientcurioscontainer.items.ConvenientItem;
import com.leobeliik.convenientcurioscontainer.networking.Network;
import com.leobeliik.convenientcurioscontainer.networking.ScrollMessage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class ConvenientContainer extends AbstractContainerMenu {
    private final ItemStackHandler ccItemHandler;
    private final Player player;
    private final List<ConvenientCurioSlots> curioSlots = new ArrayList<>();
    private final List<ConvenientCosmeticSlots> cosmeticSlots = new ArrayList<>();
    private List<Boolean> hasCosmetic = new ArrayList<>();
    private boolean cosmeticColumn;

    public ConvenientContainer(int windowId, Inventory playerInv, FriendlyByteBuf data) {
        this(windowId, playerInv, new ItemStackHandler(36));
    }

    ConvenientContainer(int id, Inventory inventory, ItemStackHandler ccItemHandler) {
        super(ConvenientCuriosContainer.CURIOS_CONTAINER_CONTAINER.get(), id);
        this.ccItemHandler = ccItemHandler;
        this.player = inventory.player;
        addContainerSlots(inventory);
        addPlayerInvSlots(inventory);
        addCuriosSlots();
    }

    private void addContainerSlots(Inventory inventory) {
        for (int i = 0; i < 4; i++) {
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
                addSlot(new Slot(inventory, j + i * 9 + 9, j * 18 + 8, i * 18 + 104));
            }
        }
        //add toolbar slot
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).getItem() instanceof ConvenientItem) {
                addSlot(new Slot(inventory, i, i * 18 + 8, 162) {
                    @ParametersAreNonnullByDefault
                    @Override
                    public boolean mayPickup(Player player) {
                        return false;
                    }
                });
            } else {
                addSlot(new Slot(inventory, i, i * 18 + 8, 162));
            }
        }
    }

    private void addCuriosSlots() {
        curioSlots.clear();
        cosmeticSlots.clear();
        CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(curios -> {
            int slotY = 13;
            for (Map.Entry<String, ICurioStacksHandler> entry : curios.getCurios().entrySet()) {
                String name = entry.getKey();
                ICurioStacksHandler curioStack = entry.getValue();
                for (int i = 0; i < curioStack.getStacks().getSlots(); i++) {
                    curioSlots.add(new ConvenientCurioSlots(player, curioStack.getStacks(), i, name, -18, slotY, curioStack.getRenders()));
                    if (curioStack.hasCosmetic()) {
                        cosmeticSlots.add(new ConvenientCosmeticSlots(player, curioStack.getCosmeticStacks(), i, name, -37, slotY));
                        cosmeticColumn = true;
                    }
                    slotY += 18;
                }
            }
        });
        addCustomSlots();
    }

    private boolean isItemValid(ItemStack stack) {
        return !CuriosApi.getCuriosHelper().getCurioTags(stack.getItem()).isEmpty()
                && stack.getItem().getRegistryName() != null
                && !Config.getForbiddenTrinkets().contains(stack.getItem().getRegistryName().toString());
    }

    private void addCustomSlots() {
        cosmeticSlots.stream().filter(cosmeticSlot -> cosmeticSlot.getY() >= 13 && cosmeticSlot.getY() <= 157).forEach(this::addSlot);
        curioSlots.stream().filter(curioSlot -> curioSlot.getY() >= 13 && curioSlot.getY() <= 157).forEach(this::addSlot);
    }

    public void scroll(int direction) {
        slots.removeIf(s -> s instanceof CurioSlot);
        List<ConvenientCosmeticSlots> tempCosmetic = cosmeticSlots.stream().map(s ->
                new ConvenientCosmeticSlots(player, s.getHandler(), s.getIndex(), s.getIdentifier(), s.getX(), s.getY() + 18 * direction)).toList();
        cosmeticSlots.clear();
        cosmeticSlots.addAll(tempCosmetic);

        List<ConvenientCurioSlots> tempCurios = curioSlots.stream().map(s ->
                new ConvenientCurioSlots(player, s.getHandler(), s.getIndex(), s.getIdentifier(), s.getX(), s.getY() + 18 * direction, s.getRenders())).toList();
        curioSlots.clear();
        curioSlots.addAll(tempCurios);

        addCustomSlots();
        broadcastChanges();
        if (player.isLocalPlayer()) {
            Network.sendToServer(new ScrollMessage(direction));
        }
    }

    public boolean canScroll(int direction) {
        int firstSlotY = curioSlots.get(0).getY();
        int lastSlotY = curioSlots.get(curioSlots.size() - 1).getY();
        return (direction != 1 || firstSlotY != 13) && (direction != -1 || lastSlotY > 157);
    }

    public int curiosSize() {
        return curioSlots.size();
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public boolean hasCosmeticColumn() {
        return cosmeticColumn;
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
            int size = 36;
            if (index < size) {
                if (!moveItemStackTo(itemStackOG, size, slots.size(), true)) {
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

                if (slot.mayPlace(cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && ItemStack.tagMatches(stack, existingStack)) {
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
    /* ***************************************************************************************************************************** */

    public boolean itemHasAtt(int slot, boolean isSwap) {
        //little hack for #5; to be fixed in 2.0 or when I have time
        //TODO keep this in mind for the rewrite
        if (CuriosApi.getSlotHelper() == null) return false;
        int targetSlots = isSwap ? 0 : 72;
        if (slot >= targetSlots && !getSlots().get(slot).getItem().isEmpty()) {
            if (!CuriosApi.getCuriosHelper().getAttributeModifiers("", getSlots().get(slot).getItem()).isEmpty()) {
                return CuriosApi.getCuriosHelper().getAttributeModifiers("", getSlots().get(slot).getItem()).asMap().values().stream().flatMap(Collection::stream).anyMatch(modifier ->
                        CuriosApi.getSlotHelper().getSlotTypes().stream().anyMatch(slotType ->
                                modifier.getName().equals(slotType.getIdentifier())));
            }
        }
        return false;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void clicked(int slot, int mouseClick, ClickType type, Player player) {
        if (itemHasAtt(slot, false)) return;

        if (mouseClick == 1 && slot >= 0 && slot < 36 && slots.get(slot).hasItem()) {
            if (type == ClickType.PICKUP) {
                swapCurios(slots.get(slot), player, false);
                return;
            } else if (type == ClickType.QUICK_MOVE) {
                swapCurios(slots.get(slot), player, true);
                return;
            }
        }
        super.clicked(slot, mouseClick, type, player);
    }

    private void swapCurios(Slot slot, Player player, boolean secondSlot) {
        if (itemHasAtt(slot.index, true)) return;

        ItemStack curiosItem = null;
        ItemStack containerItem = slot.getItem();
        Slot curioSlot = null;
        for (Slot cs : curioSlots) {
            if (cs.mayPlace(containerItem)) {
                if (secondSlot) {
                    secondSlot = false;
                    continue;
                }
                curiosItem = cs.getItem();
                curioSlot = cs;
                if (itemHasAtt(cs.index, true)) return;
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
