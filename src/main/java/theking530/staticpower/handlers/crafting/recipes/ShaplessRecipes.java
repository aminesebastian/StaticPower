package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.ItemRenderRegistry;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.itemfilter.ItemFilter;

public class ShaplessRecipes {

	@SuppressWarnings("all")
	private static void registerShapelessRecipes() {
		
		//Static Book --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.StaticBook), new Object[]{
				new ItemStack(ModItems.StaticSeeds), new ItemStack(Items.BOOK)});
		
		//Infusion Blends
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.EnergizedInfusionBlend), new Object[]{
				new ItemStack(ModItems.InertInfusionBlend), new ItemStack(ModItems.EnergizedDust)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.LumumInfusionBlend), new Object[]{
				new ItemStack(ModItems.InertInfusionBlend), new ItemStack(ModItems.LumumDust)});
		
		//Coils ---------------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.CopperWire, 8), new Object[]{
				new ItemStack(ModItems.WireCutters, 1, OreDictionary.WILDCARD_VALUE), "ingotCopper"}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SilverWire, 8), new Object[]{
				new ItemStack(ModItems.WireCutters, 1, OreDictionary.WILDCARD_VALUE), "ingotSilver"}));
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.GoldWire, 8), new Object[]{
				new ItemStack(ModItems.WireCutters, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.GOLD_INGOT)});
		
		//Plates ---------------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.IronPlate, 1), new Object[]{
				new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.IRON_INGOT)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.GoldPlate, 1), new Object[]{
				new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.GOLD_INGOT)});
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.CopperPlate, 1), new Object[]{
				new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE), "ingotCopper"}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.TinPlate, 1), new Object[]{
				new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE), "ingotTin"}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.SilverPlate, 1), new Object[]{
				new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE), "ingotSilver"}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.LeadPlate, 1), new Object[]{
				new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE), "ingotLead"}));
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.StaticPlate, 1), new Object[]{
				new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(ModItems.StaticIngot)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.EnergizedPlate, 1), new Object[]{
				new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(ModItems.EnergizedIngot)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.LumumPlate, 1), new Object[]{
				new ItemStack(ModItems.MetalHammer, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(ModItems.LumumIngot)});
		
		//Metal BLocks --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.StaticIngot, 9), new Object[]{
				new ItemStack(ModBlocks.StaticBlock)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.EnergizedIngot, 9), new Object[]{
			new ItemStack(ModBlocks.EnergizedBlock)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.LumumIngot, 9), new Object[]{
				new ItemStack(ModBlocks.LumumBlock)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.CopperIngot, 9), new Object[]{
				new ItemStack(ModBlocks.BlockCopper)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.TinIngot, 9), new Object[]{
			new ItemStack(ModBlocks.BlockTin)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.SilverIngot, 9), new Object[]{
				new ItemStack(ModBlocks.BlockSilver)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.LeadIngot, 9), new Object[]{
				new ItemStack(ModBlocks.BlockLead)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.PlatinumIngot, 9), new Object[]{
			new ItemStack(ModBlocks.BlockPlatinum)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.NickelIngot, 9), new Object[]{
				new ItemStack(ModBlocks.BlockNickel)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.AluminiumIngot, 9), new Object[]{
				new ItemStack(ModBlocks.BlockAluminium)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.SapphireGem, 9), new Object[]{
				new ItemStack(ModBlocks.BlockSapphire)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.RubyGem, 9), new Object[]{
			new ItemStack(ModBlocks.BlockRuby)});
		
		//Seeds --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.StaticSeeds), new Object[]{
			new ItemStack(ModItems.StaticCrop)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.EnergizedSeeds), new Object[]{
			new ItemStack(ModItems.EnergizedCrop)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.LumumSeeds), new Object[]{
			new ItemStack(ModItems.LumumCrop)});
		
		//Upgrade Plates ---------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.BasicUpgradePlate), new Object[]{
				new ItemStack(ModItems.IronPlate), new ItemStack(ModItems.BasicCircuit)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.StaticUpgradePlate), new Object[]{
				new ItemStack(ModItems.StaticPlate), new ItemStack(ModItems.StaticCircuit)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.EnergizedUpgradePlate), new Object[]{
				new ItemStack(ModItems.EnergizedPlate), new ItemStack(ModItems.EnergizedCircuit)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.LumumUpgradePlate), new Object[]{
				new ItemStack(ModItems.LumumPlate), new ItemStack(ModItems.LumumCircuit)});
		
		//Inert Infusion Blend --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.InertInfusionBlend, 4), new Object[]{
			 "dustTin", "dustTin", "dustSilver", "dustSilver"}));
		
		//Nuggets --------------------------------------------------------------------------------------------------
		addOreDictRecipe("nuggetCopper", 9, "ingotCopper");
		addOreDictRecipe("nuggetTin", 9, "ingotTin");
		addOreDictRecipe("nuggetSilver", 9, "ingotSilver");
		addOreDictRecipe("nuggetLead", 9, "ingotLead");
		addOreDictRecipe("nuggetPlatinum", 9, "ingotPlatinum");
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.StaticNugget, 9), new Object[]{
				new ItemStack(ModItems.StaticIngot)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.EnergizedNugget, 9), new Object[]{
				new ItemStack(ModItems.EnergizedIngot)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.LumumNugget, 9), new Object[]{
				new ItemStack(ModItems.LumumIngot)});
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.IronNugget, 9), new Object[]{
				new ItemStack(Items.IRON_INGOT)});
		//Static Planks --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.StaticPlanks), 4), new Object[]{
			new ItemStack(Item.getItemFromBlock(ModBlocks.StaticWood))});
		
		//Energized Planks --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedPlanks), 4), new Object[]{
			new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedWood))});
		
		//Lumum Planks --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(ModBlocks.LumumPlanks), 4), new Object[]{
			new ItemStack(Item.getItemFromBlock(ModBlocks.LumumWood))});
		
		//Filters
		ItemStack quarryFilter = new ItemStack(ModItems.UpgradedItemFilter);
		ItemFilter.writeQuarryFilter(quarryFilter);
		GameRegistry.addShapelessRecipe(quarryFilter, new Object[]{
				new ItemStack(ModItems.UpgradedItemFilter), new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE))});
	}
	public static void addOreDictRecipe(String input, int count, String output) {
		if(OreDictionary.getOres("input").size() > 0) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(OreDictionary.getOres("output").get(0).getItem(), count), new Object[]{"input"}));
		}		
	}
	public static void registerFullRecipes() {
		registerShapelessRecipes();
	}
}
