package com.leobeliik.convenientcurioscontainer.gui;

import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.MODID;

public class ConvenientScreen extends AbstractContainerScreen<ConvenientMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/curios_screen.png");
    private final int xSize = 176;
    private final int ySize = 222;
    private int currentScroll = 0;

    public ConvenientScreen(ConvenientMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        imageHeight = 222;
        inventoryLabelY = 129;
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

    @ParametersAreNonnullByDefault
    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        ms.blit(CONTAINER_BACKGROUND, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        //renderCurios(ms);
    }

    private int getX() {
        return (this.width - xSize) / 2;
    }

    private int getY() {
        return (this.height - ySize) / 2;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) {
            this.getMenu().ChangePage(true);
        }
        if (keyCode == 335) {
            this.getMenu().ChangePage(false);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        ConvenientMenu.isConvenient = false;
        super.onClose();
    }
}
