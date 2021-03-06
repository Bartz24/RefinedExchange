package com.bartz24.refinedexchange.features.gui;

import com.bartz24.refinedexchange.features.tile.TileEMCCrafter;
import com.bartz24.refinedexchange.features.tile.TileLiquifier;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileCrafter;
import com.raoulvdberge.refinedstorage.tile.TileInterface;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class GuiEMCCrafter extends GuiBase {
    TileEMCCrafter tile;

    public GuiEMCCrafter(ContainerEMCCrafter container) {
        super(container, 211, 217);
        this.tile = container.tile;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileInterface.REDSTONE_MODE));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafter.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        this.drawString(7, 7, t("gui.refinedexchange:emccrafter"), 26);
        this.drawString(7, 43, t("container.inventory", new Object[0]));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawBackground(guiLeft, guiTop, mouseX, mouseY);
    }
}
