package com.bartz24.refinedexchange.registry;

import com.bartz24.refinedexchange.features.gui.ContainerEMCConverter;
import com.bartz24.refinedexchange.features.gui.ContainerEMCCrafter;
import com.bartz24.refinedexchange.features.gui.ContainerEMCSolidifier;
import com.bartz24.refinedexchange.features.gui.GuiEMCConverter;
import com.bartz24.refinedexchange.features.gui.GuiEMCCrafter;
import com.bartz24.refinedexchange.features.gui.GuiEMCSolidifier;
import com.bartz24.refinedexchange.features.tile.TileEMCConverter;
import com.bartz24.refinedexchange.features.tile.TileEMCCrafter;
import com.bartz24.refinedexchange.features.tile.TileEMCSolidifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {
	public static final int EMCCrafterGUI = 0;
	public static final int EMCConverterGUI = 1;
	public static final int EMCSolidifierGUI = 2;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == EMCCrafterGUI)
			return new ContainerEMCCrafter((TileEMCCrafter) world.getTileEntity(new BlockPos(x, y, z)), player);
		else if (id == EMCConverterGUI)
			return new ContainerEMCConverter((TileEMCConverter) world.getTileEntity(new BlockPos(x, y, z)), player);
		else if (id == EMCSolidifierGUI)
			return new ContainerEMCSolidifier((TileEMCSolidifier) world.getTileEntity(new BlockPos(x, y, z)), player);

		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == EMCCrafterGUI)
			return new GuiEMCCrafter(
					new ContainerEMCCrafter((TileEMCCrafter) world.getTileEntity(new BlockPos(x, y, z)), player),
					(TileEMCCrafter) world.getTileEntity(new BlockPos(x, y, z)));
		else if (id == EMCConverterGUI)
			return new GuiEMCConverter(
					new ContainerEMCConverter((TileEMCConverter) world.getTileEntity(new BlockPos(x, y, z)), player),
					(TileEMCConverter) world.getTileEntity(new BlockPos(x, y, z)));
		else if (id == EMCSolidifierGUI)
			return new GuiEMCSolidifier(
					new ContainerEMCSolidifier((TileEMCSolidifier) world.getTileEntity(new BlockPos(x, y, z)), player),
					(TileEMCSolidifier) world.getTileEntity(new BlockPos(x, y, z)));

		return null;
	}
}
