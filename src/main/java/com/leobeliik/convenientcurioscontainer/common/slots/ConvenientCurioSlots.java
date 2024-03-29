package com.leobeliik.convenientcurioscontainer.common.slots;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.NonNullList;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public class ConvenientCurioSlots extends CurioSlot {
    private IDynamicStackHandler handler;
    private int index;
    private String identifier;
    private int x;
    private int y;
    private NonNullList<Boolean> renders;

    public ConvenientCurioSlots(Player player, IDynamicStackHandler handler, int index, String identifier, int xPosition, int yPosition, NonNullList<Boolean> renders) {
        super(player, handler, index, identifier, xPosition, yPosition, renders, false);
        this.handler = handler;
        this.index = index;
        this.identifier = identifier;
        this.x = xPosition;
        this.y = yPosition;
        this.renders = renders;
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
}
