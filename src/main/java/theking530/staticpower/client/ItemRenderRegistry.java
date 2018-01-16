package theking530.staticpower.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.armor.ModArmor;
import theking530.staticpower.items.tools.basictools.ModTools;
import theking530.staticpower.world.plants.ModPlants;

public class ItemRenderRegistry {

	public static void initItemRenderers() {
		ItemRenderRegistry.registerItem(ModItems.PotatoFlour);
		ItemRenderRegistry.registerItem(ModItems.WheatFlour);
		ItemRenderRegistry.registerItem(ModItems.PotatoBread);
		
		ItemRenderRegistry.registerItem(ModItems.ApplePie);
		ItemRenderRegistry.registerItem(ModItems.StaticPie);
		ItemRenderRegistry.registerItem(ModItems.EnergizedPie);
		ItemRenderRegistry.registerItem(ModItems.LumumPie);
		
		ItemRenderRegistry.registerItem(ModArmor.BaseShield);
		
		ItemRenderRegistry.registerItem(ModArmor.SkeletonHelmet);
		ItemRenderRegistry.registerItem(ModArmor.SkeletonChestplate);
		ItemRenderRegistry.registerItem(ModArmor.SkeletonLeggings);
		ItemRenderRegistry.registerItem(ModArmor.SkeletonBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.UndeadHelmet);
		ItemRenderRegistry.registerItem(ModArmor.UndeadChestplate);
		ItemRenderRegistry.registerItem(ModArmor.UndeadLeggings);
		ItemRenderRegistry.registerItem(ModArmor.UndeadBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.StaticHelmet);
		ItemRenderRegistry.registerItem(ModArmor.StaticChestplate);
		ItemRenderRegistry.registerItem(ModArmor.StaticLeggings);
		ItemRenderRegistry.registerItem(ModArmor.StaticBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.EnergizedHelmet);
		ItemRenderRegistry.registerItem(ModArmor.EnergizedChestplate);
		ItemRenderRegistry.registerItem(ModArmor.EnergizedLeggings);
		ItemRenderRegistry.registerItem(ModArmor.EnergizedBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.LumumHelmet);
		ItemRenderRegistry.registerItem(ModArmor.LumumChestplate);
		ItemRenderRegistry.registerItem(ModArmor.LumumLeggings);
		ItemRenderRegistry.registerItem(ModArmor.LumumBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.CopperHelmet);
		ItemRenderRegistry.registerItem(ModArmor.CopperChestplate);
		ItemRenderRegistry.registerItem(ModArmor.CopperLeggings);
		ItemRenderRegistry.registerItem(ModArmor.CopperBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.TinHelmet);
		ItemRenderRegistry.registerItem(ModArmor.TinChestplate);
		ItemRenderRegistry.registerItem(ModArmor.TinLeggings);
		ItemRenderRegistry.registerItem(ModArmor.TinBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.LeadHelmet);
		ItemRenderRegistry.registerItem(ModArmor.LeadChestplate);
		ItemRenderRegistry.registerItem(ModArmor.LeadLeggings);
		ItemRenderRegistry.registerItem(ModArmor.LeadBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.SilverHelmet);
		ItemRenderRegistry.registerItem(ModArmor.SilverChestplate);
		ItemRenderRegistry.registerItem(ModArmor.SilverLeggings);
		ItemRenderRegistry.registerItem(ModArmor.SilverBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.PlatinumHelmet);
		ItemRenderRegistry.registerItem(ModArmor.PlatinumChestplate);
		ItemRenderRegistry.registerItem(ModArmor.PlatinumLeggings);
		ItemRenderRegistry.registerItem(ModArmor.PlatinumBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.AluminiumHelmet);
		ItemRenderRegistry.registerItem(ModArmor.AluminiumChestplate);
		ItemRenderRegistry.registerItem(ModArmor.AluminiumLeggings);
		ItemRenderRegistry.registerItem(ModArmor.AluminiumBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.SapphireHelmet);
		ItemRenderRegistry.registerItem(ModArmor.SapphireChestplate);
		ItemRenderRegistry.registerItem(ModArmor.SapphireLeggings);
		ItemRenderRegistry.registerItem(ModArmor.SapphireBoots);
		
		ItemRenderRegistry.registerItem(ModArmor.RubyHelmet);
		ItemRenderRegistry.registerItem(ModArmor.RubyChestplate);
		ItemRenderRegistry.registerItem(ModArmor.RubyLeggings);
		ItemRenderRegistry.registerItem(ModArmor.RubyBoots);

		ItemRenderRegistry.registerItem(ModItems.SilverDust);
		ItemRenderRegistry.registerItem(ModItems.CopperDust);
		ItemRenderRegistry.registerItem(ModItems.IronDust);
		ItemRenderRegistry.registerItem(ModItems.GoldDust);
		ItemRenderRegistry.registerItem(ModItems.SilverDust);
		ItemRenderRegistry.registerItem(ModItems.TinDust);
		ItemRenderRegistry.registerItem(ModItems.LeadDust);
		ItemRenderRegistry.registerItem(ModItems.PlatinumDust);
		ItemRenderRegistry.registerItem(ModItems.NickelDust);
		ItemRenderRegistry.registerItem(ModItems.AluminiumDust);
		ItemRenderRegistry.registerItem(ModItems.StaticDust);
		ItemRenderRegistry.registerItem(ModItems.EnergizedDust);
		ItemRenderRegistry.registerItem(ModItems.LumumDust);
		ItemRenderRegistry.registerItem(ModItems.InertInfusionBlend);
		ItemRenderRegistry.registerItem(ModItems.RedstoneAlloyDust);
		
		ItemRenderRegistry.registerItem(ModItems.StaticNugget);
		ItemRenderRegistry.registerItem(ModItems.EnergizedNugget);
		ItemRenderRegistry.registerItem(ModItems.LumumNugget);
		ItemRenderRegistry.registerItem(ModItems.IronNugget);
		
		ItemRenderRegistry.registerItem(ModItems.EnergizedInfusionBlend);
		ItemRenderRegistry.registerItem(ModItems.LumumInfusionBlend);	
		ItemRenderRegistry.registerItem(ModItems.EnergizedEnergyCrystal);
		ItemRenderRegistry.registerItem(ModItems.LumumEnergyCrystal);
		ItemRenderRegistry.registerItem(ModItems.CopperCoil);
		ItemRenderRegistry.registerItem(ModItems.SilverCoil);
		ItemRenderRegistry.registerItem(ModItems.GoldCoil);
		ItemRenderRegistry.registerItem(ModItems.CopperWire);
		ItemRenderRegistry.registerItem(ModItems.SilverWire);
		ItemRenderRegistry.registerItem(ModItems.GoldWire);
		ItemRenderRegistry.registerItem(ModItems.IronPlate);
		ItemRenderRegistry.registerItem(ModItems.CopperPlate);
		ItemRenderRegistry.registerItem(ModItems.TinPlate);
		ItemRenderRegistry.registerItem(ModItems.SilverPlate);
		ItemRenderRegistry.registerItem(ModItems.GoldPlate);
		ItemRenderRegistry.registerItem(ModItems.LeadPlate);
		ItemRenderRegistry.registerItem(ModItems.StaticPlate);
		ItemRenderRegistry.registerItem(ModItems.EnergizedPlate);
		ItemRenderRegistry.registerItem(ModItems.LumumPlate);
		
		ItemRenderRegistry.registerItem(ModItems.SilverIngot);
		ItemRenderRegistry.registerItem(ModItems.CopperIngot);
		ItemRenderRegistry.registerItem(ModItems.TinIngot);
		ItemRenderRegistry.registerItem(ModItems.LeadIngot);
		ItemRenderRegistry.registerItem(ModItems.PlatinumIngot);
		ItemRenderRegistry.registerItem(ModItems.StaticIngot);
		ItemRenderRegistry.registerItem(ModItems.EnergizedIngot);
		ItemRenderRegistry.registerItem(ModItems.LumumIngot);
		ItemRenderRegistry.registerItem(ModItems.InertIngot);
		ItemRenderRegistry.registerItem(ModItems.RedstoneAlloyIngot);
		ItemRenderRegistry.registerItem(ModItems.NickelIngot);
		ItemRenderRegistry.registerItem(ModItems.AluminiumIngot);
		ItemRenderRegistry.registerItem(ModItems.SapphireGem);
		ItemRenderRegistry.registerItem(ModItems.RubyGem);
		
		ItemRenderRegistry.registerItem(ModItems.StaticWrench);
		ItemRenderRegistry.registerItem(ModItems.SolderingIron);
		ItemRenderRegistry.registerItem(ModItems.CoordinateMarker);
		ItemRenderRegistry.registerItem(ModItems.StaticBook);
		ItemRenderRegistry.registerItem(ModItems.WireCutters);
		ItemRenderRegistry.registerItem(ModItems.MetalHammer);
		ItemRenderRegistry.registerItem(ModItems.ElectricSolderingIron);
		
		ItemRenderRegistry.registerItem(ModItems.BasicCircuit);
		ItemRenderRegistry.registerItem(ModItems.StaticCircuit);
		ItemRenderRegistry.registerItem(ModItems.EnergizedCircuit);
		ItemRenderRegistry.registerItem(ModItems.LumumCircuit);
		
		ItemRenderRegistry.registerItem(ModItems.BasicCircuit);
		ItemRenderRegistry.registerItem(ModItems.StaticCircuit);
		ItemRenderRegistry.registerItem(ModItems.EnergizedCircuit);
		ItemRenderRegistry.registerItem(ModItems.LumumCircuit);
		
		ItemRenderRegistry.registerItem(ModItems.BasicBattery);
		ItemRenderRegistry.registerItem(ModItems.StaticBattery);
		ItemRenderRegistry.registerItem(ModItems.EnergizedBattery);
		ItemRenderRegistry.registerItem(ModItems.LumumBattery);
		//FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((IItemColor)ModItems.BasicBattery, 
				//ModItems.BasicBattery, ModItems.StaticBattery, ModItems.EnergizedBattery, ModItems.LumumBattery);
		
		ItemRenderRegistry.registerItem(ModItems.Rubber);
		ItemRenderRegistry.registerItem(ModItems.IOPort);
		
		ItemRenderRegistry.registerItem(ModItems.BasicPowerUpgrade);
		ItemRenderRegistry.registerItem(ModItems.BasicSpeedUpgrade);
		ItemRenderRegistry.registerItem(ModItems.BasicTankUpgrade);
		ItemRenderRegistry.registerItem(ModItems.BasicRangeUpgrade);
		ItemRenderRegistry.registerItem(ModItems.StaticPowerUpgrade);
		ItemRenderRegistry.registerItem(ModItems.StaticSpeedUpgrade);
		ItemRenderRegistry.registerItem(ModItems.StaticTankUpgrade);
		ItemRenderRegistry.registerItem(ModItems.StaticQuarryingUpgrade);
		ItemRenderRegistry.registerItem(ModItems.StaticRangeUpgrade);
		ItemRenderRegistry.registerItem(ModItems.EnergizedPowerUpgrade);
		ItemRenderRegistry.registerItem(ModItems.EnergizedSpeedUpgrade);
		ItemRenderRegistry.registerItem(ModItems.EnergizedTankUpgrade);
		ItemRenderRegistry.registerItem(ModItems.EnergizedQuarryingUpgrade);
		ItemRenderRegistry.registerItem(ModItems.EnergizedRangeUpgrade);
		ItemRenderRegistry.registerItem(ModItems.LumumPowerUpgrade);
		ItemRenderRegistry.registerItem(ModItems.LumumSpeedUpgrade);
		ItemRenderRegistry.registerItem(ModItems.LumumTankUpgrade);
		ItemRenderRegistry.registerItem(ModItems.LumumQuarryingUpgrade);
		ItemRenderRegistry.registerItem(ModItems.LumumRangeUpgrade);
		ItemRenderRegistry.registerItem(ModItems.BasicUpgradePlate);
		ItemRenderRegistry.registerItem(ModItems.StaticUpgradePlate);
		ItemRenderRegistry.registerItem(ModItems.EnergizedUpgradePlate);
		ItemRenderRegistry.registerItem(ModItems.LumumUpgradePlate);
		
		ItemRenderRegistry.registerItem(ModPlants.StaticCrop);
		ItemRenderRegistry.registerItem(ModPlants.EnergizedCrop);
		ItemRenderRegistry.registerItem(ModPlants.LumumCrop);
		ItemRenderRegistry.registerItem(ModPlants.DepletedCrop);
		ItemRenderRegistry.registerItem(ModPlants.StaticSeeds);
		ItemRenderRegistry.registerItem(ModPlants.EnergizedSeeds);
		ItemRenderRegistry.registerItem(ModPlants.LumumSeeds);
		
		ItemRenderRegistry.registerItem(ModItems.BasicItemFilter);
		ItemRenderRegistry.registerItem(ModItems.UpgradedItemFilter);
		ItemRenderRegistry.registerItem(ModItems.AdvancedItemFilter);

		registerBlock(ModBlocks.MachineBlock);
		registerBlock(ModBlocks.StaticBattery);
		registerBlock(ModBlocks.EnergizedBattery);
		registerBlock(ModBlocks.LumumBattery);
		registerBlock(ModBlocks.CropSqueezer);
		registerBlock(ModBlocks.MechanicalSqueezer);
		registerBlock(ModBlocks.FluidGenerator);
		registerBlock(ModBlocks.FluidInfuser);
		registerBlock(ModBlocks.FusionFurnace);
		registerBlock(ModBlocks.PoweredFurnace);
		registerBlock(ModBlocks.PoweredGrinder);
		
		registerBlock(ModBlocks.SolderingTable);
		
		registerBlock(ModBlocks.BasicSolarPanel);
		registerBlock(ModBlocks.StaticSolarPanel);
		registerBlock(ModBlocks.EnergizedSolarPanel);
		registerBlock(ModBlocks.LumumSolarPanel);
		registerBlock(ModBlocks.CreativeSolarPanel);

        registerBlock(ModBlocks.Quarry);
		registerBlock(ModBlocks.BasicFarmer);
		registerBlock(ModBlocks.ChargingStation);
		registerBlock(ModBlocks.AdvancedEarth);
		registerBlock(ModBlocks.Fermenter);
		registerBlock(ModBlocks.HeatingElement);
		registerBlock(ModBlocks.Distillery);
		registerBlock(ModBlocks.Condenser);
		
		registerBlock(ModBlocks.SilverOre);
		registerBlock(ModBlocks.CopperOre);
		registerBlock(ModBlocks.TinOre);
		registerBlock(ModBlocks.LeadOre);
		registerBlock(ModBlocks.PlatinumOre);
		registerBlock(ModBlocks.NickelOre);
		registerBlock(ModBlocks.AluminiumOre);
		registerBlock(ModBlocks.SapphireOre);
		registerBlock(ModBlocks.RubyOre);
		
		registerBlock(ModBlocks.StaticPlanks);
		registerBlock(ModBlocks.EnergizedPlanks);
		registerBlock(ModBlocks.LumumPlanks);
		registerBlock(ModBlocks.StaticCobblestone);
		registerBlock(ModBlocks.EnergizedCobblestone);
		registerBlock(ModBlocks.LumumCobblestone);

		registerBlock(ModBlocks.StaticBlock);
		registerBlock(ModBlocks.EnergizedBlock);
		registerBlock(ModBlocks.LumumBlock);
		registerBlock(ModBlocks.BlockCopper);
		registerBlock(ModBlocks.BlockTin);
		registerBlock(ModBlocks.BlockSilver);
		registerBlock(ModBlocks.BlockLead);
		registerBlock(ModBlocks.BlockPlatinum);
		registerBlock(ModBlocks.BlockNickel);
		registerBlock(ModBlocks.BlockAluminium);
		registerBlock(ModBlocks.BlockSapphire);
		registerBlock(ModBlocks.BlockRuby);
		
		registerBlock(ModBlocks.StaticLamp);
		
		registerBlock(ModBlocks.LogicGateBasePlate);
		ItemRenderRegistry.registerItem(ModItems.LogicGatePowerSync);
		ItemRenderRegistry.registerItem(ModItems.InvertedLogicGatePowerSync);
		ItemRenderRegistry.registerItem(ModItems.LogicGateServo);
		
		registerBlock(ModBlocks.VacuumChest);
		registerBlock(ModBlocks.StaticChest);
		registerBlock(ModBlocks.EnergizedChest);
		registerBlock(ModBlocks.LumumChest);
		
		registerBlock(ModBlocks.ItemConduit);
		registerBlock(ModBlocks.StaticConduit);
		registerBlock(ModBlocks.FluidConduit);
		
		registerBlock(ModBlocks.ObsidianGlass);
		
		registerBlock(ModBlocks.StaticWood);
		registerBlock(ModBlocks.EnergizedWood);
		registerBlock(ModBlocks.LumumWood);
		
		ItemRenderRegistry.registerItem(ModTools.CopperAxe);
		ItemRenderRegistry.registerItem(ModTools.CopperPickaxe);
		ItemRenderRegistry.registerItem(ModTools.CopperShovel);
		ItemRenderRegistry.registerItem(ModTools.CopperHoe);
		ItemRenderRegistry.registerItem(ModTools.CopperSword);
		
		ItemRenderRegistry.registerItem(ModTools.TinAxe);
		ItemRenderRegistry.registerItem(ModTools.TinPickaxe);
		ItemRenderRegistry.registerItem(ModTools.TinShovel);
		ItemRenderRegistry.registerItem(ModTools.TinHoe);
		ItemRenderRegistry.registerItem(ModTools.TinSword);
		
		ItemRenderRegistry.registerItem(ModTools.SilverAxe);
		ItemRenderRegistry.registerItem(ModTools.SilverPickaxe);
		ItemRenderRegistry.registerItem(ModTools.SilverShovel);
		ItemRenderRegistry.registerItem(ModTools.SilverHoe);
		ItemRenderRegistry.registerItem(ModTools.SilverSword);
		
		ItemRenderRegistry.registerItem(ModTools.LeadAxe);
		ItemRenderRegistry.registerItem(ModTools.LeadPickaxe);
		ItemRenderRegistry.registerItem(ModTools.LeadShovel);
		ItemRenderRegistry.registerItem(ModTools.LeadHoe);
		ItemRenderRegistry.registerItem(ModTools.LeadSword);
		
		ItemRenderRegistry.registerItem(ModTools.PlatinumAxe);
		ItemRenderRegistry.registerItem(ModTools.PlatinumPickaxe);
		ItemRenderRegistry.registerItem(ModTools.PlatinumShovel);
		ItemRenderRegistry.registerItem(ModTools.PlatinumHoe);
		ItemRenderRegistry.registerItem(ModTools.PlatinumSword);
		
		ItemRenderRegistry.registerItem(ModTools.StaticAxe);
		ItemRenderRegistry.registerItem(ModTools.StaticPickaxe);
		ItemRenderRegistry.registerItem(ModTools.StaticShovel);
		ItemRenderRegistry.registerItem(ModTools.StaticHoe);
		ItemRenderRegistry.registerItem(ModTools.StaticSword);
		
		ItemRenderRegistry.registerItem(ModTools.EnergizedAxe);
		ItemRenderRegistry.registerItem(ModTools.EnergizedPickaxe);
		ItemRenderRegistry.registerItem(ModTools.EnergizedShovel);
		ItemRenderRegistry.registerItem(ModTools.EnergizedHoe);
		ItemRenderRegistry.registerItem(ModTools.EnergizedSword);
		
		ItemRenderRegistry.registerItem(ModTools.LumumAxe);
		ItemRenderRegistry.registerItem(ModTools.LumumPickaxe);
		ItemRenderRegistry.registerItem(ModTools.LumumShovel);
		ItemRenderRegistry.registerItem(ModTools.LumumHoe);
		ItemRenderRegistry.registerItem(ModTools.LumumSword);
		
		ItemRenderRegistry.registerItem(ModTools.SapphireAxe);
		ItemRenderRegistry.registerItem(ModTools.SapphirePickaxe);
		ItemRenderRegistry.registerItem(ModTools.SapphireShovel);
		ItemRenderRegistry.registerItem(ModTools.SapphireHoe);
		ItemRenderRegistry.registerItem(ModTools.SapphireSword);
		
		ItemRenderRegistry.registerItem(ModTools.RubyAxe);
		ItemRenderRegistry.registerItem(ModTools.RubyPickaxe);
		ItemRenderRegistry.registerItem(ModTools.RubyShovel);
		ItemRenderRegistry.registerItem(ModTools.RubyHoe);
		ItemRenderRegistry.registerItem(ModTools.RubySword);
		
		ItemRenderRegistry.registerItem(ModItems.BaseFluidCapsule);
		ItemRenderRegistry.registerItem(ModItems.StaticFluidCapsule);
		ItemRenderRegistry.registerItem(ModItems.EnergizedFluidCapsule);
		ItemRenderRegistry.registerItem(ModItems.LumumFluidCapsule);
	}
    public static void registerItem(Item item, int metadata) {
    	ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
    public static void registerItem(Item item) {
    	ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
    public static void registerBlock(Block block, int metadata) {
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), metadata, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }
    public static void registerBlock(Block block) {
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }
}
