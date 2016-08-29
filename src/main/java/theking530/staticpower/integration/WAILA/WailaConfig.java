package theking530.staticpower.integration.WAILA;


import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import theking530.staticpower.machines.basicfarmer.TileEntityBasicFarmer;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBasicBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityEnergizedBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityLumumBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityStaticBattery;
import theking530.staticpower.machines.cropsqueezer.TileEntityCropSqueezer;
import theking530.staticpower.machines.fermenter.TileEntityFermenter;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;
import theking530.staticpower.machines.fusionfurnace.TileEntityFusionFurnace;
import theking530.staticpower.machines.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.machines.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.machines.quarry.TileEntityQuarry;
import theking530.staticpower.machines.solarpanel.TileEntityBasicSolarPanel;
import theking530.staticpower.machines.solarpanel.TileEntityCreativeSolarPanel;
import theking530.staticpower.machines.solarpanel.TileEntityEnergizedSolarPanel;
import theking530.staticpower.machines.solarpanel.TileEntityLumumSolarPanel;
import theking530.staticpower.machines.solarpanel.TileEntityStaticSolarPanel;

public class WailaConfig {

	public static void callbackRegister(IWailaRegistrar registrar) {
		registrar.registerBodyProvider(new WailaBatteryProvider(), TileEntityBasicBattery.class);
		registrar.registerBodyProvider(new WailaBatteryProvider(), TileEntityStaticBattery.class);
		registrar.registerBodyProvider(new WailaBatteryProvider(), TileEntityEnergizedBattery.class);
		registrar.registerBodyProvider(new WailaBatteryProvider(), TileEntityLumumBattery.class);
		
		registrar.registerBodyProvider(new WailaBasicMachineProvider(), TileEntityPoweredGrinder.class);
		registrar.registerBodyProvider(new WailaBasicMachineProvider(), TileEntityFusionFurnace.class);
		registrar.registerBodyProvider(new WailaBasicMachineProvider(), TileEntityPoweredFurnace.class);
		registrar.registerBodyProvider(new WailaBasicMachineProvider(), TileEntityQuarry.class);
		registrar.registerBodyProvider(new WailaBasicMachineProvider(), TileEntityBasicFarmer.class);
		registrar.registerBodyProvider(new WailaBasicTankMachineProvider(), TileEntityCropSqueezer.class);
		registrar.registerBodyProvider(new WailaBasicTankMachineProvider(), TileEntityFluidInfuser.class);
		registrar.registerBodyProvider(new WailaBasicTankMachineProvider(), TileEntityFermenter.class);
		registrar.registerBodyProvider(new WailaFluidGeneratorProvider(), TileEntityFluidGenerator.class);
		
		registrar.registerBodyProvider(new WailaSolarPanelProvider(), TileEntityBasicSolarPanel.class);
		registrar.registerBodyProvider(new WailaSolarPanelProvider(), TileEntityStaticSolarPanel.class);
		registrar.registerBodyProvider(new WailaSolarPanelProvider(), TileEntityEnergizedSolarPanel.class);
		registrar.registerBodyProvider(new WailaSolarPanelProvider(), TileEntityLumumSolarPanel.class);
		registrar.registerBodyProvider(new WailaSolarPanelProvider(), TileEntityCreativeSolarPanel.class);
	}
	public static void callIMC() {
		FMLInterModComms.sendMessage("Waila", "register", WailaConfig.class.getName() + ".callbackRegister");
	}
}
