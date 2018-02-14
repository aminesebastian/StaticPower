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
		
		OreDictionary.registerOre("ingotSilver", ModItems.SilverIngot);
		OreDictionary.registerOre("ingotTin", ModItems.TinIngot);
		OreDictionary.registerOre("ingotLead", ModItems.LeadIngot);
		OreDictionary.registerOre("ingotCopper", ModItems.CopperIngot);
		OreDictionary.registerOre("ingotPlatinum", ModItems.PlatinumIngot);
		OreDictionary.registerOre("ingotAluminium", ModItems.AluminiumIngot);
		OreDictionary.registerOre("ingotAluminum", ModItems.AluminiumIngot);
		OreDictionary.registerOre("ingotNickel", ModItems.NickelIngot);
		
		OreDictionary.registerOre("gemSapphire", ModItems.SapphireGem);
		OreDictionary.registerOre("gemRuby", ModItems.RubyGem);
		
		OreDictionary.registerOre("dustTin", ModItems.TinDust);
		OreDictionary.registerOre("dustLead", ModItems.LeadDust);
		OreDictionary.registerOre("dustCopper", ModItems.CopperDust);
		OreDictionary.registerOre("dustPlatinum", ModItems.PlatinumDust);
		OreDictionary.registerOre("dustIron", ModItems.IronDust);
		OreDictionary.registerOre("dustGold", ModItems.GoldDust);
		OreDictionary.registerOre("dustNickel", ModItems.NickelDust);
		OreDictionary.registerOre("dustAluminium", ModItems.AluminiumDust);
		OreDictionary.registerOre("dustAluminum", ModItems.AluminiumDust);
		
		OreDictionary.registerOre("plateTin", ModItems.TinPlate);
		OreDictionary.registerOre("plateLead", ModItems.LeadPlate);
		OreDictionary.registerOre("plateCopper", ModItems.CopperPlate);
		OreDictionary.registerOre("plateIron", ModItems.IronPlate);
		OreDictionary.registerOre("plateGold", ModItems.GoldPlate);
		OreDictionary.registerOre("plateSilver", ModItems.SilverPlate);
		
		OreDictionary.registerOre("wireCopper", ModItems.CopperWire);
		OreDictionary.registerOre("wireSilver", ModItems.SilverWire);
		OreDictionary.registerOre("wireGold", ModItems.GoldWire);
		
		OreDictionary.registerOre("itemSilicon", ModItems.Silicon);
	}
}
