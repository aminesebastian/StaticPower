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
import theking530.staticpower.crops.ModPlants;
import theking530.staticpower.items.ModItems;
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
				Ingredient.fromItem(ModItems.InertInfusionBlend), Ingredient.fromItem(ModItems.EnergizedDust)});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumInfusionBlend", "StaticPower", new ItemStack(ModItems.LumumInfusionBlend, 2), new Ingredient[]{
				Ingredient.fromItem(ModItems.InertInfusionBlend), Ingredient.fromItem(ModItems.LumumDust)});
		
		//Wires ---------------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_copperWire", "StaticPower", new ItemStack(ModItems.CopperWire, 4), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.WireCutters, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotCopper")});
		RegisterHelper.addShapelessRecipe("StaticPower_silverWire", "StaticPower", new ItemStack(ModItems.SilverWire, 4), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.WireCutters, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotSilver")});
		RegisterHelper.addShapelessRecipe("StaticPower_goldWire", "StaticPower", new ItemStack(ModItems.GoldWire, 4), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.WireCutters, 1, OreDictionary.WILDCARD_VALUE)), Ingredient.fromItem(Items.GOLD_INGOT)});
		
		//Plates ---------------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_ironPlate", "StaticPower", new ItemStack(ModItems.IronPlate, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Ingredient.fromItem(Items.IRON_INGOT)});
		RegisterHelper.addShapelessRecipe("StaticPower_goldPlate", "StaticPower", new ItemStack(ModItems.GoldPlate, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Ingredient.fromItem(Items.GOLD_INGOT)});
		RegisterHelper.addShapelessRecipe("StaticPower_copperPlate", "StaticPower", new ItemStack(ModItems.CopperPlate, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotCopper")});
		RegisterHelper.addShapelessRecipe("StaticPower_tinPlate", "StaticPower", new ItemStack(ModItems.TinPlate, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotTin")});
		RegisterHelper.addShapelessRecipe("StaticPower_silverPlate", "StaticPower", new ItemStack(ModItems.SilverPlate, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotSilver")});
		RegisterHelper.addShapelessRecipe("StaticPower_leadPlate", "StaticPower", new ItemStack(ModItems.LeadPlate, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), new OreIngredient("ingotLead")});
		RegisterHelper.addShapelessRecipe("StaticPower_staticPlate", "StaticPower", new ItemStack(ModItems.StaticPlate, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Ingredient.fromItem(ModItems.StaticIngot)});
		RegisterHelper.addShapelessRecipe("StaticPower_energizedPlate", "StaticPower", new ItemStack(ModItems.EnergizedPlate, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Ingredient.fromItem(ModItems.EnergizedIngot)});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumPlate", "StaticPower", new ItemStack(ModItems.LumumPlate, 1), new Ingredient[]{
				Ingredient.fromStacks(new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE)), Ingredient.fromItem(ModItems.LumumIngot)});
		
		//Metal BLocks --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_staticIngot", "StaticPower", new ItemStack(ModItems.StaticIngot, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.StaticBlock))});
		RegisterHelper.addShapelessRecipe("StaticPower_energizedIngot", "StaticPower", new ItemStack(ModItems.EnergizedIngot, 9), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.EnergizedBlock))});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumIngot", "StaticPower", new ItemStack(ModItems.LumumIngot, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.LumumBlock))});
		RegisterHelper.addShapelessRecipe("StaticPower_copperIngot", "StaticPower", new ItemStack(ModItems.CopperIngot, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockCopper))});
		RegisterHelper.addShapelessRecipe("StaticPower_tinIngot", "StaticPower", new ItemStack(ModItems.TinIngot, 9), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockTin))});
		RegisterHelper.addShapelessRecipe("StaticPower_silverIngot", "StaticPower", new ItemStack(ModItems.SilverIngot, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockSilver))});
		RegisterHelper.addShapelessRecipe("StaticPower_leadIngot", "StaticPower", new ItemStack(ModItems.LeadIngot, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockLead))});
		RegisterHelper.addShapelessRecipe("StaticPower_platinumIngot", "StaticPower", new ItemStack(ModItems.PlatinumIngot, 9), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockPlatinum))});
		RegisterHelper.addShapelessRecipe("StaticPower_nickelIngot", "StaticPower", new ItemStack(ModItems.NickelIngot, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockNickel))});
		RegisterHelper.addShapelessRecipe("StaticPower_aluminiumIngot", "StaticPower", new ItemStack(ModItems.AluminiumIngot, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockAluminium))});
		RegisterHelper.addShapelessRecipe("StaticPower_sapphireGem", "StaticPower", new ItemStack(ModItems.SapphireGem, 9), new Ingredient[]{
				Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.BlockSapphire))});
		RegisterHelper.addShapelessRecipe("StaticPower_rubyGem", "StaticPower", new ItemStack(ModItems.RubyGem, 9), new Ingredient[]{
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
				Ingredient.fromItem(ModItems.IronPlate), Ingredient.fromItem(ModItems.BasicCircuit)});
		RegisterHelper.addShapelessRecipe("StaticPower_staticUpgradePlate", "StaticPower", new ItemStack(ModItems.StaticUpgradePlate), new Ingredient[]{
				Ingredient.fromItem(ModItems.StaticPlate), Ingredient.fromItem(ModItems.StaticCircuit)});
		RegisterHelper.addShapelessRecipe("StaticPower_energizedUpgradePlate", "StaticPower", new ItemStack(ModItems.EnergizedUpgradePlate), new Ingredient[]{
				Ingredient.fromItem(ModItems.EnergizedPlate), Ingredient.fromItem(ModItems.EnergizedCircuit)});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumUpgradePlate", "StaticPower", new ItemStack(ModItems.LumumUpgradePlate), new Ingredient[]{
				Ingredient.fromItem(ModItems.LumumPlate), Ingredient.fromItem(ModItems.LumumCircuit)});
		
		//Inert Infusion Blend --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_inertInfusionBlend", "StaticPower", new ItemStack(ModItems.InertInfusionBlend, 4), new Ingredient[]{
				new OreIngredient("dustTin"), new OreIngredient("dustTin"), new OreIngredient("dustSilver"), new OreIngredient("dustSilver")});
		
		//Nuggets --------------------------------------------------------------------------------------------------

		RegisterHelper.addShapelessRecipe("StaticPower_staticNugget", "StaticPower", new ItemStack(ModItems.StaticNugget, 9), new Ingredient[]{
				Ingredient.fromItem(ModItems.StaticIngot)});
		RegisterHelper.addShapelessRecipe("StaticPower_energizedNugget", "StaticPower", new ItemStack(ModItems.EnergizedNugget, 9), new Ingredient[]{
				Ingredient.fromItem(ModItems.EnergizedIngot)});
		RegisterHelper.addShapelessRecipe("StaticPower_lumumNugget", "StaticPower", new ItemStack(ModItems.LumumNugget, 9), new Ingredient[]{
				Ingredient.fromItem(ModItems.LumumIngot)});
		RegisterHelper.addShapelessRecipe("StaticPower_ironNugget", "StaticPower", new ItemStack(ModItems.IronNugget, 9), new Ingredient[]{
				Ingredient.fromItem(Items.IRON_INGOT)});
		//Static Planks --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_staticPlanks", "StaticPower", new ItemStack(Item.getItemFromBlock(ModBlocks.StaticPlanks), 4), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.StaticWood))});
		
		//Energized Planks --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_energizedPlanks", "StaticPower", new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedPlanks), 4), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.EnergizedWood))});
		
		//Lumum Planks --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_lumumPlanks", "StaticPower", new ItemStack(Item.getItemFromBlock(ModBlocks.LumumPlanks), 4), new Ingredient[]{
			Ingredient.fromItem(Item.getItemFromBlock(ModBlocks.LumumWood))});
		
		//Cannisters --------------------------------------------------------------------------------------------------
		RegisterHelper.addShapelessRecipe("StaticPower_empty_baseFluidCapsule", "StaticPower", new ItemStack(ModItems.BaseFluidCapsule, 1), new Ingredient[]{
				Ingredient.fromItem(ModItems.BaseFluidCapsule)});
		RegisterHelper.addShapelessRecipe("StaticPower_empty_staticFluidCapsule", "StaticPower", new ItemStack(ModItems.StaticFluidCapsule, 1), new Ingredient[]{
				Ingredient.fromItem(ModItems.StaticFluidCapsule)});
		RegisterHelper.addShapelessRecipe("StaticPower_empty_energizedFluidCapsule", "StaticPower", new ItemStack(ModItems.EnergizedFluidCapsule, 1), new Ingredient[]{
				Ingredient.fromItem(ModItems.EnergizedFluidCapsule)});
		RegisterHelper.addShapelessRecipe("StaticPower_empty_lumumFluidCapsule", "StaticPower", new ItemStack(ModItems.LumumFluidCapsule, 1), new Ingredient[]{
				Ingredient.fromItem(ModItems.LumumFluidCapsule)});
		
		//Filters --------------------------------------------------------------------------------------------------
		ItemStack quarryFilter = new ItemStack(ModItems.UpgradedItemFilter, 1);
		ItemFilter.writeQuarryFilter(quarryFilter);
		RegisterHelper.addShapelessRecipe("StaticPower_quarryFilter", "StaticPower", quarryFilter, new Ingredient[]{
				Ingredient.fromItem(ModItems.UpgradedItemFilter), Ingredient.fromItem(Item.getItemFromBlock(Blocks.COBBLESTONE))});
	}
	public static void registerFullRecipes() {
		registerShapelessRecipes();
	}
}
