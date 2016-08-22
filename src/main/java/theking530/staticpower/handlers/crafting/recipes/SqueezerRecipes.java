package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.items.ModItems;

public class SqueezerRecipes {
	
	public static void registerSqueezingRecipes() {
		RegisterHelper.registerSqueezerRecipe(new ItemStack(ModItems.StaticCrop), new ItemStack(ModItems.DepletedCrop), new FluidStack(ModFluids.StaticFluid, 100));
		RegisterHelper.registerSqueezerRecipe(new ItemStack(ModItems.EnergizedCrop), new ItemStack(ModItems.DepletedCrop), new FluidStack(ModFluids.EnergizedFluid, 100));
		RegisterHelper.registerSqueezerRecipe(new ItemStack(ModItems.LumumCrop), new ItemStack(ModItems.DepletedCrop), new FluidStack(ModFluids.LumumFluid, 100));
		
		RegisterHelper.registerSqueezerRecipe(new ItemStack(ModItems.StaticSeeds), new ItemStack(Items.WHEAT_SEEDS), new FluidStack(ModFluids.StaticFluid, 25));
		RegisterHelper.registerSqueezerRecipe(new ItemStack(ModItems.EnergizedSeeds), new ItemStack(Items.WHEAT_SEEDS), new FluidStack(ModFluids.EnergizedFluid, 25));
		RegisterHelper.registerSqueezerRecipe(new ItemStack(ModItems.LumumSeeds), new ItemStack(Items.WHEAT_SEEDS), new FluidStack(ModFluids.LumumFluid, 25));
		
		RegisterHelper.registerSqueezerRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.StaticWood)), new ItemStack(Item.getItemFromBlock(Blocks.LOG)), new FluidStack(ModFluids.StaticFluid, 150));
		RegisterHelper.registerSqueezerRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedWood)), new ItemStack(Item.getItemFromBlock(Blocks.LOG)), new FluidStack(ModFluids.EnergizedFluid, 150));
		RegisterHelper.registerSqueezerRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.LumumWood)), new ItemStack(Item.getItemFromBlock(Blocks.LOG)), new FluidStack(ModFluids.LumumFluid, 150));
	}
}
