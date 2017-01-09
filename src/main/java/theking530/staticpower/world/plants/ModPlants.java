package theking530.staticpower.world.plants;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import theking530.staticpower.assists.RegisterHelper;

public class ModPlants {

	public static Block StaticCropPlant;
	public static Block EnergizedCropPlant;
	public static Block LumumCropPlant;
	public static Block TestDoublePlant;
	
	public static Item DepletedCrop;
	public static Item EnergizedSeeds;
	public static Item EnergizedCrop;
	public static Item StaticSeeds;
	public static Item StaticCrop;
	public static Item LumumSeeds;
	public static Item LumumCrop;
	
	public static void init() {		
		DepletedCrop = new DepletedCrop("DepletedCrop");
		RegisterHelper.registerItem(DepletedCrop);
		
		StaticCropPlant = new StaticPlant("StaticCropPlant");
		RegisterHelper.registerBlock(StaticCropPlant);	
		StaticSeeds = new StaticSeeds(StaticCropPlant, Blocks.FARMLAND).setUnlocalizedName("StaticSeeds");
		RegisterHelper.registerItem(StaticSeeds);	
		MinecraftForge.addGrassSeed(new ItemStack(StaticSeeds), 20);
		StaticCrop = new StaticCrop().setUnlocalizedName("StaticCrop");
		RegisterHelper.registerItem(StaticCrop);	
			
		EnergizedCropPlant = new EnergizedPlant("EnergizedCropPlant");
		RegisterHelper.registerBlock(EnergizedCropPlant);	
		EnergizedSeeds = new EnergizedSeeds(EnergizedCropPlant, Blocks.FARMLAND).setUnlocalizedName("EnergizedSeeds");
		RegisterHelper.registerItem(EnergizedSeeds);			
		EnergizedCrop = new EnergizedCrop().setUnlocalizedName("EnergizedCrop");
		RegisterHelper.registerItem(EnergizedCrop);	
	
		LumumCropPlant = new LumumPlant("LumumCropPlant");
		RegisterHelper.registerBlock(LumumCropPlant);
		LumumSeeds = new LumumSeeds(LumumCropPlant, Blocks.FARMLAND).setUnlocalizedName("LumumSeeds");
		RegisterHelper.registerItem(LumumSeeds);	
		LumumCrop = new LumumCrop().setUnlocalizedName("LumumCrop");
		RegisterHelper.registerItem(LumumCrop);		
		
		TestDoublePlant = new BaseDoublePlant("TestDoublePlant", DepletedCrop, DepletedCrop);
		RegisterHelper.registerBlock(TestDoublePlant);	

	}
}
