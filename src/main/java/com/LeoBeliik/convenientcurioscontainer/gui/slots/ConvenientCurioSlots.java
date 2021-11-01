package com.LeoBeliik.convenientcurioscontainer.gui.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.NonNullList;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public class ConvenientCurioSlots extends CurioSlot {
    private PlayerEntity player;
    private IDynamicStackHandler handler;
    private int index;
    private String identifier;
    private int x;
    private int y;
    private NonNullList<Boolean> renders;

    public ConvenientCurioSlots(PlayerEntity player, IDynamicStackHandler handler, int index, String identifier, int xPosition, int yPosition, NonNullList<Boolean> renders) {
        super(player, handler, index, identifier, xPosition, yPosition, renders);
        this.player = player;
        this.handler = handler;
        this.index = index;
        this.identifier = identifier;
        this.x = xPosition;
        this.y = yPosition;
        this.renders = renders;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public IDynamicStackHandler getHandler() {
        return handler;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public NonNullList<Boolean> getRenders() {
        return renders;
    }

    public void setRenders(NonNullList<Boolean> renders) {
        this.renders = renders;
    }
}
