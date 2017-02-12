package com.bartz24.refinedexchange.features.block;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.RefinedExchange;
import com.bartz24.refinedexchange.features.tile.TileEMCConverter;
import com.bartz24.refinedexchange.registry.ModCreativeTabs;
import com.bartz24.refinedexchange.registry.ModGuiHandler;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.TileNode;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockEMCConverter extends BlockContainer {

	public BlockEMCConverter() {
		super(Material.ROCK);
		this.setUnlocalizedName(References.ModID + "." + "emcConverter");
		this.setCreativeTab(ModCreativeTabs.tabMain);
		this.setHardness(8F);
		this.setResistance(8F);
		this.setRegistryName("emcConverter");
		this.isBlockContainer = true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEMCConverter();
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			if (player.getActiveItemStack() == null && player.isSneaking()) {
				TileEMCConverter converter = (TileEMCConverter) world.getTileEntity(pos);
				converter.setConvertUp(!converter.getConvertUp());
				player.sendMessage(new TextComponentString(
						"Set to convert Solidified EMC " + (converter.getConvertUp() ? "Up" : "Down") + "!"));
			} else
				player.openGui(RefinedExchange.instance, ModGuiHandler.EMCConverterGUI, world, pos.getX(), pos.getY(),
						pos.getZ());
		}
		return true;
	}

	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player,
			ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, player, stack);
		if (!world.isRemote) {
			EnumFacing aenumfacing[] = EnumFacing.VALUES;
			int i = aenumfacing.length;
			int j = 0;
			do {
				if (j >= i)
					break;
				EnumFacing facing = aenumfacing[j];
				TileEntity tile = world.getTileEntity(pos.offset(facing));
				if ((tile instanceof TileNode) && ((TileNode) tile).isConnected()) {
					((TileNode) tile).getNetwork().getNodeGraph().rebuild();
					break;
				}
				if (tile instanceof TileController) {
					((TileController) tile).getNodeGraph().rebuild();
					break;
				}
				j++;
			} while (true);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		INetworkMaster network = null;
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileNode)
				network = ((TileNode) tile).getNetwork();
		}
		super.breakBlock(world, pos, state);
		if (network != null)
			network.getNodeGraph().rebuild();
	}

}
