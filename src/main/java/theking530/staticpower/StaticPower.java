package theking530.staticpower;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.blocks.crops.ModPlants;
import theking530.staticpower.client.CommonProxy;
import theking530.staticpower.conduits.fluidconduit.TileEntityFluidConduit;
import theking530.staticpower.conduits.itemconduit.TileEntityItemConduit;
import theking530.staticpower.conduits.staticconduit.TileEntityStaticConduit;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.GuiHandler;
import theking530.staticpower.handlers.OreDictionaryRegistration;
import theking530.staticpower.handlers.OreGenerationHandler;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.handlers.crafting.recipes.CondenserRecipes;
import theking530.staticpower.handlers.crafting.recipes.DistilleryRecipes;
import theking530.staticpower.handlers.crafting.recipes.EsotericEnchanterRecipes;
import theking530.staticpower.handlers.crafting.recipes.FermenterRecipes;
import theking530.staticpower.handlers.crafting.recipes.FluidGeneratorRecipes;
import theking530.staticpower.handlers.crafting.recipes.FormerRecipes;
import theking530.staticpower.handlers.crafting.recipes.FusionRecipes;
import theking530.staticpower.handlers.crafting.recipes.GrinderRecipes;
import theking530.staticpower.handlers.crafting.recipes.InfuserRecipes;
import theking530.staticpower.handlers.crafting.recipes.ShapedRecipes;
import theking530.staticpower.handlers.crafting.recipes.ShaplessRecipes;
import theking530.staticpower.handlers.crafting.recipes.SmeltingRecipes;
import theking530.staticpower.handlers.crafting.recipes.SolderingRecipes;
import theking530.staticpower.handlers.crafting.recipes.SqueezerRecipes;
import theking530.staticpower.handlers.crafting.recipes.ToolRecipes;
import theking530.staticpower.integration.ICompatibilityPlugin;
import theking530.staticpower.integration.theoneprobe.PluginTOP;
import theking530.staticpower.integration.thermalfoundation.PluginThermalFoundation;
import theking530.staticpower.integration.tinkersconstruct.PluginTinkersConstruct;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.ModMaterials;
import theking530.staticpower.items.armor.ModArmor;
import theking530.staticpower.items.tools.basictools.ModTools;
import theking530.staticpower.logic.gates.adder.TileEntityAdder;
import theking530.staticpower.logic.gates.and.TileEntityAndGate;
import theking530.staticpower.logic.gates.led.TileEntityLED;
import theking530.staticpower.logic.gates.notgate.TileEntityNotGate;
import theking530.staticpower.logic.gates.or.TileEntityOrGate;
import theking530.staticpower.logic.gates.powercell.TileEntityPowerCell;
import theking530.staticpower.logic.gates.subtractor.TileEntitySubtractorGate;
import theking530.staticpower.logic.gates.timer.TileEntityTimer;
import theking530.staticpower.logic.gates.transducer.TileEntitySignalMultiplier;
import theking530.staticpower.machines.basicfarmer.TileEntityBasicFarmer;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBasicBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityEnergizedBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityLumumBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityStaticBattery;
import theking530.staticpower.machines.chargingstation.TileEntityChargingStation;
import theking530.staticpower.machines.condenser.TileEntityCondenser;
import theking530.staticpower.machines.cropsqueezer.TileEntityCropSqueezer;
import theking530.staticpower.machines.distillery.TileEntityDistillery;
import theking530.staticpower.machines.esotericenchanter.TileEsotericEnchanter;
import theking530.staticpower.machines.fermenter.TileEntityFermenter;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;
import theking530.staticpower.machines.former.TileEntityFormer;
import theking530.staticpower.machines.fusionfurnace.TileEntityFusionFurnace;
import theking530.staticpower.machines.heatingelement.TileEntityHeatingElement;
import theking530.staticpower.machines.mechanicalsqueezer.TileEntityMechanicalSqueezer;
import theking530.staticpower.machines.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.machines.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.machines.quarry.TileEntityQuarry;
import theking530.staticpower.machines.treefarmer.TileTreeFarmer;
import theking530.staticpower.potioneffects.ModPotions;
import theking530.staticpower.tileentity.chest.energizedchest.TileEntityEnergizedChest;
import theking530.staticpower.tileentity.chest.lumumchest.TileEntityLumumChest;
import theking530.staticpower.tileentity.chest.staticchest.TileEntityStaticChest;
import theking530.staticpower.tileentity.chunkloader.TileEntityChunkLoader;
import theking530.staticpower.tileentity.digistorenetwork.digistore.TileEntityDigistore;
import theking530.staticpower.tileentity.digistorenetwork.ioport.TileEntityDigistoreIOPort;
import theking530.staticpower.tileentity.digistorenetwork.manager.TileEntityDigistoreManager;
import theking530.staticpower.tileentity.solarpanels.TileEntityBasicSolarPanel;
import theking530.staticpower.tileentity.solarpanels.TileEntityCreativeSolarPanel;
import theking530.staticpower.tileentity.solarpanels.TileEntityEnergizedSolarPanel;
import theking530.staticpower.tileentity.solarpanels.TileEntityLumumSolarPanel;
import theking530.staticpower.tileentity.solarpanels.TileEntityStaticSolarPanel;
import theking530.staticpower.tileentity.solderingtable.TileEntitySolderingTable;
import theking530.staticpower.tileentity.vacuumchest.TileEntityVacuumChest;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "after:thermalfoundation;")

