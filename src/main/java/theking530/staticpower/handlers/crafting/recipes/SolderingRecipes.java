package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;

public class SolderingRecipes {
	
	@SuppressWarnings("all")
	public static void registerSolderingRecipes() {
		
		//Circuit Recipes
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicCircuit, 4), new Object[]{"R R", " I ", "R R", 
		'I', ModItems.IronPlate, 'R', Items.REDSTONE});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticCircuit, 4), new Object[]{"R R", "GSG", "R R", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'S', ModItems.StaticPlate, 'C', ModItems.BasicCircuit});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedCircuit, 4), new Object[]{"R R", "GEG", "R R", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'E', ModItems.EnergizedPlate, 'C', ModItems.StaticCircuit});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumCircuit, 4), new Object[]{"R R", "GLG", "R R", 
		'G', Items.GOLD_INGOT, 'R', Items.REDSTONE, 'L', ModItems.LumumPlate, 'C', ModItems.EnergizedCircuit});
		
		//Batteries --------------------------------------------------------------------------------------------------
		for(int i=0; i<OreDictionary.getOres("nuggetIron").size(); i++) {
			RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicBattery), new Object[]{" C ", "IRI", "IRI", 
					'I', ModItems.IronPlate, 'R', Blocks.REDSTONE_BLOCK, 'C', OreDictionary.getOres("nuggetIron").get(i)});		
		}
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticBattery), new Object[]{" S ", "IDI", "IRI", 
		'I', ModItems.StaticIngot, 'R', Blocks.REDSTONE_BLOCK, 'S', ModItems.StaticNugget, 'D', Items.DIAMOND});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedBattery), new Object[]{" E ", "IDI", "IRI", 
		'I', ModItems.EnergizedIngot, 'R', Blocks.REDSTONE_BLOCK, 'E', ModItems.EnergizedNugget, 'D', ModItems.EnergizedEnergyCrystal});	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumBattery), new Object[]{" L ", "IDI", "IRI", 
		'I', ModItems.LumumIngot, 'R', Blocks.REDSTONE_BLOCK, 'L', ModItems.LumumNugget, 'D', ModItems.LumumEnergyCrystal});	
		
		//Tools -------------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.ElectricSolderingIron), new Object[]{"I  "," IL"," LR",
		'R', ModItems.BasicBattery, 'I', Items.IRON_INGOT, 'L', new ItemStack(Items.DYE, 1, 4)});		
	
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.CoordinateMarker), new Object[]{"IIR","IGI","CBC",
		'B', ModItems.BasicBattery, 'I', Items.IRON_INGOT, 'R', Items.REDSTONE, 'G', Blocks.GLASS, 'C', ModItems.BasicCircuit});	
		
		//Upgrades --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ModItems.TinPlate, 'L', new ItemStack(Items.DYE, 1, 4), 'G', ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ModItems.TinPlate, 'R', Items.REDSTONE, 'G',  ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ModItems.TinPlate, 'P', Items.GLOWSTONE_DUST, 'G',  ModItems.BasicUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', Blocks.PLANKS, 'G',  ModItems.BasicUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ModItems.TinPlate, 'L', new ItemStack(Items.DYE, 1, 4), 'G', ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ModItems.TinPlate, 'R', Items.REDSTONE, 'G',  ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ModItems.TinPlate, 'P', Items.GLOWSTONE_DUST, 'G',  ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticQuarryingUpgrade), new Object[]{" P "," G ","P P",
		'P', Items.IRON_PICKAXE, 'G', ModItems.StaticUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ModItems.StaticPlate, 'G',  ModItems.StaticUpgradePlate});
			
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ModItems.TinPlate, 'L', new ItemStack(Items.DYE, 1, 4), 'G', ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ModItems.TinPlate, 'R', Items.REDSTONE, 'G',  ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ModItems.TinPlate, 'P', Items.GLOWSTONE_DUST, 'G',  ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedQuarryingUpgrade), new Object[]{" P "," G ","P P",
		'P', Items.GOLDEN_PICKAXE, 'G', ModItems.EnergizedUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ModItems.EnergizedPlate, 'G',  ModItems.EnergizedUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumTankUpgrade), new Object[]{" B ","LGL","I I",
		'B', Items.BUCKET, 'I', ModItems.TinPlate, 'L', Blocks.LAPIS_BLOCK, 'G', ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumPowerUpgrade), new Object[]{"R R"," G ","I I",
		'I', ModItems.TinPlate, 'R', Blocks.REDSTONE_BLOCK, 'G',  ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumSpeedUpgrade), new Object[]{" P "," G ","I I",
		'I', ModItems.TinPlate, 'P', Blocks.GLOWSTONE, 'G',  ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumQuarryingUpgrade), new Object[]{" P "," G ","P P",
		'P', Items.DIAMOND_PICKAXE, 'G', ModItems.LumumUpgradePlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumRangeUpgrade), new Object[]{"PPP"," G ","PPP",
		'P', ModItems.LumumPlate, 'G',  ModItems.LumumUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.TeleportUpgrade), new Object[]{"EHE"," P ","EHE",
		'H', Item.getItemFromBlock(Blocks.HOPPER), 'E', Items.ENDER_PEARL, 'P',  ModItems.EnergizedUpgradePlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.ExperienceVacuumUpgrade), new Object[]{"CGC","GPG","CGC",
		'G', Item.getItemFromBlock(Blocks.GLASS), 'C', ModItems.BaseFluidCapsule, 'P',  ModItems.StaticUpgradePlate});
		
		//Molds --------------------------------------------------------------------------------------------------
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.PlateMould), new Object[]{"   "," C "," P ",
		'P', ModItems.IronPlate, 'C',  ModItems.CopperPlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.PlateMould), new Object[]{"   "," C "," P ",
		'P', ModItems.IronPlate, 'C',  ModItems.TinPlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.PlateMould), new Object[]{"   "," C "," P ",
		'P', ModItems.IronPlate, 'C',  ModItems.SilverPlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.PlateMould), new Object[]{"   "," C "," P ",
		'P', ModItems.IronPlate, 'C',  ModItems.GoldPlate});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.PlateMould), new Object[]{"   "," C "," P ",
		'P', ModItems.IronPlate, 'C',  ModItems.LeadPlate});
		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.WireMould), new Object[]{"   "," C "," P ",
		'P', ModItems.IronPlate, 'C',  ModItems.CopperWire});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.WireMould), new Object[]{"   "," C "," P ",
		'P', ModItems.IronPlate, 'C',  ModItems.SilverWire});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.WireMould), new Object[]{"   "," C "," P ",
		'P', ModItems.IronPlate, 'C',  ModItems.GoldWire});
		
		//Filters --------------------------------------------------------------------------------------------------
		for(int i=0; i<OreDictionary.getOres("nuggetIron").size(); i++) {
			RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.BasicItemFilter), new Object[]{" P ","PGP"," C ",
					'P', Items.PAPER, 'C', ModItems.BasicCircuit, 'G', OreDictionary.getOres("nuggetIron").get(i)});
		}
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.UpgradedItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.StaticCircuit, 'G', ModItems.StaticNugget});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.AdvancedItemFilter), new Object[]{" P ","PGP"," C ",
		'P', Items.PAPER, 'C', ModItems.EnergizedCircuit, 'G', ModItems.EnergizedNugget});
		
		//Gate Components
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModBlocks.LogicGateBasePlate, 2), new Object[]{"   ", "   ", "SBS", 
		'S', ModItems.IronPlate, 'B', ModItems.BasicCircuit});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LogicGateServo, 4), new Object[]{"S S", " C ", "QBQ", 
		'S', ModItems.IronPlate, 'B', ModItems.BasicBattery, 'C', ModItems.CopperCoil, 'Q', ModItems.SilverWire});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LogicGatePowerSync, 2), new Object[]{"   ", " R ", " S ", 
		'S', ModItems.BasicCircuit, 'R', Blocks.REDSTONE_TORCH});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.InvertedLogicGatePowerSync, 2), new Object[]{" Q ", " R ", " S ", 
		'S', ModItems.BasicCircuit, 'R', Blocks.REDSTONE_TORCH, 'Q', Items.REDSTONE});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.Diode, 8), new Object[]{"   ", "SRS", "QCQ", 
		'S', ModItems.IronPlate, 'Q', ModItems.SilverWire, 'R', Item.getItemFromBlock(Blocks.UNPOWERED_REPEATER), 'C', ModItems.BasicCircuit});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.Transistor, 8), new Object[]{" S ", "SRS", "QCQ", 
		'S', ModItems.IronPlate, 'Q', ModItems.SilverWire, 'R', Item.getItemFromBlock(Blocks.LEVER), 'C', ModItems.BasicCircuit});
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.InternalClock, 2), new Object[]{" S ", "SRS", "QCQ", 
		'S', ModItems.IronPlate, 'Q', ModItems.SilverWire, 'R', Items.QUARTZ, 'C', ModItems.BasicCircuit});
		
		//Fluid Canisters
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.StaticFluidCapsule), new Object[]{"PPP", "CPC", "PPP", 
		'P', ModItems.StaticPlate, 'C', ModItems.BaseFluidCapsule});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.EnergizedFluidCapsule), new Object[]{"PPP", "CPC", "PPP", 
		'P', ModItems.EnergizedPlate, 'C', ModItems.StaticFluidCapsule});		
		RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.LumumFluidCapsule), new Object[]{"PPP", "CPC", "PPP", 
		'P', ModItems.LumumPlate, 'C', ModItems.EnergizedFluidCapsule});	
		
		registerSiliconOreDictRecipes();
	}
	private static void registerSiliconOreDictRecipes() {
		for(int i=0; i<OreDictionary.getOres("itemSilicon").size(); i++) {
			RegisterHelper.registerSolderingRecipe(new ItemStack(ModItems.MemoryChip, 8), new Object[]{"PPP", "SSS", "QCQ", 
					'P', ModItems.IronPlate, 'Q', ModItems.SilverWire, 'S', OreDictionary.getOres("itemSilicon").get(i).getItem(), 'C', ModItems.BasicCircuit});
		}
	}
}
