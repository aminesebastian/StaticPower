package theking530.staticpower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import theking530.staticpower.blocks.crops.ModPlants;
import theking530.staticpower.client.CommonProxy;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.GuiHandler;
import theking530.staticpower.handlers.OreDictionaryRegistration;
import theking530.staticpower.handlers.OreGenerationHandler;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModCraftingRecipes;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.initialization.ModMaterials;
import theking530.staticpower.initialization.ModTileEntities;
import theking530.staticpower.integration.ICompatibilityPlugin;
import theking530.staticpower.integration.theoneprobe.PluginTOP;
import theking530.staticpower.integration.thermalfoundation.PluginThermalFoundation;
import theking530.staticpower.integration.tinkersconstruct.PluginTinkersConstruct;
import theking530.staticpower.items.armor.ModArmor;
import theking530.staticpower.items.tools.basictools.ModTools;
import theking530.staticpower.potioneffects.ModPotions;
import theking530.staticpower.utilities.MaterialSets;
import theking530.staticpower.utilities.Reference;

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
    
    public static Map<String, Class<? extends ICompatibilityPlugin>> plugins = new HashMap<String,  Class<? extends ICompatibilityPlugin>>();
    public static List<ICompatibilityPlugin> registeredPlugins = new ArrayList<ICompatibilityPlugin>();
    
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

	    ModTileEntities.init();
		
		
		pluginsPreInit();
	}	
	
	@EventHandler
	public void Init(FMLInitializationEvent Event){
		proxy.registerProxies();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());		
		MaterialSets.initialize();
		
		OreDictionaryRegistration.registerOres();
		
		pluginsInit();
	}
	@EventHandler
	public void PostInit(FMLPostInitializationEvent Event){
		ModCraftingRecipes.init();
		pluginsPostInit();
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

		plugins.put("theoneprobe", PluginTOP.class);
		plugins.put("tconstruct", PluginTinkersConstruct.class);
		plugins.put("thermalfoundation", PluginThermalFoundation.class);
		
		for(Entry<String,  Class<? extends ICompatibilityPlugin>> pluginEntry : plugins.entrySet()) {
			if(!Loader.isModLoaded(pluginEntry.getKey())){
				continue;
			}
			try {
				ICompatibilityPlugin plugin = pluginEntry.getValue().newInstance();
				if(plugin.shouldRegister()) {
		        	plugin.register();
					LOGGER.log(Level.INFO, "Loading " + plugin.getPluginName() + " compatibility plugin.");
	                registeredPlugins.add(plugin);
				}
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, "Error while loading compatibility plugin for mod" + pluginEntry.getKey() + " compatibility plugin.");
				LOGGER.log(Level.ERROR, e.getMessage());
			}	
		}	
	}
	public void pluginsPreInit() {
		for(ICompatibilityPlugin plugin : registeredPlugins) {
			if(!plugin.isRegistered()) {
				continue;
			}
			plugin.preInit();
			LOGGER.log(Level.INFO, "Completed Pre Init for " + plugin.getPluginName() + " compatibility plugin.");
		}
	}
	public void pluginsInit() {
		for(ICompatibilityPlugin plugin : registeredPlugins) {
			if(!plugin.isRegistered()) {
				continue;
			}
			plugin.init();
			LOGGER.log(Level.INFO, "Completed Init for " + plugin.getPluginName() + " compatibility plugin.");
		}
	}
	public void pluginsPostInit() {
		for(ICompatibilityPlugin plugin : registeredPlugins) {
			if(!plugin.isRegistered()) {
				continue;
			}
			plugin.postInit();
			LOGGER.log(Level.INFO, "Completed Post Init for " + plugin.getPluginName() + " compatibility plugin.");
		}
	}
}
	



