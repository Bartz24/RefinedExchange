package com.bartz24.refinedexchange.features.block;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.RefinedExchange;
import com.bartz24.refinedexchange.features.tile.TileEMCConverter;
import com.bartz24.refinedexchange.features.tile.TileEMCSolidifier;
import com.bartz24.refinedexchange.registry.ModCreativeTabs;
import com.bartz24.refinedexchange.registry.ModGuiHandler;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.TileNode;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class BlockEMCSolidifier extends BlockContainer {

	public BlockEMCSolidifier() {
		super(Material.ROCK);
		this.setUnlocalizedName(References.ModID + "." + "emcSolidifier");
		this.setCreativeTab(ModCreativeTabs.tabMain);
		this.setHardness(8F);
		this.setResistance(8F);
		this.setRegistryName("emcSolidifier");
		this.isBlockContainer = true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEMCSolidifier();
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
				player.openGui(RefinedExchange.instance, ModGuiHandler.EMCSolidifierGUI, world, pos.getX(), pos.getY(),
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
			if ((tile instanceof TileBase) && ((TileBase) tile).getDrops() != null) {
				IItemHandler handler = ((TileBase) tile).getDrops();
				for (int i = 0; i < handler.getSlots(); i++)
					if (handler.getStackInSlot(i) != null)
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(),
								handler.getStackInSlot(i));

			}
		}
		super.breakBlock(world, pos, state);
		if (network != null)
			network.getNodeGraph().rebuild();
	}

}
