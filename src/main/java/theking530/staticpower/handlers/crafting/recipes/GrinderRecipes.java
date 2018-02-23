package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.assists.utilities.OreDictionaryUtilities;
import theking530.staticpower.handlers.crafting.CraftHelpers;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper.GrinderOutput;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.ItemMaterials;

public class GrinderRecipes {

	public static void registerGrinderRecipes() {
		
		//Food
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItem(Items.WHEAT),  newOutput(new ItemStack(ModItems.WheatFlour, 2)),  newOutput(new ItemStack(ModItems.WheatFlour, 1), 0.15f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItem(Items.POTATO),  newOutput(new ItemStack(ModItems.PotatoFlour, 2)),  newOutput(new ItemStack(ModItems.PotatoFlour, 1), 0.15f));
		

		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreCopper"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustCopper, 2)), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustGold, 1), 0.05f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreSilver"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustSilver, 2)), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustTin, 1), 0.05f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreTin"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustTin, 2)), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustSilver, 1), 0.05f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreLead"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustLead, 2)), newOutput(CraftHelpers.outputItemStack(ItemMaterials.gemRuby, 1), 0.02f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("orePlatinum"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustPlatinum, 2)), newOutput(new ItemStack(Items.DIAMOND, 1), 0.01f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreNickel"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustNickel, 2)), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustPlatinum, 1), 0.01f));	
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreAluminium"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustAluminium, 2)), newOutput(CraftHelpers.outputItemStack(ItemMaterials.gemSapphire, 1), 0.02f));	
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreAluminum"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustAluminium, 2)), newOutput(CraftHelpers.outputItemStack(ItemMaterials.gemSapphire, 1), 0.02f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreIron"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustIron, 2)), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustNickel, 1), 0.05f));			
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreGold"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustGold, 2)), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustCopper, 1), 0.05f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreRuby"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustRuby, 3)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("oreSapphire"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustSapphire, 3)));

		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(ItemMaterials.ingotStatic),  newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustStatic, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(ItemMaterials.ingotEnergized),  newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustEnergized, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(ItemMaterials.ingotLumum),  newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustLumum, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(ItemMaterials.ingotInertInfusion),  newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustInertInfusion, 1)));		
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItem(Items.IRON_INGOT), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustIron, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItem(Items.GOLD_INGOT), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustGold, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(ItemMaterials.ingotRedstoneAlloy), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustRedstoneAlloy, 1)));
		
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("ingotCopper"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustCopper, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("ingotSilver"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustSilver, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("ingotTin"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustTin, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("ingotLead"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustLead, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("ingotPlatinum"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustPlatinum, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("ingotNickel"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustNickel, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("ingotAluminium"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustAluminium, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("ingotAluminum"), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustAluminium, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("gemRuby"), newOutput(ItemMaterials.dustRuby));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("gemSapphire"), newOutput(ItemMaterials.dustSapphire));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItem(Items.COAL), newOutput(ItemMaterials.dustCoal));	
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.OBSIDIAN), newOutput(CraftHelpers.outputItemStack(ItemMaterials.dustObsidian, 8), 1.0f));	
		
		oreDictionaryRecipe("QuartzBlack", 2);

		oreDictionaryRecipe("Manganese", 2);
		oreDictionaryRecipe("Mithril", 2);
		oreDictionaryRecipe("Zinc", 2);
		oreDictionaryRecipe("Adamantine", 2);
		oreDictionaryRecipe("Alduorite", 2);
		oreDictionaryRecipe("AstralSilver", 2);
		oreDictionaryRecipe("Atlarus", 2);
		oreDictionaryRecipe("Carmot", 2);
		oreDictionaryRecipe("Ceruclase", 2);
		oreDictionaryRecipe("DeepIron", 2);
		oreDictionaryRecipe("Eximite", 2);
		oreDictionaryRecipe("Ignatius", 2);
		oreDictionaryRecipe("Infuscolium", 2);
		oreDictionaryRecipe("Kalendrite", 2);
		oreDictionaryRecipe("Lemurite", 2);
		oreDictionaryRecipe("Magnesium", 2);
		oreDictionaryRecipe("Meutoite", 2);
		oreDictionaryRecipe("Midasium", 2);
		oreDictionaryRecipe("Orichalcum", 2);
		oreDictionaryRecipe("Oureclase", 2);
		oreDictionaryRecipe("Prometheum", 2);
		oreDictionaryRecipe("Rubracium", 2);
		oreDictionaryRecipe("Sanguinite", 2);
		oreDictionaryRecipe("ShadowIron", 2);
		oreDictionaryRecipe("Vulcanite", 2);
		oreDictionaryRecipe("Vyroxeres", 2);
		
		ingotDictionaryRecipe("Bronze", 1);
		
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.COAL_ORE),  newOutput(new ItemStack(Items.COAL, 4)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.REDSTONE_ORE),  newOutput(new ItemStack(Items.REDSTONE, 5)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.DIAMOND_ORE),  newOutput(new ItemStack(Items.DIAMOND, 3)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.EMERALD_ORE),  newOutput(new ItemStack(Items.EMERALD, 3)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.QUARTZ_ORE),  newOutput(new ItemStack(Items.QUARTZ, 4)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.LAPIS_ORE),  newOutput(new ItemStack(Items.DYE, 4, 4)));
		

		for (int meta = 1; meta < 16; meta++) {
			RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.WOOL, 1, meta)), newOutput(new ItemStack(Items.STRING, 4)), newOutput(new ItemStack(Blocks.WOOL), 0.01f), newOutput(new ItemStack(Items.DYE, 1, 15 - meta), 0.5f));
		}
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.WOOL, 0)), newOutput(new ItemStack(Items.STRING, 4)), newOutput(new ItemStack(Blocks.WOOL), 0.01f));
		
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.YELLOW_FLOWER)), newOutput(new ItemStack(Items.DYE, 4, 11)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_FLOWER, 1, 0)), newOutput(new ItemStack(Items.DYE, 4, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_FLOWER, 1, 1)), newOutput(new ItemStack(Items.DYE, 4, 12)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_FLOWER, 1, 2)), newOutput(new ItemStack(Items.DYE, 4, 13)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_FLOWER, 1, 3)), newOutput(new ItemStack(Items.DYE, 4, 7)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_FLOWER, 1, 4)), newOutput(new ItemStack(Items.DYE, 4, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_FLOWER, 1, 5)), newOutput(new ItemStack(Items.DYE, 4, 14)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_FLOWER, 1, 6)), newOutput(new ItemStack(Items.DYE, 4, 7)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_FLOWER, 1, 7)), newOutput(new ItemStack(Items.DYE, 4, 9)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_FLOWER, 1, 8)), newOutput(new ItemStack(Items.DYE, 4, 7)));

		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.DOUBLE_PLANT, 1, 0)), newOutput(new ItemStack(Items.DYE, 4, 11)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.DOUBLE_PLANT, 1, 1)), newOutput(new ItemStack(Items.DYE, 4, 13)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.DOUBLE_PLANT, 1, 4)), newOutput(new ItemStack(Items.DYE, 4, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.DOUBLE_PLANT, 1, 5)), newOutput(new ItemStack(Items.DYE, 4, 9)));
		
		//Items
		oreDictionaryRecipe("Sulfur", 4);
		oreDictionaryRecipe("Saltpeter", 4);
		oreDictionaryRecipe("Potash", 4);
		oreDictionaryRecipe("Bitumen", 4);
		oreDictionaryRecipe("Salt", 4);
		

		oreDictionaryRecipe("ingotSteel", "dustSteel", 1);
		oreDictionaryRecipe("ingotBronze", "dustBronze", 1);
		oreDictionaryRecipe("ingotElectrum", "dustElectrum", 1);
		oreDictionaryRecipe("ingotInvar", "dustInvar", 1);
		oreDictionaryRecipe("ingotUranium", "dustUranium", 1);
		
		oreDictionaryRecipe("orePeridot", "gemPeridot", 3);
		oreDictionaryRecipe("gemPeridot", "dustPeridot", 1);

		//Utils
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.GRAVEL), newOutput(new ItemStack(Items.FLINT, 1)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.COBBLESTONE), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.SAND))), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.GRAVEL)), 0.25f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.COBBLESTONE_WALL), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.SAND))), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.GRAVEL)), 0.25f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.GLASS), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.SAND))));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.STONE), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE))));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.STAINED_HARDENED_CLAY), newOutput(new ItemStack(Items.CLAY_BALL, 3)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.HARDENED_CLAY), newOutput(new ItemStack(Items.CLAY_BALL, 3)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.BRICK_BLOCK), newOutput(new ItemStack(Items.BRICK, 3)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.SAND),  newOutput(new ItemStack(ModItems.Silicon)),  newOutput(new ItemStack(ModItems.Silicon), 0.1f));
		
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItem(Items.BONE), newOutput(new ItemStack(Items.DYE, 6, 15)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItem(Items.PRISMARINE_SHARD), newOutput(new ItemStack(Items.PRISMARINE_CRYSTALS)));
		
		/*Thanks to CoFH team for pointers on good items to add here! :) */
		
		for (int i = 0; i < 15; i++) {
			RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.STAINED_GLASS, 1, i)), newOutput(new ItemStack(Blocks.SAND)), newOutput(new ItemStack(Items.DYE, 1, 15 - i), 0.1f));
		}
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.REDSTONE_LAMP), newOutput(new ItemStack(Items.REDSTONE, 4)), newOutput(new ItemStack(Items.GLOWSTONE_DUST, 2), 0.5f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.BRICK_BLOCK), newOutput(new ItemStack(Items.BRICK, 4)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.NETHER_BRICK), newOutput(new ItemStack(Items.NETHERBRICK, 4)));

		for (int i = 0; i < 3; i++) {
			RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.QUARTZ_BLOCK, 1, i)), newOutput(new ItemStack(Items.QUARTZ, 4)));
			RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.SANDSTONE, 1, i)), newOutput(new ItemStack(Blocks.SAND, 4)));
			RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.RED_SANDSTONE, 1, i)), newOutput(new ItemStack(Blocks.SAND, 4, 1)));
		}

		/* STAIRS */
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.STONE_SLAB, 1, 4)), newOutput(new ItemStack(Items.BRICK, 2)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.STONE_SLAB, 1, 6)), newOutput(new ItemStack(Items.NETHERBRICK, 2)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.STONE_SLAB, 1, 7)), newOutput(new ItemStack(Items.QUARTZ, 2)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.STONE_SLAB, 1, 1)), newOutput(new ItemStack(Blocks.SAND)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItemstack(new ItemStack(Blocks.STONE_SLAB2, 1, 0)), newOutput(new ItemStack(Blocks.SAND, 1, 1)));
		
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.BRICK_STAIRS), newOutput(new ItemStack(Items.BRICK, 3)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.NETHER_BRICK_STAIRS), newOutput(new ItemStack(Items.NETHERBRICK, 3)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.QUARTZ_STAIRS), newOutput(new ItemStack(Items.QUARTZ, 3)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.SANDSTONE_STAIRS), newOutput(new ItemStack(Blocks.SAND, 2)));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.RED_SANDSTONE_STAIRS), newOutput(new ItemStack(Blocks.SAND, 2, 1)));

		/* MISC */
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItem(Items.FLOWER_POT), newOutput(new ItemStack(Items.BRICK, 2)), newOutput(new ItemStack(Items.BRICK, 1), 0.5f));
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromItem(Items.GLASS_BOTTLE), newOutput(new ItemStack(Blocks.SAND)));		
		RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientFromBlock(Blocks.STONEBRICK), newOutput(new ItemStack(Blocks.STONEBRICK, 1, 2)));
	}
	public static void oreDictionaryRecipe(String input, String output, int number) {
		if(OreDictionary.getOres("input").size() > 0) {
			for(int index = 0; index < OreDictionary.getOres("input").size(); index++) {
				RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre("input"), newOutput(new ItemStack(OreDictionary.getOres("output").get(0).getItem(), number)));
			}	
		}
	}
	public static void oreDictionaryRecipe(String metal, int number) {
		String ore = "ore";
		String dust = "dust";
		if(OreDictionary.getOres(dust+metal).size() > 0) {
			for(int index = 0; index < OreDictionary.getOres(ore+metal).size(); index++) {
				ItemStack oreDustStack = OreDictionaryUtilities.getOreStack(dust+metal, 0);
				oreDustStack.setCount(number);
				RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre(ore+metal), newOutput(oreDustStack));
			}	
		}
	}

	public static void ingotDictionaryRecipe(String metal, int number) {
		String ingot = "ingot";
		String dust = "dust";
		if(OreDictionary.getOres(dust+metal).size() > 0) {
			for(int index = 0; index < OreDictionary.getOres(ingot+metal).size(); index++) {
				ItemStack ingotDustStack = OreDictionaryUtilities.getOreStack(dust+metal, 0);
				ingotDustStack.setCount(number);
				RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre(ingot+metal), newOutput(ingotDustStack));
			}
		}
	}
	public static void baseDictionaryRecipe(String input, String output, int number) {
		if(OreDictionary.getOres(output).size() > 0) {
			for(int index = 0; index < OreDictionary.getOres(input).size(); index++) {
				ItemStack result = OreDictionaryUtilities.getOreStack(output, 0);
				result.setCount(number);
				RegisterHelper.registerGrinderRecipe(CraftHelpers.ingredientOre(input), newOutput(result));
			}
		}
	}
	public static GrinderOutput newOutput(ItemStack itemstack, float percentage) {
		GrinderOutput tempOutput = new GrinderOutput(itemstack, percentage);
		return tempOutput;
	}
	public static GrinderOutput newOutput(ItemStack itemstack) {
		GrinderOutput tempOutput = new GrinderOutput(itemstack, 1.0f);
		return tempOutput;
	}
}
