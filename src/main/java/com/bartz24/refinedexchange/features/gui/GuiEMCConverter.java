package com.bartz24.refinedexchange.features.gui;

import java.io.IOException;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.features.tile.TileEMCConverter;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButton;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonRedstoneMode;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;

public class GuiEMCConverter extends GuiBase {

	TileEMCConverter tile;

	public GuiEMCConverter(ContainerEMCConverter container, TileEMCConverter tile) {
		super(container, 211, 137);
		this.tile = tile;
	}

	public void init(int x, int y) {
		addSideButton(new SideButtonRedstoneMode(this, TileEMCConverter.REDSTONE_MODE));
	}

	public void update(int i, int j) {
	}

	public void drawBackground(int x, int y, int mouseX, int mouseY) {
		bindTexture(References.ModID, "gui/emccrafter.png");
		drawTexture(x, y, 0, 0, screenWidth, screenHeight);
	}

	public void drawForeground(int mouseX, int mouseY) {
		drawString(7, 7, t("gui.refinedexchange:emcConverter", new Object[0]));
		drawString(7, 24, TextFormatting.BLUE + "Converting " + (tile.getConvertUp() ? "Up":"Down"));
		drawString(95, 24, "Shift-R Click");
		drawString(85, 37, "block to change");
		drawString(7, 43, t("container.inventory", new Object[0]));
	}
}
