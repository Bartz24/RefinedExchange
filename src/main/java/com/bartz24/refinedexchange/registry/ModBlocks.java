package com.bartz24.refinedexchange.registry;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.features.block.BlockEMCCrafter;
import com.bartz24.refinedexchange.features.block.BlockLiquifier;
import com.bartz24.refinedexchange.features.tile.TileLiquifier;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder("refinedstorage")
@Mod.EventBusSubscriber
public class ModBlocks {

    public static Fluid liquidEMC = new Fluid("liquidemc", new ResourceLocation(References.ModID, "blocks/liquidemc_still"), new ResourceLocation(References.ModID, "blocks/liquidemc_flowing"));

    @GameRegistry.ObjectHolder("liquifier")
    public static Block liquifier;
    @GameRegistry.ObjectHolder("emccrafter")
    public static Block emccrafter;


    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        FluidRegistry.addBucketForFluid(liquidEMC);
        event.getRegistry().register(new BlockFluidFinite(liquidEMC, Material.WATER).setRegistryName(new ResourceLocation(References.ModID, "liquidemc")));

        registerBlock(event, new BlockLiquifier());
        registerBlock(event, new BlockEMCCrafter());
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(ModBlocks.liquifier).setRegistryName(liquifier.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.emccrafter).setRegistryName(emccrafter.getRegistryName()));
    }

    private static void registerBlock(RegistryEvent.Register<Block> event, BlockBase block) {

        event.getRegistry().register(block);

        if (block.getInfo().hasTileEntity()) {
            registerTile(block.getInfo());
        }
    }

    private static void registerTile(IBlockInfo info) {
        Class<? extends TileBase> clazz = info.createTileEntity().getClass();

        GameRegistry.registerTileEntity(clazz, info.getId());

        try {
            TileBase tileInstance = clazz.newInstance();

            if (tileInstance instanceof TileNode) {
                API.instance().getNetworkNodeRegistry().add(((TileNode) tileInstance).getNodeId(), (tag, world, pos) -> {
                    NetworkNode node = ((TileNode) tileInstance).createNode(world, pos);

                    node.read(tag);

                    return node;
                });
            }

            tileInstance.getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
