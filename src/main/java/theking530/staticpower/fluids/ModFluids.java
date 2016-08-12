package theking530.staticpower.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.assists.Reference;

public class ModFluids {
	
	public static Item StaticBucket;
	public static Item EnergizedBucket;
	public static Item LumumBucket;
	
	public static Fluid StaticFluid;
	public static Fluid EnergizedFluid;
	public static Fluid LumumFluid;
	
	public static String StaticFluidName = "StaticFluid";
	public static String EnergizedFluidName = "EnergizedFluid";
	public static String LumumFluidName = "LumumFluid";
	
	public static Block BlockStaticFluid;
	public static Block BlockEnergizedFluid;
	public static Block BlockLumumFluid;

	
	public static void init() {
		StaticFluid = new Fluid(StaticFluidName, getStill(StaticFluidName), getFlowing(StaticFluidName)).setViscosity(1500).setDensity(1100).setLuminosity(15);
		FluidRegistry.registerFluid(StaticFluid);
		BlockStaticFluid = new BaseFluidBlock(StaticFluid, Material.WATER, "StaticFluid");;
		GameRegistry.register(BlockStaticFluid);
		
		EnergizedFluid = new Fluid(EnergizedFluidName, getStill(EnergizedFluidName), getFlowing(EnergizedFluidName)).setViscosity(3500).setDensity(1200).setLuminosity(15);
		FluidRegistry.registerFluid(EnergizedFluid);
		BlockEnergizedFluid = new BaseFluidBlock(EnergizedFluid, Material.WATER,"EnergizedFluid" );
		GameRegistry.register(BlockEnergizedFluid);
		
		LumumFluid = new Fluid(LumumFluidName, getStill(LumumFluidName), getFlowing(LumumFluidName)).setViscosity(3500).setDensity(1200).setLuminosity(15);
		FluidRegistry.registerFluid(LumumFluid);
		BlockLumumFluid = new BaseFluidBlock(LumumFluid, new MaterialLiquid(MapColor.YELLOW), "LumumFluid");
		GameRegistry.register(BlockLumumFluid);
		
		FluidRegistry.addBucketForFluid(StaticFluid);
		FluidRegistry.addBucketForFluid(EnergizedFluid);
		FluidRegistry.addBucketForFluid(LumumFluid);
	}
	
	public static ResourceLocation getStill(String fluidName) {
			return new ResourceLocation(Reference.MODID + ":blocks/fluids/" + fluidName + "Still");
	}
	public static ResourceLocation getFlowing(String fluidName) {
			return new ResourceLocation(Reference.MODID  + ":blocks/fluids/" + fluidName + "Flow");
	}
}
