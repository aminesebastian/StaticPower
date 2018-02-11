package theking530.staticpower.client;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.blocks.crops.ModPlants;
import theking530.staticpower.items.IVariantItem;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.armor.ModArmor;
import theking530.staticpower.items.tools.basictools.ModTools;

public class ItemRenderRegistry {
	
	public static void initItemRenderers() {
		registerItem(ModItems.PotatoFlour);
		registerItem(ModItems.WheatFlour);
		registerItem(ModItems.PotatoBread);
		
		registerItem(ModItems.ApplePie);
		registerItem(ModItems.StaticPie);
		registerItem(ModItems.EnergizedPie);
		registerItem(ModItems.LumumPie);
		
		registerItem(ModArmor.BaseShield);
		
		registerItem(ModArmor.SkeletonHelmet);
		registerItem(ModArmor.SkeletonChestplate);
		registerItem(ModArmor.SkeletonLeggings);
		registerItem(ModArmor.SkeletonBoots);
		
		registerItem(ModArmor.UndeadHelmet);
		registerItem(ModArmor.UndeadChestplate);
		registerItem(ModArmor.UndeadLeggings);
		registerItem(ModArmor.UndeadBoots);
		
		registerItem(ModArmor.StaticHelmet);
		registerItem(ModArmor.StaticChestplate);
		registerItem(ModArmor.StaticLeggings);
		registerItem(ModArmor.StaticBoots);
		
		registerItem(ModArmor.EnergizedHelmet);
		registerItem(ModArmor.EnergizedChestplate);
		registerItem(ModArmor.EnergizedLeggings);
		registerItem(ModArmor.EnergizedBoots);
		
		registerItem(ModArmor.LumumHelmet);
		registerItem(ModArmor.LumumChestplate);
		registerItem(ModArmor.LumumLeggings);
		registerItem(ModArmor.LumumBoots);
		
		registerItem(ModArmor.CopperHelmet);
		registerItem(ModArmor.CopperChestplate);
		registerItem(ModArmor.CopperLeggings);
		registerItem(ModArmor.CopperBoots);
		
		registerItem(ModArmor.TinHelmet);
		registerItem(ModArmor.TinChestplate);
		registerItem(ModArmor.TinLeggings);
		registerItem(ModArmor.TinBoots);
		
		registerItem(ModArmor.LeadHelmet);
		registerItem(ModArmor.LeadChestplate);
		registerItem(ModArmor.LeadLeggings);
		registerItem(ModArmor.LeadBoots);
		
		registerItem(ModArmor.SilverHelmet);
		registerItem(ModArmor.SilverChestplate);
		registerItem(ModArmor.SilverLeggings);
		registerItem(ModArmor.SilverBoots);
		
		registerItem(ModArmor.PlatinumHelmet);
		registerItem(ModArmor.PlatinumChestplate);
		registerItem(ModArmor.PlatinumLeggings);
		registerItem(ModArmor.PlatinumBoots);
		
		registerItem(ModArmor.AluminiumHelmet);
		registerItem(ModArmor.AluminiumChestplate);
		registerItem(ModArmor.AluminiumLeggings);
		registerItem(ModArmor.AluminiumBoots);
		
		registerItem(ModArmor.SapphireHelmet);
		registerItem(ModArmor.SapphireChestplate);
		registerItem(ModArmor.SapphireLeggings);
		registerItem(ModArmor.SapphireBoots);
		
		registerItem(ModArmor.RubyHelmet);
		registerItem(ModArmor.RubyChestplate);
		registerItem(ModArmor.RubyLeggings);
		registerItem(ModArmor.RubyBoots);

		registerItem(ModItems.SilverDust);
		registerItem(ModItems.CopperDust);
		registerItem(ModItems.IronDust);
		registerItem(ModItems.GoldDust);
		registerItem(ModItems.SilverDust);
		registerItem(ModItems.TinDust);
		registerItem(ModItems.LeadDust);
		registerItem(ModItems.PlatinumDust);
		registerItem(ModItems.NickelDust);
		registerItem(ModItems.AluminiumDust);
		registerItem(ModItems.StaticDust);
		registerItem(ModItems.EnergizedDust);
		registerItem(ModItems.LumumDust);
		registerItem(ModItems.InertInfusionBlend);
		registerItem(ModItems.RedstoneAlloyDust);
		
		registerItem(ModItems.StaticNugget);
		registerItem(ModItems.EnergizedNugget);
		registerItem(ModItems.LumumNugget);

		registerItem(ModItems.EnergizedInfusionBlend);
		registerItem(ModItems.LumumInfusionBlend);	
		registerItem(ModItems.EnergizedEnergyCrystal);
		registerItem(ModItems.LumumEnergyCrystal);
		registerItem(ModItems.CopperCoil);
		registerItem(ModItems.SilverCoil);
		registerItem(ModItems.GoldCoil);
		registerItem(ModItems.CopperWire);
		registerItem(ModItems.SilverWire);
		registerItem(ModItems.GoldWire);
		registerItem(ModItems.IronPlate);
		registerItem(ModItems.CopperPlate);
		registerItem(ModItems.TinPlate);
		registerItem(ModItems.SilverPlate);
		registerItem(ModItems.GoldPlate);
		registerItem(ModItems.LeadPlate);
		registerItem(ModItems.StaticPlate);
		registerItem(ModItems.EnergizedPlate);
		registerItem(ModItems.LumumPlate);
		
		registerItem(ModItems.PlateMould);
		registerItem(ModItems.WireMould);
		
		registerItem(ModItems.SilverIngot);
		registerItem(ModItems.CopperIngot);
		registerItem(ModItems.TinIngot);
		registerItem(ModItems.LeadIngot);
		registerItem(ModItems.PlatinumIngot);
		registerItem(ModItems.StaticIngot);
		registerItem(ModItems.EnergizedIngot);
		registerItem(ModItems.LumumIngot);
		registerItem(ModItems.InertIngot);
		registerItem(ModItems.RedstoneAlloyIngot);
		registerItem(ModItems.NickelIngot);
		registerItem(ModItems.AluminiumIngot);
		registerItem(ModItems.SapphireGem);
		registerItem(ModItems.RubyGem);
		
		registerItem(ModItems.StaticWrench);
		registerItem(ModItems.SolderingIron);
		registerItem(ModItems.CoordinateMarker);
		registerItem(ModItems.StaticBook);
		registerItem(ModItems.WireCutters);
		registerItem(ModItems.MetalHammer);
		registerItem(ModItems.ElectricSolderingIron);
		
		registerItem(ModItems.BasicCircuit);
		registerItem(ModItems.StaticCircuit);
		registerItem(ModItems.EnergizedCircuit);
		registerItem(ModItems.LumumCircuit);
		
		registerItem(ModItems.BasicCircuit);
		registerItem(ModItems.StaticCircuit);
		registerItem(ModItems.EnergizedCircuit);
		registerItem(ModItems.LumumCircuit);
		
		registerItem(ModItems.BasicBattery);
		registerItem(ModItems.StaticBattery);
		registerItem(ModItems.EnergizedBattery);
		registerItem(ModItems.LumumBattery);
		//FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler((IItemColor)ModItems.BasicBattery, 
				//ModItems.BasicBattery, ModItems.StaticBattery, ModItems.EnergizedBattery, ModItems.LumumBattery);
		
		registerItem(ModItems.Rubber);
		registerItem(ModItems.IOPort);
		
		registerItem(ModItems.ExperienceVacuumUpgrade);
		registerItem(ModItems.TeleportUpgrade);
		registerItem(ModItems.BasicPowerUpgrade);
		registerItem(ModItems.BasicSpeedUpgrade);
		registerItem(ModItems.BasicTankUpgrade);
		registerItem(ModItems.BasicRangeUpgrade);
		registerItem(ModItems.StaticPowerUpgrade);
		registerItem(ModItems.StaticSpeedUpgrade);
		registerItem(ModItems.StaticTankUpgrade);
		registerItem(ModItems.StaticQuarryingUpgrade);
		registerItem(ModItems.StaticRangeUpgrade);
		registerItem(ModItems.EnergizedPowerUpgrade);
		registerItem(ModItems.EnergizedSpeedUpgrade);
		registerItem(ModItems.EnergizedTankUpgrade);
		registerItem(ModItems.EnergizedQuarryingUpgrade);
		registerItem(ModItems.EnergizedRangeUpgrade);
		registerItem(ModItems.LumumPowerUpgrade);
		registerItem(ModItems.LumumSpeedUpgrade);
		registerItem(ModItems.LumumTankUpgrade);
		registerItem(ModItems.LumumQuarryingUpgrade);
		registerItem(ModItems.LumumRangeUpgrade);
		registerItem(ModItems.BasicUpgradePlate);
		registerItem(ModItems.StaticUpgradePlate);
		registerItem(ModItems.EnergizedUpgradePlate);
		registerItem(ModItems.LumumUpgradePlate);
		
		registerItem(ModItems.BasicOutputMultiplierUpgrade);
		registerItem(ModItems.StaticOutputMultiplierUpgrade);
		registerItem(ModItems.EnergizedOutputMultiplierUpgrade);
		registerItem(ModItems.LumumOutputMultiplierUpgrade);
		
		registerItem(ModPlants.StaticCrop);
		registerItem(ModPlants.EnergizedCrop);
		registerItem(ModPlants.LumumCrop);
		registerItem(ModPlants.DepletedCrop);
		registerItem(ModPlants.StaticSeeds);
		registerItem(ModPlants.EnergizedSeeds);
		registerItem(ModPlants.LumumSeeds);
		
		registerItem(ModItems.BasicItemFilter);
		registerItem(ModItems.UpgradedItemFilter);
		registerItem(ModItems.AdvancedItemFilter);

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
		
		registerBlock(ModBlocks.Digistore);
		
		registerBlock(ModBlocks.StaticGrass);
		registerBlock(ModBlocks.EnergizedGrass);
		
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
		registerBlock(ModBlocks.EnergizedLamp);
		registerBlock(ModBlocks.LumumLamp);
		
		registerBlock(ModBlocks.LogicGateBasePlate);
		
		registerItem(ModItems.LogicGatePowerSync);
		registerItem(ModItems.InvertedLogicGatePowerSync);
		registerItem(ModItems.LogicGateServo);
		registerItem(ModItems.Transistor);
		registerItem(ModItems.Silicon);
		registerItem(ModItems.Diode);
		registerItem(ModItems.MemoryChip);
		registerItem(ModItems.InternalClock);
		
		registerTemporaryLogicGate(ModBlocks.Adder);
		registerTemporaryLogicGate(ModBlocks.Subtractor);
		registerTemporaryLogicGate(ModBlocks.Timer);
		registerTemporaryLogicGate(ModBlocks.And);
		registerTemporaryLogicGate(ModBlocks.Or);
		registerTemporaryLogicGate(ModBlocks.SignalMultiplier);
		registerTemporaryLogicGate(ModBlocks.PowerCell);
		registerTemporaryLogicGate(ModBlocks.LED);
		registerTemporaryLogicGate(ModBlocks.NotGate);
		
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
		
		registerItem(ModTools.CopperAxe);
		registerItem(ModTools.CopperPickaxe);
		registerItem(ModTools.CopperShovel);
		registerItem(ModTools.CopperHoe);
		registerItem(ModTools.CopperSword);
		
		registerItem(ModTools.TinAxe);
		registerItem(ModTools.TinPickaxe);
		registerItem(ModTools.TinShovel);
		registerItem(ModTools.TinHoe);
		registerItem(ModTools.TinSword);
		
		registerItem(ModTools.SilverAxe);
		registerItem(ModTools.SilverPickaxe);
		registerItem(ModTools.SilverShovel);
		registerItem(ModTools.SilverHoe);
		registerItem(ModTools.SilverSword);
		
		registerItem(ModTools.LeadAxe);
		registerItem(ModTools.LeadPickaxe);
		registerItem(ModTools.LeadShovel);
		registerItem(ModTools.LeadHoe);
		registerItem(ModTools.LeadSword);
		
		registerItem(ModTools.PlatinumAxe);
		registerItem(ModTools.PlatinumPickaxe);
		registerItem(ModTools.PlatinumShovel);
		registerItem(ModTools.PlatinumHoe);
		registerItem(ModTools.PlatinumSword);
		
		registerItem(ModTools.StaticAxe);
		registerItem(ModTools.StaticPickaxe);
		registerItem(ModTools.StaticShovel);
		registerItem(ModTools.StaticHoe);
		registerItem(ModTools.StaticSword);
		
		registerItem(ModTools.EnergizedAxe);
		registerItem(ModTools.EnergizedPickaxe);
		registerItem(ModTools.EnergizedShovel);
		registerItem(ModTools.EnergizedHoe);
		registerItem(ModTools.EnergizedSword);
		
		registerItem(ModTools.LumumAxe);
		registerItem(ModTools.LumumPickaxe);
		registerItem(ModTools.LumumShovel);
		registerItem(ModTools.LumumHoe);
		registerItem(ModTools.LumumSword);
		
		registerItem(ModTools.SapphireAxe);
		registerItem(ModTools.SapphirePickaxe);
		registerItem(ModTools.SapphireShovel);
		registerItem(ModTools.SapphireHoe);
		registerItem(ModTools.SapphireSword);
		
		registerItem(ModTools.RubyAxe);
		registerItem(ModTools.RubyPickaxe);
		registerItem(ModTools.RubyShovel);
		registerItem(ModTools.RubyHoe);
		registerItem(ModTools.RubySword);		

	    registerCannister(ModItems.BaseFluidCapsule, "basefluidcapsule");
	    registerCannister(ModItems.StaticFluidCapsule, "staticfluidcapsule");
	    registerCannister(ModItems.EnergizedFluidCapsule, "energizedfluidcapsule");
	    registerCannister(ModItems.LumumFluidCapsule, "lumumfluidcapsule");
	    
	    registerItemWithVariants(ModItems.DigistoreCapacityUpgrade);
	    registerItemWithVariants(ModItems.DigistoreMiscUpgrade);
	}
    public static void registerCannister(Item item, String blockstate) {
    	ModelResourceLocation location =  new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, blockstate), "inventory");
		ModelLoader.setCustomMeshDefinition(item, stack -> location);
	    ModelBakery.registerItemVariants(item, location);
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
    public static void registerTemporaryLogicGate(Block block) {
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModBlocks.LogicGateBasePlate.getRegistryName(), "inventory"));
    }
    
    public static void registerItemWithVariants(Item baseItem) {
    	if(baseItem instanceof IVariantItem) {
    		IVariantItem variantItem = (IVariantItem)baseItem;
        	Map<ItemStack, String> subItems = variantItem.getSubItemMap();
        	for(Entry<ItemStack, String> entry : subItems.entrySet()) {
        		registerItemModelForMeta(baseItem, entry.getKey().getMetadata(), "model" + "=" + entry.getValue());
        	}
    	}
	}
	private static void registerItemModelForMeta(Item item, int metadata, String variant) {
		ModelResourceLocation res = new ModelResourceLocation(item.getRegistryName(), variant);
		registerItemModelForMeta(item, metadata, res);
	}
	private static void registerItemModelForMeta(Item item, int metadata, ModelResourceLocation modelResourceLocation) {
		ModelLoader.setCustomModelResourceLocation(item, metadata, modelResourceLocation);
	}
}
