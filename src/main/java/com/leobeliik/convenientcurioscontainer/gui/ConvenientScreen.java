package com.leobeliik.convenientcurioscontainer.gui;

import com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer;
import com.leobeliik.convenientcurioscontainer.common.ConvenientContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.common.inventory.CosmeticCurioSlot;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.MODID;

public class ConvenientScreen extends AbstractContainerScreen<ConvenientContainer> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(MODID, "textures/gui/curios_container.png");
    private final int xSize = 176;
    private final int ySize = 186;
    private int currentScroll = 0;

    public ConvenientScreen(ConvenientContainer container, Inventory inventory, Component title) {
        super(container, inventory, title);
        imageHeight = 186;
        inventoryLabelY = 92;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        renderBackground(ms, mouseX, mouseY, partialTicks);
        super.render(ms, mouseX, mouseY, partialTicks);
        renderTooltip(ms, mouseX, mouseY);
        if (mouseX > leftPos + 168 && mouseX < leftPos + 172 && mouseY > topPos + 3 && mouseY < topPos + 9) {
            ms.renderTooltip(font,
                    List.of(Component.translatable("container_info").withStyle(ChatFormatting.GRAY),
                            Component.translatable("container_RMB").withStyle(ChatFormatting.GRAY),
                            Component.translatable("container_SRMB").withStyle(ChatFormatting.GRAY)),
                    java.util.Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double directionH, double directionV) {
        if (directionH > 0 && menu.canScroll(1)) {
            menu.scroll(1);
            currentScroll--;
            return true;
        }
        if (directionH < 0 && menu.canScroll(-1)) {
            menu.scroll(-1);
            currentScroll++;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, directionH, directionV);
    }

    @ParametersAreNonnullByDefault
    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        ms.blit(CONTAINER_BACKGROUND, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        renderCurios(ms);
    }

    @Override //TODO scroll by dragging the scrollbar
    public boolean mouseDragged(double mouseX, double mouseY, int mouseDragged, double xAmount, double yAmount) {
        return super.mouseDragged(mouseX, mouseY, mouseDragged, xAmount, yAmount);
    }

    private void renderCurios(GuiGraphics ms) {
        //render top and bottom
        if (menu.curiosSize() > 0) {
            int barHeight = Math.min(menu.curiosSize(), 9) * 18 + 12;
            ms.blit(CONTAINER_BACKGROUND, leftPos - 24, topPos + 5, 186, 0, 25, 6); //top
            ms.blit(CONTAINER_BACKGROUND, leftPos - 24, topPos + barHeight, 186, 4, 25, 7); //bottom
            if (menu.hasCosmeticColumn()) {
                ms.blit(CONTAINER_BACKGROUND, leftPos - 43, topPos + 5, 186, 0, 24, 6); //top
                ms.blit(CONTAINER_BACKGROUND, leftPos - 43, topPos + barHeight, 186, 4, 24, 7); //bottom
            }
        }//blit(matrix, x, y, xtexture, ytexture, width, height)

        //render slots and cosmetic
        for (int i = menu.getSlots().size() - 1; i >= 72; i--) {
            Slot slot = menu.getSlots().get(i);
            if (slot instanceof CosmeticCurioSlot) {
                ms.blit(CONTAINER_BACKGROUND, leftPos + slot.x - 6, topPos + slot.y - 2, 186, 11, 23, 19);
            } else if (slot instanceof CurioSlot) {
                ms.blit(CONTAINER_BACKGROUND, leftPos + slot.x - 6, topPos + slot.y - 2, 186, 11, 25, 19);
                if (menu.hasCosmeticColumn()) { //render "empty" bg if has no cosmetic slot
                    ms.blit(CONTAINER_BACKGROUND, leftPos + slot.x - 25, topPos + slot.y - 2, 186, 31, 23, 19);
                }
            }
        }

        //render scrollbar
        if (menu.curiosSize() > 9) {
            int barxPos = menu.hasCosmeticColumn() ? leftPos - 48 : leftPos - 29;
            ms.blit(CONTAINER_BACKGROUND, barxPos, topPos + 5, 176, 0, 10, 176); //bar
            int scrollXPos = menu.hasCosmeticColumn() ? leftPos - 44 : leftPos - 25;
            int scrollYPos = topPos + (currentScroll == 0 ? 12 : Math.min(166 / (menu.curiosSize() - 9) * currentScroll + 12, 166)); //Maths!
            ms.blit(CONTAINER_BACKGROUND, scrollXPos, scrollYPos, 186, 51, 5, 9); //scroll
        }
        //blit(matrix, x, y, xtexture, ytexture, width, height)
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode) || ConvenientCuriosContainer.openConvenientKey.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }
}
