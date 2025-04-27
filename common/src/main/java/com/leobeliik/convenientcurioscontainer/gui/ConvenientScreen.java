package com.leobeliik.convenientcurioscontainer.gui;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.MODID;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainerCommon.OPEN_CONVENIENT_SCREEN_KEY;

public class ConvenientScreen extends AbstractContainerScreen<ConvenientMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/convenient_screen.png");
    private final int xSize = 176;
    private final int ySize = 222;
    private Button btnNext;
    private Button btnPrev;

    public ConvenientScreen(ConvenientMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        imageHeight = 222;
        inventoryLabelY = 129;
    }

    @Override
    protected void init() {
        this.addButtons();
        super.init();
    }

    @Override
    public void render(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        renderBackground(ms, mouseX, mouseY, partialTicks);
        super.render(ms, mouseX, mouseY, partialTicks);
        renderWidgets(ms, mouseX, mouseY, partialTicks);
        renderTooltip(ms, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        ms.blit(CONTAINER_BACKGROUND, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //close if the Inventory key or the mod key is pressed
        if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode) || OPEN_CONVENIENT_SCREEN_KEY.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics gg, int mouseX, int mouseY) {
        //render information tooltip
        if (mouseX > this.getX() + 162 && mouseX < this.getX() + 172 && mouseY > this.getY() + 4 && mouseY < this.getY() + 14) {
            gg.renderTooltip(font,
                    List.of(Component.translatable("gui.container_info").withStyle(ChatFormatting.AQUA),
                            Component.translatable("gui.container_RMB").withStyle(ChatFormatting.GRAY),
                            Component.translatable("gui.container_SRMB").withStyle(ChatFormatting.GRAY)),
                    java.util.Optional.empty(), mouseX, mouseY + 5);
        }
        //Button page tooltip
        int maxPage = this.getMenu().getMaxPage();
        if (maxPage > 1 && (mouseX >= btnPrev.getX() && mouseX <= btnPrev.getX() + 23 && mouseY >= btnNext.getY() && mouseY <= btnNext.getY() + 12))
            gg.renderTooltip(font, Component.translatable("gui.curios.page", this.getMenu().getCurrentPage(), maxPage), mouseX, mouseY);

        for (Slot slot : this.menu.slots.subList(90, this.menu.slots.size())) {
            boolean mouseInSlot = mouseX >= leftPos + slot.x && mouseX <= leftPos + slot.x + 16 && mouseY >= topPos + slot.y && mouseY <= topPos + slot.y + 16;
            if (slot.getItem().isEmpty() && mouseInSlot) {
                gg.renderTooltip(font, Component.translatable(slot.toString()), mouseX, mouseY);
            }
        }

        super.renderTooltip(gg, mouseX, mouseY);
    }

    private void addButtons() {
        addWidget(btnNext = Button.builder(Component.empty(), b -> this.getMenu().ChangePage(true))
                .pos(this.getX() - 13, this.getY()).size(12, 12).build());

        addWidget(btnPrev = Button.builder(Component.empty(), b -> this.getMenu().ChangePage(false))
                .pos(this.getX() - 25, this.getY()).size(12, 12).build());
    }

    private void renderWidgets(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        int size = this.menu.slots.size();
        int curiosSize = this.menu.curiosSize;
        int currentPage = this.menu.getCurrentPage();

        //render next/prev buttons
        gg.blit(CONTAINER_BACKGROUND, btnNext.getX(), btnNext.getY(), 188F, 0F, btnNext.getWidth(), btnNext.getHeight(), 256, 256);
        gg.blit(CONTAINER_BACKGROUND, btnPrev.getX(), btnPrev.getY(), 176F, 0F, btnPrev.getWidth(), btnPrev.getHeight(), 256, 256);

        //render next/prev buttons overlay
        btnNext.active = this.menu.getMaxPage() != currentPage;
        if (btnNext.isActive() && btnNext.isMouseOver(mouseX, mouseY)) {
            gg.blit(CONTAINER_BACKGROUND, btnNext.getX(), btnNext.getY(), 200F, 0F, btnNext.getWidth(), btnNext.getHeight(), 256, 256);
        }

        btnPrev.active = currentPage > 1;
        if (btnPrev.isActive() && btnPrev.isMouseOver(mouseX, mouseY)) {
            gg.blit(CONTAINER_BACKGROUND, btnPrev.getX(), btnPrev.getY(), 200F, 0F, btnPrev.getWidth(), btnPrev.getHeight(), 256, 256);
        }

        //curios slot background
        for (int i = 0; i < Math.min(Math.ceilDiv(curiosSize - (33 * (currentPage - 1)), 11), 3); i++) {
            gg.blit(CONTAINER_BACKGROUND, leftPos - 26 - (18 * i), topPos + 12, 176, 12, (i == 0) ? 26 : 23, 210);
        }

        //render curios slots
        for (Slot slot : this.menu.slots.subList(90, Math.min(123, size))) {
            gg.blit(CONTAINER_BACKGROUND, leftPos + slot.x - 1, topPos + slot.y, 7, 17, 18, 18);
        }

    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}
