package theking530.staticpower;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class StaticPowerConfig {
	
	public static boolean COPPER_ORE_GEN;
	public static boolean TIN_ORE_GEN;
	public static boolean LEAD_ORE_GEN;
	public static boolean SILVER_ORE_GEN;
	public static boolean PLATINUM_ORE_GEN;
		
	public static void updateConfig() {
		MinecraftForge.EVENT_BUS.register(StaticPower.instance);
		Configuration config = StaticPower.config;
		
		final String oreGen = config.CATEGORY_GENERAL + config.CATEGORY_SPLITTER + "Ore Generation";
		config.addCustomCategoryComment(oreGen, "Disable ore generation by ore.");
		
		COPPER_ORE_GEN = config.getBoolean("Copper Ore", oreGen, true, "Disable or Enable Copper Ore Generation");
		TIN_ORE_GEN = config.getBoolean("Tin Ore", oreGen, true, "Disable or Enable Tin Ore Generation");
		LEAD_ORE_GEN = config.getBoolean("Lead Ore", oreGen, true, "Disable or Enable Lead Ore Generation");
		SILVER_ORE_GEN = config.getBoolean("Silver Ore", oreGen, true, "Disable or Enable Silver Ore Generation");
		PLATINUM_ORE_GEN = config.getBoolean("Platinum Ore", oreGen, true, "Disable or Enable Platinum Ore Generation");
		
		if(StaticPower.config.hasChanged()) {
			StaticPower.config.save();
		}
	}	
}
