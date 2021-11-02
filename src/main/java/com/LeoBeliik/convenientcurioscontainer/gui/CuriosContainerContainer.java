package com.LeoBeliik.convenientcurioscontainer.gui;

import com.LeoBeliik.convenientcurioscontainer.ConvenientCuriosContainer;
import com.LeoBeliik.convenientcurioscontainer.gui.slots.ConvenientCosmeticSlots;
import com.LeoBeliik.convenientcurioscontainer.gui.slots.ConvenientCurioSlots;
import com.LeoBeliik.convenientcurioscontainer.items.CuriosContainerItem;
import com.LeoBeliik.convenientcurioscontainer.networking.Network;
import com.LeoBeliik.convenientcurioscontainer.networking.ScrollMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CuriosContainerContainer extends Container {
    private final ItemStackHandler ccItemHandler;
    private final PlayerEntity player;
    private final List<ConvenientCurioSlots> curioSlots = new ArrayList<>();
    private final List<ConvenientCosmeticSlots> cosmeticSlots = new ArrayList<>();
    private List<Boolean> hasCosmetic = new ArrayList<>();
    private boolean cosmeticColumn;

    public CuriosContainerContainer(int windowId, PlayerInventory playerInv, PacketBuffer data) {
        this(windowId, playerInv, new ItemStackHandler(36));
    }

    public CuriosContainerContainer(int id, PlayerInventory inventory, ItemStackHandler ccItemHandler) {
        super(ConvenientCuriosContainer.CURIOS_CONTAINER_CONTAINER.get(), id);
        this.ccItemHandler = ccItemHandler;
        this.player = inventory.player;
        addContainerSlots(inventory);
        addPlayerInvSlots(inventory);
        addCuriosSlots();
    }

    private void addContainerSlots(PlayerInventory inventory) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(ccItemHandler, j + i * 9, j * 18 + 8, i * 18 + 18) {
                    @Override
                    public boolean mayPlace(@Nonnull ItemStack stack) {
                        return !CuriosApi.getCuriosHelper().getCurioTags(stack.getItem()).isEmpty();
                    }
                });
            }
        }
    }

    private void addPlayerInvSlots(PlayerInventory inventory) {
        //add inventory slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, j * 18 + 8, i * 18 + 104));
            }
        }
        //add toolbar slot
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).getItem() instanceof CuriosContainerItem) {
                addSlot(new Slot(inventory, i, i * 18 + 8, 162) {
                    @ParametersAreNonnullByDefault
                    @Override
                    public boolean mayPickup(PlayerEntity player) {
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

    private void addCustomSlots() {
        cosmeticSlots.stream().filter(cosmeticSlot -> cosmeticSlot.getY() >= 13 && cosmeticSlot.getY() <= 157).forEach(this::addSlot);
        curioSlots.stream().filter(curioSlot -> curioSlot.getY() >= 13 && curioSlot.getY() <= 157).forEach(this::addSlot);
    }

    public void scroll(int direction) {
        slots.removeIf(s -> s instanceof CurioSlot);
        List<ConvenientCosmeticSlots> tempCosmetic = cosmeticSlots.stream().map(s ->
                        new ConvenientCosmeticSlots(player, s.getHandler(), s.getIndex(), s.getIdentifier(), s.getX(), s.getY() + 18 * direction))
                .collect(Collectors.toList());
        cosmeticSlots.clear();
        cosmeticSlots.addAll(tempCosmetic);

        List<ConvenientCurioSlots> tempCurios = curioSlots.stream().map(s ->
                        new ConvenientCurioSlots(player, s.getHandler(), s.getIndex(), s.getIdentifier(), s.getX(), s.getY() + 18 * direction, s.getRenders()))
                .collect(Collectors.toList());
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
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemStackCopy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
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

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public ItemStack clicked(int slot, int mouseClick, ClickType type, PlayerEntity player) {
        if (mouseClick == 1 && slot >= 0 && slot < 36 && slots.get(slot).hasItem()) {
            if (type == ClickType.PICKUP) {
                return swapCurios(slots.get(slot), player, false);
            } else if (type == ClickType.QUICK_MOVE) {
                return swapCurios(slots.get(slot), player, true);
            }
        }
        return super.clicked(slot, mouseClick, type, player);
    }

    private ItemStack swapCurios(Slot slot, PlayerEntity player, boolean secondSlot) {
        ItemStack itemstack1 = null;
        ItemStack itemstack2 = slot.getItem();
        Slot curioSlot = null;
        for (Slot cs : curioSlots) {
            if (cs.mayPlace(itemstack2)) {
                if (secondSlot) {
                    secondSlot = false;
                    continue;
                }
                itemstack1 = cs.getItem();
                curioSlot = cs;
                break;
            }
        }
        if (itemstack1 == null) {
            return slot.getItem();
        }
        if (!itemstack1.isEmpty() || !itemstack2.isEmpty()) {
            if (itemstack1.isEmpty()) {
                if (slot.mayPickup(player)) {
                    curioSlot.set(itemstack2);
                    slot.set(ItemStack.EMPTY);
                    slot.onTake(player, itemstack2);
                }
            } else if (itemstack2.isEmpty()) {
                if (slot.mayPlace(itemstack1)) {
                    int i = slot.getMaxStackSize(itemstack1);
                    if (itemstack1.getCount() > i) {
                        slot.set(itemstack1.split(i));
                    } else {
                        slot.set(itemstack1);
                        curioSlot.set(ItemStack.EMPTY);
                    }
                }
            } else if (slot.mayPickup(player) && slot.mayPlace(itemstack1)) {
                int l1 = slot.getMaxStackSize(itemstack1);
                if (itemstack1.getCount() > l1) {
                    slot.set(itemstack1.split(l1));
                    slot.onTake(player, itemstack2);
                    if (!curioSlot.mayPlace(itemstack2)) {
                        player.drop(itemstack2, true);
                    }
                } else {
                    slot.set(itemstack1);
                    curioSlot.set(itemstack2);
                    slot.onTake(player, itemstack2);
                }
            }
        }
        return itemstack2;
    }
}
