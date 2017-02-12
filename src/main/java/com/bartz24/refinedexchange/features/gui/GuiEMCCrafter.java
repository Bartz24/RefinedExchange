package com.bartz24.refinedexchange.features.gui;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.features.tile.TileEMCCrafter;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonRedstoneMode;

public class GuiEMCCrafter extends GuiBase {

	TileEMCCrafter tile;

	public GuiEMCCrafter(ContainerEMCCrafter container, TileEMCCrafter tile) {
		super(container, 211, 137);
		this.tile = tile;
	}

	public void init(int x, int y) {
		addSideButton(new SideButtonRedstoneMode(this, TileEMCCrafter.REDSTONE_MODE));
	}

	public void update(int i, int j) {
	}

	public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafter.png");
		drawTexture(x, y, 0, 0, screenWidth, screenHeight);
	}

	public void drawForeground(int mouseX, int mouseY) {
		drawString(7, 7, t("gui.refinedexchange:emcCrafter", new Object[0]));
		drawString(7, 43, t("container.inventory", new Object[0]));
	}
}