public class StaticPower {
	
    @Instance("staticpower")
    public static StaticPower staticpower;
    
    @SidedProxy(clientSide="theking530.staticpower.client.ClientProxy", serverSide="theking530.staticpower.client.CommonProxy")
    public static CommonProxy proxy;
    
    public static CreativeTabs StaticPower = new CreativeTabStandard(CreativeTabs.getNextID(), "StaticPower");  
    public static Configuration CONFIG;
    public static org.apache.logging.log4j.Logger LOGGER;
    public static Registry REGISTRY;
    
    public static List<ICompatibilityPlugin> plugins = new ArrayList<ICompatibilityPlugin>();
    
    @Mod.Instance(Reference.MOD_ID)
    public static StaticPower instance;
    
	static {
		FluidRegistry.enableUniversalBucket();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent preEvent){
		LOGGER = preEvent.getModLog();
		REGISTRY = new Registry();
		REGISTRY.preInit(preEvent);
		CONFIG = new Configuration(preEvent.getSuggestedConfigurationFile());
		StaticPowerConfig.updateConfig();
	
		PacketHandler.initPackets();
		ModFluids.init(REGISTRY);
		ModItems.init(REGISTRY);
		ModMaterials.init();
		ModBlocks.init(REGISTRY);
		ModTools.init(REGISTRY);
		ModPlants.init(REGISTRY);
		ModArmor.init(REGISTRY);
		ModPotions.init();
		
	    OreGenerationHandler.intialize();
	    CommonProxy.preInit();

	    loadCompatibilityPlugins();
	    
	    

		GameRegistry.registerTileEntity(TileEntityChunkLoader.class, "BaseChunkLoader");
		
		GameRegistry.registerTileEntity(TileEntitySolderingTable.class, "SolderingTable");	
		GameRegistry.registerTileEntity(TileEntityFluidInfuser.class, "FluidInfuser");
		GameRegistry.registerTileEntity(TileEntityFluidGenerator.class, "FluidGenerator");
		GameRegistry.registerTileEntity(TileEntityCropSqueezer.class, "CropSqueezer");
		GameRegistry.registerTileEntity(TileEntityPoweredGrinder.class, "PoweredGrinder");
		GameRegistry.registerTileEntity(TileEntityPoweredFurnace.class, "PoweredSmelter");
		GameRegistry.registerTileEntity(TileEntityStaticConduit.class, "StaticConduit");
		GameRegistry.registerTileEntity(TileEntityFluidConduit.class, "FluidConduit");
		GameRegistry.registerTileEntity(TileEntityItemConduit.class, "ItemConduit");
		GameRegistry.registerTileEntity(TileEntityFusionFurnace.class, "FusionFurnace");
		GameRegistry.registerTileEntity(TileEntityQuarry.class, "Quarry");
		GameRegistry.registerTileEntity(TileEntityBasicFarmer.class, "BasicFarmer");
		GameRegistry.registerTileEntity(TileEntityChargingStation.class, "ChargingStation");
		GameRegistry.registerTileEntity(TileEntityMechanicalSqueezer.class, "MechanicalSqueezer");
		GameRegistry.registerTileEntity(TileEntityFermenter.class, "Fermenter");
		GameRegistry.registerTileEntity(TileEntityHeatingElement.class, "HeatingElement");
		GameRegistry.registerTileEntity(TileEntityDistillery.class, "Distillery");
		GameRegistry.registerTileEntity(TileEntityCondenser.class, "Condenser");
		GameRegistry.registerTileEntity(TileEntityFormer.class, "Former");
		GameRegistry.registerTileEntity(TileTreeFarmer.class, "TreeFarmer");
		
		GameRegistry.registerTileEntity(TileEntityBasicSolarPanel.class, "BaseSolarPanel");
		GameRegistry.registerTileEntity(TileEntityStaticSolarPanel.class, "StaticSolarPanel");
		GameRegistry.registerTileEntity(TileEntityEnergizedSolarPanel.class, "EnergizedSolarPanel");
		GameRegistry.registerTileEntity(TileEntityLumumSolarPanel.class, "LumumSolarPanel");
		GameRegistry.registerTileEntity(TileEntityCreativeSolarPanel.class, "CreativeSolarPanel");	
		
		GameRegistry.registerTileEntity(TileEntityBasicBattery.class, "BasicBattery");
		GameRegistry.registerTileEntity(TileEntityStaticBattery.class, "StaticBattery");
		GameRegistry.registerTileEntity(TileEntityEnergizedBattery.class, "EnergizedBattery");
		GameRegistry.registerTileEntity(TileEntityLumumBattery.class, "LumumBattery");


		GameRegistry.registerTileEntity(TileEntitySignalMultiplier.class, "SignalMultiplier");
		GameRegistry.registerTileEntity(TileEntityNotGate.class, "NotGate");
		GameRegistry.registerTileEntity(TileEntityPowerCell.class, "PowerCell");
		GameRegistry.registerTileEntity(TileEntityTimer.class, "Timer");
		GameRegistry.registerTileEntity(TileEntityAdder.class, "Adder");
		GameRegistry.registerTileEntity(TileEntityAndGate.class, "And");
		GameRegistry.registerTileEntity(TileEntityOrGate.class, "Or");
		GameRegistry.registerTileEntity(TileEntitySubtractorGate.class, "Subtractor");
		GameRegistry.registerTileEntity(TileEntityLED.class, "LED");
		
		GameRegistry.registerTileEntity(TileEntityStaticChest.class, "StaticChest");
		GameRegistry.registerTileEntity(TileEntityEnergizedChest.class, "EnergizedChest");
		GameRegistry.registerTileEntity(TileEntityLumumChest.class, "LumumChest");
		GameRegistry.registerTileEntity(TileEntityVacuumChest.class, "VacuumChest");
		
		GameRegistry.registerTileEntity(TileEsotericEnchanter.class, "Esoteric Enchanter");
		
		GameRegistry.registerTileEntity(TileEntityDigistore.class, "Digistore");
		GameRegistry.registerTileEntity(TileEntityDigistoreManager.class, "DigistoreManager");
		GameRegistry.registerTileEntity(TileEntityDigistoreIOPort.class, "DigistoreIOPort");
	}	
	@EventHandler
	public void Init(FMLInitializationEvent Event){
		proxy.registerProxies();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());	
		
