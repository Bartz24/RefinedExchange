package com.bartz24.refinedexchange.proxy;

import com.bartz24.refinedexchange.References;
import com.bartz24.refinedexchange.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        OBJLoader.INSTANCE.addDomain(References.ModID);


    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        mapFluidState(ModBlocks.liquidEMC);
        registerItemRenderer(Item.getItemFromBlock(ModBlocks.liquifier));
        registerItemRenderer(Item.getItemFromBlock(ModBlocks.emccrafter));
    }

    public static void mapFluidState(Fluid fluid) {
        Block block = fluid.getBlock();
        Item item = Item.getItemFromBlock(block);
        FluidStateMapper mapper = new FluidStateMapper(fluid);
        if (item != null) {
            ModelLoader.registerItemVariants(item);
            ModelLoader.setCustomMeshDefinition(item, mapper);
        }
        ModelLoader.setCustomStateMapper(block, mapper);
    }

    static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {
        public final ModelResourceLocation location;

        public FluidStateMapper(Fluid fluid) {
            this.location = new ModelResourceLocation(References.ModID + ":fluid_block", fluid.getName());
        }

        @Nonnull
        @Override
        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            return location;
        }

        @Nonnull
        @Override
        public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            return location;
        }
    }

    public static void registerItemRenderer(Item item, int meta, ResourceLocation name) {
        ModelBakery.registerItemVariants(item, name);
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, "inventory"));
    }

    public static void registerItemRenderer(Item item, int meta) {
        registerItemRenderer(item, meta, new ResourceLocation(item.getRegistryName().toString() + meta));
    }

    public static void registerItemRenderer(Item item, int meta, boolean global) {
        if (!global)
            registerItemRenderer(item, meta);
        else
            registerItemRenderer(item, meta, item.getRegistryName());
    }

    public static void registerItemRenderer(Item item, ResourceLocation name) {
        registerItemRenderer(item, 0, name);
    }

    public static void registerItemRenderer(Item item) {
        registerItemRenderer(item, item.getRegistryName());
    }
}
