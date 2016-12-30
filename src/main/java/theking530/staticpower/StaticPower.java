package theking530.staticpower;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.CommonProxy;
import theking530.staticpower.client.ItemRenderRegistry;
import theking530.staticpower.conduits.fluidconduit.TileEntityFluidConduit;
import theking530.staticpower.conduits.itemconduit.TileEntityItemConduit;
import theking530.staticpower.conduits.staticconduit.TileEntityStaticConduit;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.GuiHandler;
import theking530.staticpower.handlers.OreGenerationHandler;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.handlers.crafting.recipes.FermenterRecipes;
import theking530.staticpower.handlers.crafting.recipes.FusionRecipes;
import theking530.staticpower.handlers.crafting.recipes.GrinderRecipes;
import theking530.staticpower.handlers.crafting.recipes.InfuserRecipes;
import theking530.staticpower.handlers.crafting.recipes.ShapedRecipes;
import theking530.staticpower.handlers.crafting.recipes.ShaplessRecipes;
import theking530.staticpower.handlers.crafting.recipes.SmeltingRecipes;
import theking530.staticpower.handlers.crafting.recipes.SolderingRecipes;
import theking530.staticpower.handlers.crafting.recipes.SqueezerRecipes;
import theking530.staticpower.integration.TIC.TinkersIMC;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.machines.basicfarmer.TileEntityBasicFarmer;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBasicBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityEnergizedBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityLumumBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityStaticBattery;
import theking530.staticpower.machines.chargingstation.TileEntityChargingStation;
import theking530.staticpower.machines.condenser.TileEntityCondenser;
import theking530.staticpower.machines.cropsqueezer.TileEntityCropSqueezer;
import theking530.staticpower.machines.distillery.TileEntityDistillery;
import theking530.staticpower.machines.fermenter.TileEntityFermenter;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;
import theking530.staticpower.machines.fusionfurnace.TileEntityFusionFurnace;
import theking530.staticpower.machines.heatingelement.TileEntityHeatingElement;
import theking530.staticpower.machines.mechanicalsqueezer.TileEntityMechanicalSqueezer;
import theking530.staticpower.machines.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.machines.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.machines.quarry.TileEntityQuarry;
import theking530.staticpower.machines.solarpanel.TileEntityBasicSolarPanel;
import theking530.staticpower.machines.solarpanel.TileEntityCreativeSolarPanel;
import theking530.staticpower.machines.solarpanel.TileEntityEnergizedSolarPanel;
import theking530.staticpower.machines.solarpanel.TileEntityLumumSolarPanel;
import theking530.staticpower.machines.solarpanel.TileEntityStaticSolarPanel;
import theking530.staticpower.machines.solderingtable.TileEntitySolderingTable;
import theking530.staticpower.newconduits.BaseConduitTileEntity;
import theking530.staticpower.tileentity.chest.energizedchest.TileEntityEnergizedChest;
import theking530.staticpower.tileentity.chest.lumumchest.TileEntityLumumChest;
import theking530.staticpower.tileentity.chest.staticchest.TileEntityStaticChest;
import theking530.staticpower.tileentity.gates.adder.TileEntityAdder;
import theking530.staticpower.tileentity.gates.and.TileEntityAndGate;
import theking530.staticpower.tileentity.gates.led.TileEntityLED;
import theking530.staticpower.tileentity.gates.notgate.TileEntityNotGate;
import theking530.staticpower.tileentity.gates.or.TileEntityOrGate;
import theking530.staticpower.tileentity.gates.powercell.TileEntityPowerCell;
import theking530.staticpower.tileentity.gates.subtractor.TileEntitySubtractorGate;
import theking530.staticpower.tileentity.gates.timer.TileEntityTimer;
import theking530.staticpower.tileentity.gates.transducer.TileEntitySignalMultiplier;
import theking530.staticpower.tileentity.vacuumchest.TileEntityVacuumChest;

@Mod(modid = Reference.MODID, name = Reference.name, version = Reference.VERSION)

public class StaticPower {
	
    @Instance("staticpower")
    public static StaticPower staticpower;
    
    @SidedProxy(clientSide="theking530.staticpower.client.ClientProxy", serverSide="theking530.staticpower.client.CommonProxy")
    public static CommonProxy proxy;
    
    public static CreativeTabs StaticPower = new CreativeTabStandard(CreativeTabs.getNextID(), "StaticPower");  
    public static Configuration config;
    public static Logger logger = FMLLog.getLogger();
    
    @Mod.Instance(Reference.MODID)
    public static StaticPower instance;
    
	static {
		FluidRegistry.enableUniversalBucket();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent preEvent){
		config = new Configuration(preEvent.getSuggestedConfigurationFile());
		StaticPowerConfig.updateConfig();
	
		PacketHandler.initPackets();
		ModBlocks.init();
		ModFluids.init();
		ModItems.init();
	    OreGenerationHandler.intialize();
	    CommonProxy.preInit();
	    if (Loader.instance().isModLoaded("tconstruct")) {
	        try {
        		TinkersIMC.initialize();
        		logger.log(Level.INFO, "Loaded Tinkers' Construct addon");
	        }catch (Exception e) {
	        	logger.log(Level.WARN, "Could not load Tinkers' Construct addon");
                e.printStackTrace(System.err);
            }
        }
	    
	    
		GameRegistry.registerTileEntity(BaseConduitTileEntity.class, "BaseConduitTileEntity");
		GameRegistry.registerTileEntity(BaseConduitTileEntity.class, "BaseChunkLoader");
		
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
		
	}	
	@EventHandler
	public void Init(FMLInitializationEvent Event){
		proxy.registerProxies();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());	
		
	
		ShapedRecipes.registerFullRecipes();   
		ShaplessRecipes.registerFullRecipes(); 
		SmeltingRecipes.registerFullRecipes();
		FusionRecipes.registerFusionRecipes();
		SolderingRecipes.registerSolderingRecipes();
		GrinderRecipes.registerGrinderRecipes();
		InfuserRecipes.registerInfusionRecipes();
		SqueezerRecipes.registerSqueezingRecipes();
		FermenterRecipes.registerFermenterRecipes();
	}
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(Reference.MODID)) {
			StaticPowerConfig.updateConfig();
		}
	}
}
	



