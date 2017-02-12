package com.bartz24.refinedexchange.features.gui;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.features.tile.TileEMCSolidifier;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonRedstoneMode;

import net.minecraft.util.text.TextFormatting;

public class GuiEMCSolidifier extends GuiBase {

	TileEMCSolidifier tile;

	public GuiEMCSolidifier(ContainerEMCSolidifier container, TileEMCSolidifier tile) {
		super(container, 211, 137);
		this.tile = tile;
	}

	public void init(int x, int y) {
		addSideButton(new SideButtonRedstoneMode(this, TileEMCSolidifier.REDSTONE_MODE));
	}

	public void update(int i, int j) {
	}

	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture(References.ModID, "gui/emcsolidifier.png");
		drawTexture(x, y, 0, 0, screenWidth, screenHeight);
	}

	public void drawForeground(int mouseX, int mouseY) {
		drawString(7, 7, t("gui.refinedexchange:emcSolidifier", new Object[0]));
		drawString(7, 24, TextFormatting.YELLOW + "EMC " + TextFormatting.DARK_GRAY + tile.getEmcStored());
		drawString(7, 43, t("container.inventory", new Object[0]));
	}
}
