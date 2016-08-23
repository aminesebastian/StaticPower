package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import theking530.staticpower.blocks.ModBlocks;
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
		
		//Static Ingot --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.StaticIngot, 9), new Object[]{
			new ItemStack(ModBlocks.StaticBlock)});
		
		//Energized Ingot --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.EnergizedIngot, 9), new Object[]{
			new ItemStack(ModBlocks.EnergizedBlock)});
		
		//Static Seeds --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.StaticSeeds), new Object[]{
			new ItemStack(ModItems.StaticCrop)});
		
		//Energized Seeds --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.EnergizedSeeds), new Object[]{
			new ItemStack(ModItems.EnergizedCrop)});
		
		//Lumum Seeds --------------------------------------------------------------------------------------------------
		GameRegistry.addShapelessRecipe(new ItemStack(ModItems.LumumSeeds), new Object[]{
			new ItemStack(ModItems.LumumCrop)});
		
		//Inert Infusion Blend --------------------------------------------------------------------------------------------------
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.InertInfusionBlend, 4), new Object[]{
			"dustPlatinum", "dustTin", "dustTin", "dustSilver"}));
		
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
