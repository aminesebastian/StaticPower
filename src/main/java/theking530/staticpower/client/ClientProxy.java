package theking530.staticpower.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.client.render.conduit.TileEntityRenderFluidConduit;
import theking530.staticpower.client.render.conduit.TileEntityRenderItemConduit;
import theking530.staticpower.client.render.conduit.TileEntityRenderStaticConduit;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderBattery;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderChargingStation;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderChest;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderCondenser;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderCropSqueezer;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderDistillery;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderFermenter;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderFluidGenerator;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderFluidInfuser;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderFusionFurnace;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderMechanicalSqueezer;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderPoweredFurnace;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderPoweredGrinder;
import theking530.staticpower.client.render.tileentitys.logicgates.TileEntityRenderAdder;
import theking530.staticpower.client.render.tileentitys.logicgates.TileEntityRenderAndGate;
import theking530.staticpower.client.render.tileentitys.logicgates.TileEntityRenderLED;
import theking530.staticpower.client.render.tileentitys.logicgates.TileEntityRenderNotGate;
import theking530.staticpower.client.render.tileentitys.logicgates.TileEntityRenderOrGate;
import theking530.staticpower.client.render.tileentitys.logicgates.TileEntityRenderPowerCell;
import theking530.staticpower.client.render.tileentitys.logicgates.TileEntityRenderSignalMultiplier;
import theking530.staticpower.client.render.tileentitys.logicgates.TileEntityRenderSubtractor;
import theking530.staticpower.client.render.tileentitys.logicgates.TileEntityRenderTimer;
import theking530.staticpower.conduits.fluidconduit.TileEntityFluidConduit;
import theking530.staticpower.conduits.itemconduit.TileEntityItemConduit;
import theking530.staticpower.conduits.staticconduit.TileEntityStaticConduit;
import theking530.staticpower.logic.gates.adder.TileEntityAdder;
import theking530.staticpower.logic.gates.and.TileEntityAndGate;
import theking530.staticpower.logic.gates.led.TileEntityLED;
import theking530.staticpower.logic.gates.notgate.TileEntityNotGate;
import theking530.staticpower.logic.gates.or.TileEntityOrGate;
import theking530.staticpower.logic.gates.powercell.TileEntityPowerCell;
import theking530.staticpower.logic.gates.subtractor.TileEntitySubtractorGate;
import theking530.staticpower.logic.gates.timer.TileEntityTimer;
import theking530.staticpower.logic.gates.transducer.TileEntitySignalMultiplier;
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
import theking530.staticpower.machines.mechanicalsqueezer.TileEntityMechanicalSqueezer;
import theking530.staticpower.machines.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.machines.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.tileentity.chest.energizedchest.TileEntityEnergizedChest;
import theking530.staticpower.tileentity.chest.lumumchest.TileEntityLumumChest;
import theking530.staticpower.tileentity.chest.staticchest.TileEntityStaticChest;

public class ClientProxy extends CommonProxy {

	public void registerProxies(){
	    //WailaConfig.callIMC();
		OBJLoader.INSTANCE.addDomain(Reference.MOD_ID);
		
		//Fluid Infuser
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidInfuser.class, new TileEntityRenderFluidInfuser());		
		//Crop Squeezer
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCropSqueezer.class, new TileEntityRenderCropSqueezer());
		//Fluid Generator
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidGenerator.class, new TileEntityRenderFluidGenerator());
		//Fermenter
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFermenter.class, new TileEntityRenderFermenter());
		//Smelter
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPoweredFurnace.class, new TileEntityRenderPoweredFurnace());
		//Grinder
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPoweredGrinder.class, new TileEntityRenderPoweredGrinder());
		//SConduit
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStaticConduit.class, new TileEntityRenderStaticConduit());
		//FConduit
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidConduit.class, new TileEntityRenderFluidConduit());
		//IConduit
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemConduit.class, new TileEntityRenderItemConduit());
		//Mechanical Squeezer
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMechanicalSqueezer.class, new TileEntityRenderMechanicalSqueezer());

		
		//Signal Multiplier
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySignalMultiplier.class, new TileEntityRenderSignalMultiplier());	
		//Not Gate
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNotGate.class, new TileEntityRenderNotGate());	
		//Power Cell
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPowerCell.class, new TileEntityRenderPowerCell());	
		//Timer
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTimer.class, new TileEntityRenderTimer());	
		//Adder
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdder.class, new TileEntityRenderAdder());	
		//And
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAndGate.class, new TileEntityRenderAndGate());	
		//Or
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOrGate.class, new TileEntityRenderOrGate());	
		//Subtractor
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySubtractorGate.class, new TileEntityRenderSubtractor());	
		//LED
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLED.class, new TileEntityRenderLED());	
		
		//Batteries
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStaticBattery.class, new TileEntityRenderBattery(Tier.STATIC));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergizedBattery.class, new TileEntityRenderBattery(Tier.ENERGIZED));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLumumBattery.class, new TileEntityRenderBattery(Tier.LUMUM));
		//Charging Station
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChargingStation.class, new TileEntityRenderChargingStation());	
		
		//Chests		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStaticChest.class, new TileEntityRenderChest(Tier.STATIC));	
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergizedChest.class, new TileEntityRenderChest(Tier.ENERGIZED));	
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLumumChest.class, new TileEntityRenderChest(Tier.LUMUM));		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFusionFurnace.class, new TileEntityRenderFusionFurnace());
		
		//Distillery
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDistillery.class, new TileEntityRenderDistillery());
		
		//Condenser
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCondenser.class, new TileEntityRenderCondenser());
	}
	public static void registerModelLoader(Item item, ResourceLocation modelLocation) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(modelLocation, "inventory"));
	}
}