package com.bartz24.refinedexchange.features.gui;

import com.bartz24.refinedexchange.features.tile.TileLiquifier;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileInterface;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class GuiLiquifier extends GuiBase {
    TileLiquifier tile;

    public GuiLiquifier(ContainerLiquifier container) {
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
        bindTexture("gui/liquifier.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 46, t("container.inventory"));
        this.drawString(7, 7, t("gui.refinedexchange:liquifier"), 26);

        drawString(102, 18, "EMC Stored:", Color.GREEN.getRGB());
        drawString(102, 28, Long.toString(this.tile.getNode().getEmcStored()), Color.GREEN.getRGB());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawBackground(guiLeft, guiTop, mouseX, mouseY);
    }
}
