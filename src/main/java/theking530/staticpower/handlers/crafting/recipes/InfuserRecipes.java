package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.blocks.crops.ModPlants;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.crafting.Craft;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.ItemMaterials;
import theking530.staticpower.items.armor.ModArmor;

public class InfuserRecipes {

	public static void registerInfusionRecipes() {
		//StaticFluid
		RegisterHelper.registerInfuserRecipe(Craft.ore("logWood"), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticWood)), new FluidStack(ModFluids.StaticFluid, 150));
		RegisterHelper.registerInfuserRecipe(Craft.block(Blocks.COBBLESTONE), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticCobblestone)), new FluidStack(ModFluids.StaticFluid, 50));	
		RegisterHelper.registerInfuserRecipe(Craft.block(ModBlocks.ObsidianGlass), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticLamp)), new FluidStack(ModFluids.StaticFluid, 1000));
		RegisterHelper.registerInfuserRecipe(Craft.item(Items.IRON_INGOT), ItemMaterials.ingotStatic, new FluidStack(ModFluids.StaticFluid, 250));
		RegisterHelper.registerInfuserRecipe(Craft.ore("dustIron"),  Craft.outputItemStack(ItemMaterials.dustStatic), new FluidStack(ModFluids.StaticFluid, 250));
		RegisterHelper.registerInfuserRecipe(Craft.ore("plateIron"), Craft.outputItemStack(ItemMaterials.plateStatic), new FluidStack(ModFluids.StaticFluid, 250));
		RegisterHelper.registerInfuserRecipe(Craft.item(Item.getItemFromBlock(Blocks.IRON_BLOCK)), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticBlock)), new FluidStack(ModFluids.StaticFluid, 2250));
		RegisterHelper.registerInfuserRecipe(Craft.item(Items.IRON_HELMET), new ItemStack(ModArmor.StaticHelmet), new FluidStack(ModFluids.StaticFluid, 1250));
		RegisterHelper.registerInfuserRecipe(Craft.item(Items.IRON_CHESTPLATE), new ItemStack(ModArmor.StaticChestplate), new FluidStack(ModFluids.StaticFluid, 2000));
		RegisterHelper.registerInfuserRecipe(Craft.item(Items.IRON_LEGGINGS), new ItemStack(ModArmor.StaticLeggings), new FluidStack(ModFluids.StaticFluid, 1750));
		RegisterHelper.registerInfuserRecipe(Craft.item(Items.IRON_BOOTS), new ItemStack(ModArmor.StaticBoots), new FluidStack(ModFluids.StaticFluid, 1000));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModPlants.StaticCrop), new ItemStack(ModPlants.EnergizedCrop), new FluidStack(ModFluids.StaticFluid, 10000));
		RegisterHelper.registerInfuserRecipe(Craft.item(Item.getItemFromBlock(Blocks.DIRT)), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticGrass)), new FluidStack(ModFluids.StaticFluid, 500));
		RegisterHelper.registerInfuserRecipe(Craft.item(Item.getItemFromBlock(Blocks.CHEST)), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticChest)), new FluidStack(ModFluids.StaticFluid, 2000));
		RegisterHelper.registerInfuserRecipe(Craft.item(Item.getItemFromBlock(ModBlocks.BasicSolarPanel)), new ItemStack(Item.getItemFromBlock(ModBlocks.StaticSolarPanel)), new FluidStack(ModFluids.StaticFluid, 1000));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModItems.ApplePie), new ItemStack(ModItems.StaticPie), new FluidStack(ModFluids.StaticFluid, 50));
		
		//EnergizedFluid	
		RegisterHelper.registerInfuserRecipe(Craft.ore("logWood"), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedWood)), new FluidStack(ModFluids.EnergizedFluid, 150));
		RegisterHelper.registerInfuserRecipe(Craft.block(Blocks.COBBLESTONE), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedCobblestone)), new FluidStack(ModFluids.EnergizedFluid, 50));	
		RegisterHelper.registerInfuserRecipe(Craft.block(ModBlocks.ObsidianGlass), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedLamp)), new FluidStack(ModFluids.EnergizedFluid, 100));
		RegisterHelper.registerInfuserRecipe(Craft.itemstack(ItemMaterials.ingotStatic), Craft.outputItemStack(ItemMaterials.ingotEnergized), new FluidStack(ModFluids.EnergizedFluid, 250));		
		RegisterHelper.registerInfuserRecipe(Craft.itemstack(ItemMaterials.dustStatic), Craft.outputItemStack(ItemMaterials.dustEnergized), new FluidStack(ModFluids.EnergizedFluid, 250));
		RegisterHelper.registerInfuserRecipe(Craft.itemstack(ItemMaterials.plateStatic), Craft.outputItemStack(ItemMaterials.plateEnergized), new FluidStack(ModFluids.EnergizedFluid, 250));
		RegisterHelper.registerInfuserRecipe(Craft.item(Item.getItemFromBlock(Blocks.IRON_BLOCK)), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedBlock)), new FluidStack(ModFluids.EnergizedFluid, 2250));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModArmor.StaticHelmet), new ItemStack(ModArmor.EnergizedHelmet), new FluidStack(ModFluids.EnergizedFluid, 1250));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModArmor.StaticChestplate), new ItemStack(ModArmor.EnergizedChestplate), new FluidStack(ModFluids.EnergizedFluid, 2000));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModArmor.StaticLeggings), new ItemStack(ModArmor.EnergizedLeggings), new FluidStack(ModFluids.EnergizedFluid, 1750));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModArmor.StaticBoots), new ItemStack(ModArmor.EnergizedBoots), new FluidStack(ModFluids.EnergizedFluid, 1000));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModPlants.EnergizedCrop), new ItemStack(ModPlants.LumumCrop), new FluidStack(ModFluids.EnergizedFluid, 20000));	
		RegisterHelper.registerInfuserRecipe(Craft.item(Item.getItemFromBlock(ModBlocks.StaticSolarPanel)), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedSolarPanel)), new FluidStack(ModFluids.EnergizedFluid, 1000));
		RegisterHelper.registerInfuserRecipe(Craft.item(Item.getItemFromBlock(ModBlocks.StaticChest)), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedChest)), new FluidStack(ModFluids.EnergizedFluid, 2000));
		RegisterHelper.registerInfuserRecipe(Craft.item(Item.getItemFromBlock(Blocks.DIRT)), new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedGrass)), new FluidStack(ModFluids.EnergizedFluid, 500));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModItems.StaticPie), new ItemStack(ModItems.EnergizedPie), new FluidStack(ModFluids.EnergizedFluid, 50));
		
		//LumumFluid
		RegisterHelper.registerInfuserRecipe(Craft.ore("logWood"), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumWood)), new FluidStack(ModFluids.LumumFluid, 150));
		RegisterHelper.registerInfuserRecipe(Craft.block(Blocks.COBBLESTONE), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumCobblestone)), new FluidStack(ModFluids.LumumFluid, 50));	
		RegisterHelper.registerInfuserRecipe(Craft.block(ModBlocks.ObsidianGlass), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumLamp)), new FluidStack(ModFluids.LumumFluid, 1000));
		RegisterHelper.registerInfuserRecipe(Craft.itemstack(ItemMaterials.ingotEnergized), Craft.outputItemStack(ItemMaterials.ingotLumum), new FluidStack(ModFluids.LumumFluid, 250));		
		RegisterHelper.registerInfuserRecipe(Craft.itemstack(ItemMaterials.dustEnergized), Craft.outputItemStack(ItemMaterials.dustLumum), new FluidStack(ModFluids.LumumFluid, 250));
		RegisterHelper.registerInfuserRecipe(Craft.itemstack(ItemMaterials.plateEnergized), Craft.outputItemStack(ItemMaterials.plateLumum), new FluidStack(ModFluids.LumumFluid, 250));
		RegisterHelper.registerInfuserRecipe(Craft.block(ModBlocks.EnergizedSolarPanel), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumSolarPanel)), new FluidStack(ModFluids.LumumFluid, 1000));
		RegisterHelper.registerInfuserRecipe(Craft.block(ModBlocks.EnergizedBlock), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumBlock)), new FluidStack(ModFluids.LumumFluid, 2250));
		RegisterHelper.registerInfuserRecipe(Craft.block(ModBlocks.EnergizedChest), new ItemStack(Item.getItemFromBlock(ModBlocks.LumumChest)), new FluidStack(ModFluids.LumumFluid, 2000));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModArmor.EnergizedHelmet), new ItemStack(ModArmor.LumumHelmet), new FluidStack(ModFluids.LumumFluid, 1250));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModArmor.EnergizedChestplate), new ItemStack(ModArmor.LumumChestplate), new FluidStack(ModFluids.LumumFluid, 2000));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModArmor.EnergizedLeggings), new ItemStack(ModArmor.LumumLeggings), new FluidStack(ModFluids.LumumFluid, 1750));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModArmor.EnergizedBoots), new ItemStack(ModArmor.LumumBoots), new FluidStack(ModFluids.LumumFluid, 1000));
		RegisterHelper.registerInfuserRecipe(Craft.item(ModItems.EnergizedPie), new ItemStack(ModItems.LumumPie), new FluidStack(ModFluids.LumumFluid, 50));
		
		//Water
		RegisterHelper.registerInfuserRecipe(Craft.block(Blocks.GRAVEL), new ItemStack(Items.CLAY_BALL), new FluidStack(FluidRegistry.WATER, 1000));	
	}
}
