package com.LeoBeliik.convenientcurioscontainer.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConvenientCapabilityProvider implements ICapabilitySerializable<INBT> {
    private IItemHandler ccInventory;
    private final LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(this::getCachedInventory);

    @Nonnull
    private IItemHandler getCachedInventory() {
        if (ccInventory == null) {
            ccInventory = new ItemStackHandler(36);
        }
        return ccInventory;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, lazyInventory);
    }

    @Override
    public INBT serializeNBT() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(getCachedInventory(), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(getCachedInventory(), null, nbt);
    }
}
