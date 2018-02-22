package theking530.staticpower.handlers;

import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;

public class OreDictionaryRegistration {
	
	public static void registerOres() {
		OreDictionary.registerOre("logWood", ModBlocks.StaticWood);
		OreDictionary.registerOre("logWood", ModBlocks.EnergizedWood);
		OreDictionary.registerOre("logWood", ModBlocks.LumumWood);
		
		OreDictionary.registerOre("plankWood", ModBlocks.StaticPlanks);
		OreDictionary.registerOre("plankWood", ModBlocks.EnergizedPlanks);
		OreDictionary.registerOre("plankWood", ModBlocks.LumumPlanks);
		
		OreDictionary.registerOre("blockCopper", ModBlocks.BlockCopper);
		OreDictionary.registerOre("blockTin", ModBlocks.BlockTin);
		OreDictionary.registerOre("blockSilver", ModBlocks.BlockSilver);
		OreDictionary.registerOre("blockLead", ModBlocks.BlockLead);
		OreDictionary.registerOre("blockPlatinum", ModBlocks.BlockPlatinum);	
		OreDictionary.registerOre("blockNickel", ModBlocks.BlockNickel);
		OreDictionary.registerOre("blockAluminium", ModBlocks.BlockAluminium);
		OreDictionary.registerOre("blockAluminum", ModBlocks.BlockAluminium);
		OreDictionary.registerOre("blockSapphire", ModBlocks.BlockSapphire);
		OreDictionary.registerOre("blockRuby", ModBlocks.BlockRuby);
		
		OreDictionary.registerOre("oreCopper", ModBlocks.CopperOre);
		OreDictionary.registerOre("oreTin", ModBlocks.TinOre);
		OreDictionary.registerOre("oreSilver", ModBlocks.SilverOre);
		OreDictionary.registerOre("oreLead", ModBlocks.LeadOre);
		OreDictionary.registerOre("orePlatinum", ModBlocks.PlatinumOre);
		OreDictionary.registerOre("oreNickel", ModBlocks.NickelOre);
		OreDictionary.registerOre("oreAluminium", ModBlocks.AluminiumOre);
		OreDictionary.registerOre("oreAluminum", ModBlocks.AluminiumOre);
		OreDictionary.registerOre("oreSapphire", ModBlocks.SapphireOre);
		OreDictionary.registerOre("oreRuby", ModBlocks.RubyOre);
		
		OreDictionary.registerOre("itemSilicon", ModItems.Silicon);
		
		OreDictionary.registerOre("wireCopper", ModItems.CopperWire);
		OreDictionary.registerOre("wireSilver", ModItems.SilverWire);
		OreDictionary.registerOre("wireGold", ModItems.GoldWire);
		
		OreDictionary.registerOre("itemSilicon", ModItems.Silicon);
	}
}
