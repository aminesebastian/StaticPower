package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.items.ModItems;

public class InfuserRecipes {

	public static void registerInfusionRecipe() {
		//StaticFluid
		oreDictionaryStack("logWood", Item.getItemFromBlock(ModBlocks.StaticWood), new FluidStack(ModFluids.StaticFluid, 150));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE)), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticCobblestone)), new FluidStack(ModFluids.StaticFluid, 50));	
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.ObsidianGlass)), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticLamp)), new FluidStack(ModFluids.StaticFluid, 100));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.StaticIngot), new FluidStack(ModFluids.StaticFluid, 250));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(Blocks.IRON_BLOCK)), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticBlock)), new FluidStack(ModFluids.StaticFluid, 2250));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.BUCKET), new ItemStack(ModFluids.StaticBucket), new FluidStack(ModFluids.StaticFluid, 1000));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.IRON_HELMET), new ItemStack(ModItems.StaticHelmet), new FluidStack(ModFluids.StaticFluid, 1250));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(ModItems.StaticChestplate), new FluidStack(ModFluids.StaticFluid, 2000));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.IRON_LEGGINGS), new ItemStack(ModItems.StaticLeggings), new FluidStack(ModFluids.StaticFluid, 1750));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.IRON_BOOTS), new ItemStack(ModItems.StaticBoots), new FluidStack(ModFluids.StaticFluid, 1000));
		oreDictionaryStack("dustIron", ModItems.StaticDust, new FluidStack(ModFluids.StaticFluid, 100));
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.StaticCrop), new ItemStack(ModItems.EnergizedCrop), new FluidStack(ModFluids.StaticFluid, 10000));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(Blocks.DIRT)), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticGrass)), new FluidStack(ModFluids.StaticFluid, 500));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(Blocks.CHEST)), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticChest)), new FluidStack(ModFluids.StaticFluid, 2000));
		
		//EnergizedFluid	
		oreDictionaryStack("logWood", Item.getItemFromBlock(ModBlocks.EnergizedWood), new FluidStack(ModFluids.EnergizedFluid, 150));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE)), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedCobblestone)), new FluidStack(ModFluids.EnergizedFluid, 50));	
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.ObsidianGlass)), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedLamp)), new FluidStack(ModFluids.EnergizedFluid, 100));
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.StaticIngot), new ItemStack(ModItems.EnergizedIngot), new FluidStack(ModFluids.EnergizedFluid, 250));		
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(Blocks.IRON_BLOCK)), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedBlock)), new FluidStack(ModFluids.EnergizedFluid, 2250));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.BUCKET), new ItemStack(ModFluids.EnergizedBucket), new FluidStack(ModFluids.EnergizedFluid, 1000));
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.StaticHelmet), new ItemStack(ModItems.EnergizedHelmet), new FluidStack(ModFluids.EnergizedFluid, 1250));
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.StaticChestplate), new ItemStack(ModItems.EnergizedChestplate), new FluidStack(ModFluids.EnergizedFluid, 2000));
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.StaticLeggings), new ItemStack(ModItems.EnergizedLeggings), new FluidStack(ModFluids.EnergizedFluid, 1750));
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.StaticBoots), new ItemStack(ModItems.EnergizedBoots), new FluidStack(ModFluids.EnergizedFluid, 1000));
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.StaticDust), new ItemStack(ModItems.EnergizedDust), new FluidStack(ModFluids.EnergizedFluid, 100));
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.EnergizedCrop), new ItemStack(ModItems.LumumCrop), new FluidStack(ModFluids.EnergizedFluid, 20000));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(Blocks.DIRT)), new ItemStack(ModBlocks.AdvancedEarth), new FluidStack(ModFluids.EnergizedFluid, 250));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.StaticSolarPanel)), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedSolarPanel)), new FluidStack(ModFluids.EnergizedFluid, 750));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.StaticChest)), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedChest)), new FluidStack(ModFluids.EnergizedFluid, 2000));
		
		//LumumFluid
		oreDictionaryStack("logWood", Item.getItemFromBlock(ModBlocks.LumumWood), new FluidStack(ModFluids.LumumFluid, 150));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE)), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumCobblestone)), new FluidStack(ModFluids.LumumFluid, 50));	
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.ObsidianGlass)), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumLamp)), new FluidStack(ModFluids.LumumFluid, 100));
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.EnergizedIngot), new ItemStack(ModItems.LumumIngot), new FluidStack(ModFluids.LumumFluid, 250));		
		RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.EnergizedDust), new ItemStack(ModItems.LumumDust), new FluidStack(ModFluids.LumumFluid, 100));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedSolarPanel)), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumSolarPanel)), new FluidStack(ModFluids.LumumFluid, 750));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedBlock)), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumBlock)), new FluidStack(ModFluids.LumumFluid, 2250));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedChest)), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumChest)), new FluidStack(ModFluids.LumumFluid, 2000));
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.BUCKET), new ItemStack(ModFluids.LumumBucket), new FluidStack(ModFluids.LumumFluid, 1000));
		
		//Water
		RegisterHelper.registerInfuserRecipe(new ItemStack(Item.getItemFromBlock(Blocks.SAND)), new ItemStack(Items.CLAY_BALL), new FluidStack(FluidRegistry.WATER, 250));	
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.BUCKET), new ItemStack(Items.WATER_BUCKET), new FluidStack(FluidRegistry.WATER, 1000));
		
		//Lava
		RegisterHelper.registerInfuserRecipe(new ItemStack(Items.BUCKET), new ItemStack(Items.LAVA_BUCKET), new FluidStack(FluidRegistry.LAVA, 1000));
		
		//EnderFluid
		//RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.InertInfusionBlend), OreDictionary.getOres("dustEnderium").get(0), fluidStack("ender", 250));
		//RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.InertIngot), OreDictionary.getOres("ingotEnderium").get(0), fluidStack("ender", 250));
		
		//Redstone
		//RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.InertInfusionBlend), new ItemStack(Items.redstone, 2), fluidStack("redstone", 100));
		
		//Glowstone
		//RegisterHelper.registerInfuserRecipe(new ItemStack(ModItems.InertInfusionBlend), new ItemStack(Items.glowstone_dust, 2), fluidStack("glowstone", 100));
	}
	public static void oreDictionaryStack(String ore, Item output, FluidStack fluidStack) {
		for(int index = 0; index < OreDictionary.getOres(ore).size(); index++) {
			ItemStack tempStack = OreDictionary.getOres(ore).get(index);
			RegisterHelper.registerInfuserRecipe(tempStack, new ItemStack(output), fluidStack);
		}
	}
}
