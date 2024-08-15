package com.leobeliik.convenientcurioscontainer.gui;

import com.leobeliik.convenientcurioscontainer.ConvenientClientReg;
import com.leobeliik.convenientcurioscontainer.common.ConvenientMenu;
import com.leobeliik.convenientcurioscontainer.network.Networking;
import com.leobeliik.convenientcurioscontainer.network.SwitchCCC;
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
import top.theillusivec4.curios.CuriosConstants;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.ICuriosScreen;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import static com.leobeliik.convenientcurioscontainer.ConvenientCuriosContainer.MODID;

public class ConvenientScreen extends AbstractContainerScreen<ConvenientMenu> implements ICuriosScreen {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/convenient_screen.png");
    private static final ResourceLocation CURIO_INVENTORY = ResourceLocation.fromNamespaceAndPath(CuriosConstants.MOD_ID, "textures/gui/curios/inventory.png");
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

    @ParametersAreNonnullByDefault
    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        renderBackground(ms, mouseX, mouseY, partialTicks);
        super.render(ms, mouseX, mouseY, partialTicks);
        if (this.getMenu().container.totalPages > 1) {
            renderWidgets(ms, mouseX, mouseY, partialTicks);
        }
        renderTooltip(ms, mouseX, mouseY);
    }

    @ParametersAreNonnullByDefault
    @Override
    protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        ms.blit(CONTAINER_BACKGROUND, getX(), getY(), 0, 0, xSize, ySize); //Main screen bounds
        renderCuriosPage(ms, partialTicks, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //close if the Inventory key or the mod key is pressed
        if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode) || ConvenientClientReg.OPEN_CONVENIENT_SCREEN_KEY.get().matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics ms, int mouseX, int mouseY) {
        //render button tooltips
        if (this.getMenu().container.totalPages > 1 && (mouseX > 97 && mouseX < 216 && mouseY > 11 && mouseY < 23)) {
            ms.renderTooltip(font,
                    Component.translatable("gui.curios.page", (this.getMenu().container.currentPage + 1), this.getMenu().container.totalPages),
                    mouseX, mouseY);
        }

        //render information tooltip
        if (mouseX > this.getX() + 162 && mouseX < this.getX() + 172 && mouseY > this.getY() + 4 && mouseY < this.getY() + 14) {
            ms.renderTooltip(font,
                    List.of(Component.translatable("gui.container_info").withStyle(ChatFormatting.AQUA),
                            Component.translatable("gui.container_RMB").withStyle(ChatFormatting.GRAY),
                            Component.translatable("gui.container_SRMB").withStyle(ChatFormatting.GRAY)),
                    java.util.Optional.empty(), mouseX, mouseY + 5);
        }
        super.renderTooltip(ms, mouseX, mouseY);
    }

    private void addButtons() {
        addWidget(btnNext = Button.builder(Component.empty(), b -> this.getMenu().ChangePage(true))
                .pos(this.getX() - 17, this.getY() + 2).size(11, 12).build()).visible = this.getMenu().container.totalPages > 1;

        addWidget(btnPrev = Button.builder(Component.empty(), b -> this.getMenu().ChangePage(false))
                .pos(this.getX() - 28, this.getY() + 2).size(11, 12).build()).visible = this.getMenu().container.totalPages > 1;
    }

    private void renderWidgets(@Nonnull GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        //next button render
        int btnNextRenderY = this.getMenu().container.currentPage + 1 == this.getMenu().container.totalPages ? 37 : 25;
        int btnNextRenderX = btnNext.isMouseOver(x, y) ? 65 : 43;
        guiGraphics.blit(CURIO_INVENTORY, this.getX() - 17, this.getY() + 2, btnNextRenderX, btnNextRenderY, 11, 12);
        btnNext.active = btnNextRenderY == 25;

        //prev button render
        int btnPrevRenderY = this.getMenu().container.currentPage == 0 ? 37 : 25;
        int btnPrevRenderX = btnPrev.isMouseOver(x, y) ? 54 : 32;
        guiGraphics.blit(CURIO_INVENTORY, this.getX() - 28, this.getY() + 2, btnPrevRenderX, btnPrevRenderY, 11, 12);
        btnPrev.active = btnPrevRenderY == 25;
    }

    //copy of CuriosScreen#renderBg
    private void renderCuriosPage(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null) {
            CuriosContainer container = this.getMenu().container;
            int i = this.getX();
            int j = this.getY();

            CuriosApi.getCuriosInventory(this.minecraft.player).ifPresent(handler -> {
                int xOffset = -33;
                int yOffset = j;
                boolean pageOffset = container.totalPages > 1;

                if (container.hasCosmetics) {
                    ms.blit(CURIO_INVENTORY, i + xOffset + 2, yOffset - 23, 32, 0, 28, 24);
                }
                List<Integer> grid = container.grid;
                xOffset -= (grid.size() - 1) * 18;

                // render backplate
                for (int r = 0; r < grid.size(); r++) {
                    int rows = grid.getFirst();
                    int upperHeight = 7 + rows * 18;
                    int xTexOffset = 91;

                    if (pageOffset) {
                        upperHeight += 8;
                    }

                    if (r != 0) {
                        xTexOffset += 7;
                    }
                    ms.blit(CURIO_INVENTORY, i + xOffset, yOffset, xTexOffset, 0, 25, upperHeight);
                    ms.blit(CURIO_INVENTORY, i + xOffset, yOffset + upperHeight, xTexOffset, 159, 25, 7);

                    if (grid.size() == 1) {
                        xTexOffset += 7;
                        ms.blit(CURIO_INVENTORY, i + xOffset + 7, yOffset, xTexOffset, 0, 25, upperHeight);
                        ms.blit(CURIO_INVENTORY, i + xOffset + 7, yOffset + upperHeight, xTexOffset, 159, 25, 7);
                    }

                    xOffset += r == 0 ? 25 : 18;
                }
                xOffset -= (grid.size()) * 18;

                if (pageOffset) {
                    yOffset += 8;
                }

                // render slots
                for (int rows : grid) {
                    int upperHeight = rows * 18;

                    ms.blit(CURIO_INVENTORY, i + xOffset, yOffset + 7, 7, 7, 18, upperHeight);
                    xOffset += 18;
                }
                RenderSystem.enableBlend();

                for (Slot slot : this.getMenu().slots) {

                    if (slot instanceof CurioSlot curioSlot && curioSlot.isCosmetic()) {
                        ms.blit(CURIO_INVENTORY, slot.x + this.getX() - 1,
                                slot.y + this.getY() - 1, 32, 50, 18, 18);
                    }
                }
                RenderSystem.disableBlend();
            });
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
        Networking.sendToServer(new SwitchCCC(false));
        super.onClose();
    }
}
