package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.assists.utilities.OreDictionaryUtilities;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper.GrinderOutput;
import theking530.staticpower.items.ModItems;

public class GrinderRecipes {

	public static void registerGrinderRecipes() {
		
		//Food
		RegisterHelper.registerGrinderRecipe(new ItemStack(Items.WHEAT),  newOutput(new ItemStack(ModItems.WheatFlour, 2), 1.0f),  newOutput(new ItemStack(ModItems.WheatFlour, 2), 0.25f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Items.POTATO),  newOutput(new ItemStack(ModItems.PotatoFlour, 2), 1.0f),  newOutput(new ItemStack(ModItems.PotatoFlour, 2), 0.25f));
		
		for(int index = 0; index < OreDictionary.getOres("oreCopper").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("oreCopper").get(index), newOutput(new ItemStack(ModItems.CopperDust, 2), 1.0f), newOutput(new ItemStack(ModItems.GoldDust, 1), 0.1f));
		}
		for(int index = 0; index < OreDictionary.getOres("oreSilver").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("oreSilver").get(index), newOutput(new ItemStack(ModItems.SilverDust, 2), 1.0f), newOutput(new ItemStack(ModItems.TinDust, 1), 0.1f));
		}
		for(int index = 0; index < OreDictionary.getOres("oreTin").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("oreTin").get(index), newOutput(new ItemStack(ModItems.TinDust, 2), 1.0f), newOutput(new ItemStack(ModItems.SilverDust, 1), 0.1f));
		}
		for(int index = 0; index < OreDictionary.getOres("oreLead").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("oreLead").get(index), newOutput(new ItemStack(ModItems.LeadDust, 2), 1.0f), newOutput(new ItemStack(ModItems.RubyGem, 1), 0.02f));
		}
		for(int index = 0; index < OreDictionary.getOres("orePlatinum").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("orePlatinum").get(index), newOutput(new ItemStack(ModItems.PlatinumDust, 2), 1.0f), newOutput(new ItemStack(Items.DIAMOND, 1), 0.02f));
		}
		for(int index = 0; index < OreDictionary.getOres("oreNickel").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("oreNickel").get(index), newOutput(new ItemStack(ModItems.NickelDust, 2), 1.0f), newOutput(new ItemStack(ModItems.PlatinumDust, 1), 0.1f));
		}
		for(int index = 0; index < OreDictionary.getOres("oreAluminium").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("oreAluminium").get(index), newOutput(new ItemStack(ModItems.AluminiumDust, 2), 1.0f), newOutput(new ItemStack(ModItems.SapphireGem, 1), 0.02f));
		}
		for(int index = 0; index < OreDictionary.getOres("oreBauxite").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("oreBauxite").get(index), newOutput(new ItemStack(ModItems.AluminiumDust, 2), 1.0f), newOutput(new ItemStack(ModItems.SapphireGem, 1), 0.02f));
		}
		
		RegisterHelper.registerGrinderRecipe(new ItemStack(ModItems.StaticIngot),  newOutput(new ItemStack(ModItems.StaticDust), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(ModItems.EnergizedIngot),  newOutput(new ItemStack(ModItems.EnergizedDust), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(ModItems.LumumIngot),  newOutput(new ItemStack(ModItems.LumumDust), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(ModItems.InertIngot),  newOutput(new ItemStack(ModItems.InertInfusionBlend), 1.0f));
		
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.SAND)),  newOutput(new ItemStack(ModItems.Silicon), 1.0f),  newOutput(new ItemStack(ModItems.Silicon), 0.1f));
		
		oreDictionaryRecipe("QuartzBlack", 2);
		ingotDictionaryRecipe("QuartzBlack", 1);
		
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
		oreDictionaryOutput(new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN)), "dustObsidian", 8);

		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.IRON_ORE)),  newOutput(new ItemStack(ModItems.IronDust, 2), 1.0f), newOutput(new ItemStack(ModItems.NickelDust), 0.1f));		
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.GOLD_ORE)),  newOutput(new ItemStack(ModItems.GoldDust, 2), 1.0f), newOutput(new ItemStack(ModItems.CopperDust, 1), 0.1f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.COAL_ORE)),  newOutput(new ItemStack(Items.COAL, 4), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.REDSTONE_ORE)),  newOutput(new ItemStack(Items.REDSTONE, 5), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.DIAMOND_ORE)),  newOutput(new ItemStack(Items.DIAMOND, 3), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.EMERALD_ORE)),  newOutput(new ItemStack(Items.EMERALD, 3), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.QUARTZ_ORE)),  newOutput(new ItemStack(Items.QUARTZ, 4), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.LAPIS_ORE)),  newOutput(new ItemStack(Items.DYE, 4, 4), 1.0f));
		
		//Items
		oreDictionaryRecipe("Sulfur", 4);
		oreDictionaryRecipe("Saltpeter", 4);
		oreDictionaryRecipe("Potash", 4);
		oreDictionaryRecipe("Bitumen", 4);
		oreDictionaryRecipe("Salt", 4);
		if(OreDictionary.getOres("dustCoal").size() > 0) {
			RegisterHelper.registerGrinderRecipe(new ItemStack(Items.COAL), newOutput(OreDictionary.getOres("dustCoal").get(0), 1.0f));	
		}

		oreDictionaryOutput(new ItemStack(Item.getItemFromBlock(Blocks.SAND)), "itemSilicon", 1);
		
		//Ingots
		for(int index = 0; index < OreDictionary.getOres("ingotCopper").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("ingotCopper").get(index), newOutput(new ItemStack(ModItems.CopperDust, 1), 1.0f));
		}
		for(int index = 0; index < OreDictionary.getOres("ingotSilver").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("ingotSilver").get(index), newOutput(new ItemStack(ModItems.SilverDust, 1), 1.0f));
		}
		for(int index = 0; index < OreDictionary.getOres("ingotTin").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("ingotTin").get(index), newOutput(new ItemStack(ModItems.TinDust, 1), 1.0f));
		}
		for(int index = 0; index < OreDictionary.getOres("ingotLead").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("ingotLead").get(index), newOutput(new ItemStack(ModItems.LeadDust, 1), 1.0f));
		}
		for(int index = 0; index < OreDictionary.getOres("ingotPlatinum").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("ingotPlatinum").get(index), newOutput(new ItemStack(ModItems.PlatinumDust, 1), 1.0f));
		}
		for(int index = 0; index < OreDictionary.getOres("ingotNickel").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("ingotNickel").get(index), newOutput(new ItemStack(ModItems.NickelDust, 1), 1.0f));
		}
		for(int index = 0; index < OreDictionary.getOres("ingotAluminium").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("ingotAluminium").get(index), newOutput(new ItemStack(ModItems.AluminiumDust, 1), 1.0f));
		}
		for(int index = 0; index < OreDictionary.getOres("ingotBauxite").size(); index++) {
			RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("ingotBauxite").get(index), newOutput(new ItemStack(ModItems.AluminiumDust, 1), 1.0f));
		}
		
		oreDictionaryRecipe("ingotSteel", "dustSteel", 1);
		oreDictionaryRecipe("ingotBronze", "dustBronze", 1);
		oreDictionaryRecipe("ingotElectrum", "dustElectrum", 1);
		oreDictionaryRecipe("ingotInvar", "dustInvar", 1);
		oreDictionaryRecipe("ingotUranium", "dustUranium", 1);
		oreDictionaryRecipe("ingotAluminium", "dustAluminium", 1);
		
		oreDictionaryRecipe("oreRuby", "gemRuby", 3);
		oreDictionaryRecipe("oreSapphire", "gemSapphire", 3);
		oreDictionaryRecipe("orePeridot", "gemPeridot", 3);
		oreDictionaryRecipe("gemRuby", "dustRuby", 1);
		oreDictionaryRecipe("gemSapphire", "dustSapphire", 1);
		oreDictionaryRecipe("gemPeridot", "dustPeridot", 1);
		
		RegisterHelper.registerGrinderRecipe(new ItemStack(Items.IRON_INGOT), newOutput(new ItemStack(ModItems.IronDust, 1), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Items.GOLD_INGOT), newOutput(new ItemStack(ModItems.GoldDust, 1), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(ModItems.RedstoneAlloyIngot), newOutput(new ItemStack(ModItems.RedstoneAlloyDust, 1), 1.0f));
		
		//Utils
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.WOOL)), newOutput(new ItemStack(Items.STRING, 4), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.GRAVEL)), newOutput(new ItemStack(Items.FLINT, 1), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE)), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.SAND)), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE_WALL)), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.SAND)), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.GLASS)), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.SAND)), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.DIRT)), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.GRAVEL)), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.STONE)), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE)), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.SANDSTONE)), newOutput(new ItemStack(Item.getItemFromBlock(Blocks.SAND), 4), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY)), newOutput(new ItemStack(Items.CLAY_BALL, 3), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.HARDENED_CLAY)), newOutput(new ItemStack(Items.CLAY_BALL, 3), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Item.getItemFromBlock(Blocks.BRICK_BLOCK)), newOutput(new ItemStack(Items.BRICK, 3), 1.0f));
		RegisterHelper.registerGrinderRecipe(new ItemStack(Items.BONE), newOutput(new ItemStack(Items.DYE, 6, 15), 1.0f));
	}
	public static void oreDictionaryRecipe(String input, String output, int number) {
		if(OreDictionary.getOres("input").size() > 0) {
			for(int index = 0; index < OreDictionary.getOres("input").size(); index++) {
				RegisterHelper.registerGrinderRecipe(OreDictionary.getOres("input").get(index), newOutput(new ItemStack(OreDictionary.getOres("output").get(0).getItem(), number), 1.0f));
			}	
		}
	}
	public static void oreDictionaryRecipe(String metal, int number) {
		String ore = "ore";
		String dust = "dust";
		if(OreDictionary.getOres(dust+metal).size() > 0) {
			for(int index = 0; index < OreDictionary.getOres(ore+metal).size(); index++) {
				ItemStack oreDustStack = OreDictionaryUtilities.getOreDicitionaryItemStackToModify(dust+metal, 0);
				oreDustStack.setCount(number);
				RegisterHelper.registerGrinderRecipe(OreDictionary.getOres(ore+metal).get(index), newOutput(oreDustStack, 1.0f));
			}	
		}
	}
	public static void oreDictionaryOutput(ItemStack input, String output, int number) {
		if(OreDictionary.getOres(output).size() > 0) {
			ItemStack outputStack = OreDictionaryUtilities.getOreDicitionaryItemStackToModify(output, 0);
			outputStack.setCount(number);
			RegisterHelper.registerGrinderRecipe(input, newOutput(outputStack, 1.0f));	
		}

	}
	public static void ingotDictionaryRecipe(String metal, int number) {
		String ingot = "ingot";
		String dust = "dust";
		if(OreDictionary.getOres(dust+metal).size() > 0) {
			for(int index = 0; index < OreDictionary.getOres(ingot+metal).size(); index++) {
				ItemStack ingotDustStack = OreDictionaryUtilities.getOreDicitionaryItemStackToModify(dust+metal, 0);
				ingotDustStack.setCount(number);
				RegisterHelper.registerGrinderRecipe(OreDictionary.getOres(ingot+metal).get(index), newOutput(ingotDustStack, 1.0f));
			}
		}
	}
	public static void baseDictionaryRecipe(String input, String output, int number) {
		if(OreDictionary.getOres(output).size() > 0) {
			for(int index = 0; index < OreDictionary.getOres(input).size(); index++) {
				ItemStack result = OreDictionaryUtilities.getOreDicitionaryItemStackToModify(output, 0);
				result.setCount(number);
				RegisterHelper.registerGrinderRecipe(OreDictionary.getOres(input).get(index), newOutput(result, 1.0f));
			}
		}
	}
	public static GrinderOutput newOutput(ItemStack itemstack, float percentage) {
		GrinderOutput tempOutput = new GrinderOutput(itemstack, percentage);
		return tempOutput;
	}
	public static void registerGrinderRecipe(ItemStack input, GrinderOutput... output) {
		RegisterHelper.registerGrinderRecipe(input, output);
	}
}
