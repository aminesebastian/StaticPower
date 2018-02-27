package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.blocks.crops.ModPlants;
import theking530.staticpower.handlers.crafting.Craft;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.ItemMaterials;
import theking530.staticpower.items.itemfilter.ItemFilter;

public class ShaplessRecipes {

	@SuppressWarnings("all")
	private static void registerShapelessRecipes() {
		
		//Food --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_potatoFlour", "StaticPower", new ItemStack(ModItems.PotatoFlour), new Ingredient[]{
			Ingredient.fromItem(Items.POTATO)});
		RegisterHelper.addShapelessRecipe("StaticPower_wheatFlour", "StaticPower", new ItemStack(ModItems.WheatFlour), new Ingredient[]{
				Ingredient.fromItem(Items.WHEAT)});
		
		//Static Book --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_staticBook", "StaticPower", new ItemStack(ModItems.StaticBook), new Ingredient[]{
				Ingredient.fromItem(ModPlants.StaticSeeds), Ingredient.fromItem(Items.BOOK)});
		
		//Infusion Blends
		RegisterHelper.addShapelessRecipe("StaticPower_energizedInfusionBlend", "StaticPower", new ItemStack(ModItems.EnergizedInfusionBlend, 2), new Ingredient[]{
				Craft.itemstack(ItemMaterials.dustInertInfusion), Craft.itemstack(ItemMaterials.dustEnergized)});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumInfusionBlend", "StaticPower", new ItemStack(ModItems.LumumInfusionBlend, 2), new Ingredient[]{
				Craft.itemstack(ItemMaterials.dustInertInfusion), Craft.itemstack(ItemMaterials.dustLumum)});
		
		//Wires ---------------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_copperWire", "StaticPower", new ItemStack(ModItems.CopperWire, 4), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.WireCutters, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotCopper")});
		RegisterHelper.addShapelessRecipe("StaticPower_silverWire", "StaticPower", new ItemStack(ModItems.SilverWire, 4), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.WireCutters, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotSilver")});
		RegisterHelper.addShapelessRecipe("StaticPower_goldWire", "StaticPower", new ItemStack(ModItems.GoldWire, 4), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.WireCutters, 1, OreDictionary.WILDCARD_VALUE)), Ingredient.fromItem(Items.GOLD_INGOT)});
		
		//Plates ---------------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_ironPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateIron, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Ingredient.fromItem(Items.IRON_INGOT)});
		RegisterHelper.addShapelessRecipe("StaticPower_goldPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateGold, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Ingredient.fromItem(Items.GOLD_INGOT)});
		RegisterHelper.addShapelessRecipe("StaticPower_copperPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateCopper, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotCopper")});
		RegisterHelper.addShapelessRecipe("StaticPower_tinPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateTin, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotTin")});
		RegisterHelper.addShapelessRecipe("StaticPower_silverPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateSilver, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotSilver")});
		RegisterHelper.addShapelessRecipe("StaticPower_leadPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateLead, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotLead")});
		RegisterHelper.addShapelessRecipe("StaticPower_platinumPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.platePlatinum, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotPlatinum")});
		RegisterHelper.addShapelessRecipe("StaticPower_copperPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateAluminium, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotAluminium")});
		RegisterHelper.addShapelessRecipe("StaticPower_tinPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateNickel, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotTin")});
		RegisterHelper.addShapelessRecipe("StaticPower_silverPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateInertInfusion, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotInertInfusion")});
		RegisterHelper.addShapelessRecipe("StaticPower_leadPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateRedstoneAlloy, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotRedstoneAlloy")});
		
		RegisterHelper.addShapelessRecipe("StaticPower_staticPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateStatic, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Craft.itemstack(ItemMaterials.ingotStatic)});
		RegisterHelper.addShapelessRecipe("StaticPower_energizedPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateEnergized, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Craft.itemstack(ItemMaterials.ingotEnergized)});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumPlate", "StaticPower", Craft.outputItemStack(ItemMaterials.plateLumum, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Craft.itemstack(ItemMaterials.ingotLumum)});
		
		//Metal BLocks --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_staticIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotStatic, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.StaticBlock))});
		RegisterHelper.addShapelessRecipe("StaticPower_energizedIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotEnergized, 9), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.EnergizedBlock))});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotLumum, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.LumumBlock))});
		RegisterHelper.addShapelessRecipe("StaticPower_copperIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotCopper, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockCopper))});
		RegisterHelper.addShapelessRecipe("StaticPower_tinIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotTin, 9), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockTin))});
		RegisterHelper.addShapelessRecipe("StaticPower_silverIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotSilver, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockSilver))});
		RegisterHelper.addShapelessRecipe("StaticPower_leadIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotLead, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockLead))});
		RegisterHelper.addShapelessRecipe("StaticPower_platinumIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotPlatinum, 9), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockPlatinum))});
		RegisterHelper.addShapelessRecipe("StaticPower_nickelIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotNickel, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockNickel))});
		RegisterHelper.addShapelessRecipe("StaticPower_aluminiumIngotFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.ingotAluminium, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockAluminium))});
		RegisterHelper.addShapelessRecipe("StaticPower_sapphireGemFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.gemSapphire, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockSapphire))});
		RegisterHelper.addShapelessRecipe("StaticPower_rubyGemFromBlock", "StaticPower", Craft.outputItemStack(ItemMaterials.gemRuby, 9), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockRuby))});
		
		//Seeds --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_staticSeeds", "StaticPower", new ItemStack(ModPlants.StaticSeeds), new Ingredient[]{
			Ingredient.fromItem(ModPlants.StaticCrop)});
		RegisterHelper.addShapelessRecipe("StaticPower_energizedSeeds", "StaticPower", new ItemStack(ModPlants.EnergizedSeeds), new Ingredient[]{
			Ingredient.fromItem(ModPlants.EnergizedCrop)});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumSeeds", "StaticPower", new ItemStack(ModPlants.LumumSeeds), new Ingredient[]{
			Ingredient.fromItem(ModPlants.LumumCrop)});
		
		//Upgrade Plates ---------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_basicUpgradePlate", "StaticPower", new ItemStack(ModItems.BasicUpgradePlate), new Ingredient[]{
				Craft.itemstack(ItemMaterials.plateIron), Ingredient.fromItem(ModItems.BasicCircuit)});
		RegisterHelper.addShapelessRecipe("StaticPower_staticUpgradePlate", "StaticPower", new ItemStack(ModItems.StaticUpgradePlate), new Ingredient[]{
				Craft.itemstack(ItemMaterials.plateStatic), Ingredient.fromItem(ModItems.StaticCircuit)});
		RegisterHelper.addShapelessRecipe("StaticPower_energizedUpgradePlate", "StaticPower", new ItemStack(ModItems.EnergizedUpgradePlate), new Ingredient[]{
				Craft.itemstack(ItemMaterials.plateEnergized), Ingredient.fromItem(ModItems.EnergizedCircuit)});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumUpgradePlate", "StaticPower", new ItemStack(ModItems.LumumUpgradePlate), new Ingredient[]{
				Craft.itemstack(ItemMaterials.plateLumum), Ingredient.fromItem(ModItems.LumumCircuit)});
		
		//Inert Infusion Blend --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_inertInfusionBlend", "StaticPower", Craft.outputItemStack(ItemMaterials.dustInertInfusion, 4), new Ingredient[]{
				new OreIngredient("dustTin"), new OreIngredient("dustPlatinum"), new OreIngredient("dustSilver"), new OreIngredient("dustSilver")});
		
		//Nuggets --------------------------------------------------------------------------------------------------

		RegisterHelper.addShapelessRecipe("StaticPower_staticNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetStatic, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotStatic)});
		RegisterHelper.addShapelessRecipe("StaticPower_energizedNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetEnergized, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotEnergized)});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetLumum, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotLumum)});
		
		RegisterHelper.addShapelessRecipe("StaticPower_copperNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetCopper, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotCopper)});
		RegisterHelper.addShapelessRecipe("StaticPower_tinNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetTin, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotTin)});
		RegisterHelper.addShapelessRecipe("StaticPower_silverNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetSilver, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotSilver)});
		RegisterHelper.addShapelessRecipe("StaticPower_leadNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetLead, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotLead)});
		RegisterHelper.addShapelessRecipe("StaticPower_nickelNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetNickel, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotNickel)});
		RegisterHelper.addShapelessRecipe("StaticPower_platinumNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetPlatinum, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotPlatinum)});
		RegisterHelper.addShapelessRecipe("StaticPower_aluminiumNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetAluminium, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotAluminium)});
		RegisterHelper.addShapelessRecipe("StaticPower_redstoneAlloyNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetRedstoneAlloy, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotRedstoneAlloy)});
		RegisterHelper.addShapelessRecipe("StaticPower_inertInfusionNugget", "StaticPower", Craft.outputItemStack(ItemMaterials.nuggetInertInfusion, 9), new Ingredient[]{
				Craft.itemstack(ItemMaterials.ingotInertInfusion)});
		
		//Static Planks --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_staticPlanks", "StaticPower", new ItemStack(Item.getItemFromBlock(ModBlocks.StaticPlanks), 4), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.StaticWood))});
		
		//Energized Planks --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_energizedPlanks", "StaticPower", new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedPlanks), 4), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.EnergizedWood))});
		
		//Lumum Planks --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_lumumPlanks", "StaticPower", new ItemStack(Item.getItemFromBlock(ModBlocks.LumumPlanks), 4), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.LumumWood))});
		

		//Gunpowder
		RegisterHelper.addShapelessRecipe("StaticPower_gunpowder", "StaticPower", new ItemStack(Items.GUNPOWDER), new Ingredient[]{ 
			Craft.itemstack(ItemMaterials.dustSaltpeter), Craft.itemstack(ItemMaterials.dustSaltpeter), Craft.itemstack(ItemMaterials.dustCoal), Craft.itemstack(ItemMaterials.dustSulfur)});
		RegisterHelper.addShapelessRecipe("StaticPower_gunpowder", "StaticPower", new ItemStack(Items.GUNPOWDER), new Ingredient[]{ 
				Craft.itemstack(ItemMaterials.dustSaltpeter), Craft.itemstack(ItemMaterials.dustSaltpeter), Craft.itemstack(ItemMaterials.dustCharcoal), Craft.itemstack(ItemMaterials.dustSulfur)});
			
		//Filters --------------------------------------------------------------------------------------------------
		ItemStack quarryFilter = new ItemStack(ModItems.UpgradedItemFilter, 1);
		ItemFilter.writeQuarryFilter(quarryFilter);
		RegisterHelper.addShapelessRecipe("StaticPower_quarryFilter", "StaticPower", quarryFilter, new Ingredient[]{
				Ingredient.fromItem(ModItems.UpgradedItemFilter), Ingredient.fromItem(Item.getItemFromBlock(Blocks.COBBLESTONE))});
		
		ItemStack quarryFilter2 = new ItemStack(ModItems.UpgradedItemFilter, 1);
		ItemFilter.writeOreFilter(quarryFilter2);
		RegisterHelper.addShapelessRecipe("StaticPower_oreFilter", "StaticPower", quarryFilter2, new Ingredient[]{
				Ingredient.fromItem(ModItems.UpgradedItemFilter), Ingredient.fromItem(Item.getItemFromBlock(Blocks.IRON_ORE))});
		
		ItemStack quarryFilter3 = new ItemStack(ModItems.AdvancedItemFilter, 1);
		ItemFilter.writeAdvancedOreFilter(quarryFilter3);
		RegisterHelper.addShapelessRecipe("StaticPower_advancedOreFilter", "StaticPower", quarryFilter3, new Ingredient[]{
				Ingredient.fromItem(ModItems.AdvancedItemFilter), Ingredient.fromItem(Item.getItemFromBlock(Blocks.GOLD_ORE))});
	}
	public static void registerFullRecipes() {
		registerShapelessRecipes();
	}
}