		OreDictionaryRegistration.registerOres();
		ShapedRecipes.registerFullRecipes();   
		ShaplessRecipes.registerFullRecipes(); 
		SmeltingRecipes.registerFullRecipes();
		FusionRecipes.registerFusionRecipes();
		SolderingRecipes.registerSolderingRecipes();
		GrinderRecipes.registerGrinderRecipes();
		InfuserRecipes.registerInfusionRecipes();
		SqueezerRecipes.registerSqueezingRecipes();
		FermenterRecipes.registerFermenterRecipes();
		FluidGeneratorRecipes.registerFluidGeneratorRecipes();
		CondenserRecipes.registerCondenserRecipes();
		DistilleryRecipes.registerDistilleryRecipes();
		FormerRecipes.registerFusionRecipes();
		EsotericEnchanterRecipes.registerEsotericEnchanterRecipes();
		ToolRecipes.registerToolRecipes();
		
	}
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(Reference.MOD_ID)) {
			StaticPowerConfig.updateConfig();
		}
	}
	
	
    @Mod.EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {

    }
	public void loadCompatibilityPlugins() {
	    Loader.instance();

		plugins.add(new PluginTOP());
		plugins.add(new PluginTinkersConstruct());
		plugins.add(new PluginThermalFoundation());
		
		for(ICompatibilityPlugin plugin : plugins) {
			if(plugin.shouldRegister()) {
		        try {
		        	plugin.register();
					LOGGER.log(Level.INFO, "Static Power: Loading " + plugin.getPluginName() + " compatibility plugin.");
		        } catch (Exception e) {
					LOGGER.log(Level.INFO, "Static Power: Error while loading " + plugin.getPluginName() + " compatibility plugin.");
	                e.printStackTrace(System.err);
	            }
			}
		}
		
	}
}
	



