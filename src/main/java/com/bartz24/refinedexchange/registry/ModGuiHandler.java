package com.bartz24.refinedexchange.registry;

import com.bartz24.refinedexchange.features.gui.ContainerEMCCrafter;
import com.bartz24.refinedexchange.features.gui.ContainerLiquifier;
import com.bartz24.refinedexchange.features.gui.GuiEMCCrafter;
import com.bartz24.refinedexchange.features.gui.GuiLiquifier;
import com.bartz24.refinedexchange.features.tile.TileEMCCrafter;
import com.bartz24.refinedexchange.features.tile.TileLiquifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {
    public static final int EMCLiquifier = 0;
    public static final int EMCCrafter = 1;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == EMCLiquifier)
            return new ContainerLiquifier((TileLiquifier) world.getTileEntity(new BlockPos(x, y, z)), player) {
            };
        else if (id == EMCCrafter)
            return new ContainerEMCCrafter((TileEMCCrafter) world.getTileEntity(new BlockPos(x, y, z)), player) {
            };

        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == EMCLiquifier)
            return new GuiLiquifier(
                    new ContainerLiquifier((TileLiquifier) world.getTileEntity(new BlockPos(x, y, z)), player));
        else if (id == EMCCrafter)
            return new GuiEMCCrafter(
                    new ContainerEMCCrafter((TileEMCCrafter) world.getTileEntity(new BlockPos(x, y, z)), player));

        return null;
    }
}
