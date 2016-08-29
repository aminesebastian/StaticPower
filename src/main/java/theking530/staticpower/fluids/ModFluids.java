package theking530.staticpower.fluids;

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.assists.Reference;

public class ModFluids {
	
	public static Fluid StaticFluid;
	public static Fluid EnergizedFluid;
	public static Fluid LumumFluid;
	public static Fluid Steam;
	public static Fluid Ethanol;
	
	public static String StaticFluidName = "staticfluid";
	public static String EnergizedFluidName = "energizedfluid";
	public static String LumumFluidName = "lumumfluid";
	public static String EthanolName = "Ethanol";
	public static String SteamFluidName = "Steam";
	
	public static Block BlockStaticFluid;
	public static Block BlockEnergizedFluid;
	public static Block BlockLumumFluid;
	public static Block BlockSteamFluid;
	public static Block BlockEthanol;
	
	public static void init() {
		
		Fluid f  = new Fluid(StaticFluidName, getStill(StaticFluidName), getFlowing(StaticFluidName))
		        .setDensity(1500).setViscosity(3000);
	    FluidRegistry.registerFluid(f);
	    StaticFluid = FluidRegistry.getFluid(f.getName());
	    BlockStaticFluid = new BaseFluidBlock(StaticFluid, new MaterialLiquid(MapColor.EMERALD), StaticFluidName);
		GameRegistry.register(BlockStaticFluid);
	    registerBucket(StaticFluid);
	    registerFluidBlockRendering(StaticFluid, StaticFluidName);
	    
	    f  = new Fluid(EnergizedFluidName, getStill(EnergizedFluidName), getFlowing(EnergizedFluidName))
		        .setDensity(1500).setViscosity(3000);
	    FluidRegistry.registerFluid(f);
	    EnergizedFluid = FluidRegistry.getFluid(f.getName());
	    BlockEnergizedFluid = new BaseFluidBlock(EnergizedFluid, new MaterialLiquid(MapColor.DIAMOND), EnergizedFluidName);
		GameRegistry.register(BlockEnergizedFluid);
	    registerBucket(EnergizedFluid);
	    registerFluidBlockRendering(EnergizedFluid, EnergizedFluidName);
	    
	    f  = new Fluid(LumumFluidName, getStill(LumumFluidName), getFlowing(LumumFluidName))
		        .setDensity(1500).setViscosity(3000);
	    FluidRegistry.registerFluid(f);
	    LumumFluid = FluidRegistry.getFluid(f.getName());
	    BlockLumumFluid = new BaseFluidBlock(LumumFluid, new MaterialLiquid(MapColor.GOLD), LumumFluidName);
		GameRegistry.register(BlockLumumFluid);
	    registerBucket(LumumFluid);
	    registerFluidBlockRendering(LumumFluid, LumumFluidName);
	    
	    f  = new Fluid(SteamFluidName, getStill(SteamFluidName), getFlowing(SteamFluidName))
		        .setDensity(1500).setViscosity(3000).setGaseous(true);
	    FluidRegistry.registerFluid(f);
	    Steam = FluidRegistry.getFluid(f.getName());
	    BlockSteamFluid = new BaseFluidBlock(Steam, new MaterialLiquid(MapColor.QUARTZ), SteamFluidName);
		GameRegistry.register(BlockSteamFluid);
	    registerBucket(Steam);
	    registerFluidBlockRendering(Steam, SteamFluidName);
	    
	    f  = new Fluid(EthanolName, getStill(EthanolName), getFlowing(EthanolName))
		        .setDensity(150).setViscosity(1000);
	    FluidRegistry.registerFluid(f);
	    Ethanol = FluidRegistry.getFluid(f.getName());
	    BlockEthanol = new BaseFluidBlock(Ethanol, new MaterialLiquid(MapColor.ICE), EthanolName);
		GameRegistry.register(BlockEthanol);
	    registerBucket(Ethanol);
	    registerFluidBlockRendering(Ethanol, EthanolName);
		
	}
	
	public static ResourceLocation getStill(String fluidName) {
			return new ResourceLocation(Reference.MODID + ":blocks/" + fluidName + "Still");
	}
	public static ResourceLocation getFlowing(String fluidName) {
			return new ResourceLocation(Reference.MODID  + ":blocks/" + fluidName + "Flow");
	}
	private static void registerBucket(Fluid fluid) {
		FluidRegistry.addBucketForFluid(fluid);
	}
	@SideOnly(Side.CLIENT)
	public static void registerFluidBlockRendering(Fluid fluid, String name) {

	    FluidStateMapper mapper = new FluidStateMapper(fluid);
	    Block block = fluid.getBlock();
	    Item item = Item.getItemFromBlock(block);

	    // item-model
	    if (item != null) {
	      ModelLoader.registerItemVariants(item);
	      ModelLoader.setCustomMeshDefinition(item, mapper);
	    }
	    // block-model
	    if (block != null) {
	      ModelLoader.setCustomStateMapper(block, mapper);
	    }
	}
	 public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

		    public final Fluid fluid;
		    public final ModelResourceLocation location;

		    public FluidStateMapper(Fluid fluid) {
		      this.fluid = fluid;
		      location = new ModelResourceLocation(Reference.MODID + ":Fluid", fluid.getName());
		    }

		    @Override
		    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		      return location;
		    }

		    @Override
		    public ModelResourceLocation getModelLocation(ItemStack stack) {
		      return location;
		    }
		  }
}

