package theking530.staticpower.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.items.book.GuiStaticBook;
import theking530.staticpower.items.itemfilter.ContainerItemFilter;
import theking530.staticpower.items.itemfilter.GuiItemFilter;
import theking530.staticpower.items.itemfilter.InventoryItemFilter;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.machines.basicfarmer.ContainerBasicFarmer;
import theking530.staticpower.machines.basicfarmer.GuiBasicFarmer;
import theking530.staticpower.machines.basicfarmer.TileEntityBasicFarmer;
import theking530.staticpower.machines.batteries.ContainerBattery;
import theking530.staticpower.machines.batteries.GuiBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBattery;
import theking530.staticpower.machines.chargingstation.ContainerChargingStation;
import theking530.staticpower.machines.chargingstation.GuiChargingStation;
import theking530.staticpower.machines.chargingstation.TileEntityChargingStation;
import theking530.staticpower.machines.cropsqueezer.ContainerCropSqueezer;
import theking530.staticpower.machines.cropsqueezer.GuiCropSqueezer;
import theking530.staticpower.machines.cropsqueezer.TileEntityCropSqueezer;
import theking530.staticpower.machines.fermenter.ContainerFermenter;
import theking530.staticpower.machines.fermenter.GuiFermenter;
import theking530.staticpower.machines.fermenter.TileEntityFermenter;
import theking530.staticpower.machines.fluidgenerator.ContainerFluidGenerator;
import theking530.staticpower.machines.fluidgenerator.GuiFluidGenerator;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;
import theking530.staticpower.machines.fluidinfuser.ContainerFluidInfuser;
import theking530.staticpower.machines.fluidinfuser.GuiFluidInfuser;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;
import theking530.staticpower.machines.fusionfurnace.ContainerFusionFurnace;
import theking530.staticpower.machines.fusionfurnace.GuiFusionFurnace;
import theking530.staticpower.machines.fusionfurnace.TileEntityFusionFurnace;
import theking530.staticpower.machines.mechanicalsqueezer.ContainerMechanicalSqueezer;
import theking530.staticpower.machines.mechanicalsqueezer.GuiMechanicalSqueezer;
import theking530.staticpower.machines.mechanicalsqueezer.TileEntityMechanicalSqueezer;
import theking530.staticpower.machines.poweredfurnace.ContainerPoweredFurnace;
import theking530.staticpower.machines.poweredfurnace.GuiPoweredFurnace;
import theking530.staticpower.machines.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.machines.poweredgrinder.ContainerPoweredGrinder;
import theking530.staticpower.machines.poweredgrinder.GuiPoweredGrinder;
import theking530.staticpower.machines.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.machines.quarry.ContainerQuarry;
import theking530.staticpower.machines.quarry.GuiQuarry;
import theking530.staticpower.machines.quarry.TileEntityQuarry;
import theking530.staticpower.machines.solderingtable.ContainerSolderingTable;
import theking530.staticpower.machines.solderingtable.GuiSolderingTable;
import theking530.staticpower.machines.solderingtable.TileEntitySolderingTable;
import theking530.staticpower.tileentity.chest.energizedchest.ContainerEnergizedChest;
import theking530.staticpower.tileentity.chest.energizedchest.GuiEnergizedChest;
import theking530.staticpower.tileentity.chest.energizedchest.TileEntityEnergizedChest;
import theking530.staticpower.tileentity.chest.lumumchest.ContainerLumumChest;
import theking530.staticpower.tileentity.chest.lumumchest.GuiLumumChest;
import theking530.staticpower.tileentity.chest.lumumchest.TileEntityLumumChest;
import theking530.staticpower.tileentity.chest.staticchest.ContainerStaticChest;
import theking530.staticpower.tileentity.chest.staticchest.GuiStaticChest;
import theking530.staticpower.tileentity.chest.staticchest.TileEntityStaticChest;
import theking530.staticpower.tileentity.gates.powercell.ContainerPowerCell;
import theking530.staticpower.tileentity.gates.powercell.GuiPowerCell;
import theking530.staticpower.tileentity.gates.powercell.TileEntityPowerCell;
import theking530.staticpower.tileentity.gates.timer.ContainerTimer;
import theking530.staticpower.tileentity.gates.timer.GuiTimer;
import theking530.staticpower.tileentity.gates.timer.TileEntityTimer;
import theking530.staticpower.tileentity.gates.transducer.ContainerMultiplier;
import theking530.staticpower.tileentity.gates.transducer.GuiSignalMultiplier;
import theking530.staticpower.tileentity.gates.transducer.TileEntitySignalMultiplier;
import theking530.staticpower.tileentity.vacuumchest.ContainerVacuumChest;
import theking530.staticpower.tileentity.vacuumchest.GuiVacuumChest;
import theking530.staticpower.tileentity.vacuumchest.TileEntityVacuumChest;

public class GuiHandler implements IGuiHandler {
	//Server
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity entity = world.getTileEntity(pos);
			
		if(entity != null) {
			switch(ID) {
			case GuiIDRegistry.guiIDFluidInfuser:
				if (entity instanceof TileEntityFluidInfuser) {	
					return new ContainerFluidInfuser(player.inventory, (TileEntityFluidInfuser) entity);
				}
				return null;
			case GuiIDRegistry.guiIDFluidGenerator:
				if (entity instanceof TileEntityFluidGenerator) {	
					return new ContainerFluidGenerator(player.inventory, (TileEntityFluidGenerator) entity);
				}
				return null;
			case GuiIDRegistry.guiBasicFarmer:
				if (entity instanceof TileEntityBasicFarmer) {	
					return new ContainerBasicFarmer(player.inventory, (TileEntityBasicFarmer) entity);
				}
				return null;
			case GuiIDRegistry.guiIDCropSqueezer:
				if (entity instanceof TileEntityCropSqueezer) {	
					return new ContainerCropSqueezer(player.inventory, (TileEntityCropSqueezer) entity);
				}
				return null;
			case GuiIDRegistry.guiMechanicalSqueezer:
				if (entity instanceof TileEntityMechanicalSqueezer) {	
					return new ContainerMechanicalSqueezer(player.inventory, (TileEntityMechanicalSqueezer) entity);
				}
				return null;
			case GuiIDRegistry.guiIDChargingStation:
				if (entity instanceof TileEntityChargingStation) {	
					return new ContainerChargingStation(player.inventory, (TileEntityChargingStation) entity);
				}
				return null;
			case GuiIDRegistry.guiFermenter:
				if (entity instanceof TileEntityFermenter) {	
					return new ContainerFermenter(player.inventory, (TileEntityFermenter) entity);
				}
				return null;
			case GuiIDRegistry.guiIDFusionFurnace:
				if (entity instanceof TileEntityFusionFurnace) {	
					return new ContainerFusionFurnace(player.inventory, (TileEntityFusionFurnace) entity);
				}
				return null;	
			case GuiIDRegistry.guiPowerCell:
				if (entity instanceof TileEntityPowerCell) {	
					return new ContainerPowerCell(player.inventory, (TileEntityPowerCell) entity);
				}
				return null;
			case GuiIDRegistry.guiTimer:
				if (entity instanceof TileEntityTimer) {	
					return new ContainerTimer(player.inventory, (TileEntityTimer) entity);
				}
				return null;
			case GuiIDRegistry.guiIDQuarry:
				if (entity instanceof TileEntityQuarry) {	
					return new ContainerQuarry(player.inventory, (TileEntityQuarry) entity);
				}
				return null;
			case GuiIDRegistry.guiIDPoweredGrinder:
				if (entity instanceof TileEntityPoweredGrinder) {	
					return new ContainerPoweredGrinder(player.inventory, (TileEntityPoweredGrinder) entity);
				}
				return null;
			case GuiIDRegistry.guiIDVacuumChest:
				if (entity instanceof TileEntityVacuumChest) {	
					return new ContainerVacuumChest(player.inventory, (TileEntityVacuumChest) entity);
				}
				return null;
			case GuiIDRegistry.guiIDPoweredFurnace:
				if (entity instanceof TileEntityPoweredFurnace) {	
					return new ContainerPoweredFurnace(player.inventory, (TileEntityPoweredFurnace) entity);
				}
				return null;
			case GuiIDRegistry.guiIDSolderingTable:
				if (entity instanceof TileEntitySolderingTable) {	
					return new ContainerSolderingTable(player.inventory, (TileEntitySolderingTable) entity);
				}
				return null;
			case GuiIDRegistry.guiIDBattery:
				if (entity instanceof TileEntityBattery) {	
					return new ContainerBattery(player.inventory, (TileEntityBattery) entity);
				}
				return null;
			case GuiIDRegistry.guiIDStaticChest:
				if (entity instanceof TileEntityStaticChest) {	
					return new ContainerStaticChest(player.inventory, (TileEntityStaticChest) entity);
				}
				return null;
			case GuiIDRegistry.guiIDEnergizedChest:
				if (entity instanceof TileEntityEnergizedChest) {	
					return new ContainerEnergizedChest(player.inventory, (TileEntityEnergizedChest) entity);
				}
				return null;
			case GuiIDRegistry.guiIDLumumChest:
				if (entity instanceof TileEntityLumumChest) {	
					return new ContainerLumumChest(player.inventory, (TileEntityLumumChest) entity);
				}
				return null;
			case GuiIDRegistry.guiIDSignalMultiplier:
				if (entity instanceof TileEntitySignalMultiplier) {	
					return new ContainerMultiplier(player.inventory, (TileEntitySignalMultiplier) entity);
				}
				return null;							
			}
		}else{
			switch(ID) {
			case GuiIDRegistry.guiIDItemFilter:
				ItemFilter filter = (ItemFilter)player.getHeldItemMainhand().getItem();
				return new ContainerItemFilter(player.inventory, new InventoryItemFilter(player.getHeldItemMainhand(), filter.TIER), filter.TIER);
			}
		}
		return null;
	}	

	//Client
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity entity = world.getTileEntity(pos);
		
		if(entity != null) {
		switch(ID) {
			case GuiIDRegistry.guiIDFluidInfuser:
				if (entity instanceof TileEntityFluidInfuser) {	
					return new GuiFluidInfuser(player.inventory, (TileEntityFluidInfuser) entity);				
				}
				return null;
			case GuiIDRegistry.guiIDFluidGenerator:
				if (entity instanceof TileEntityFluidGenerator) {	
					return new GuiFluidGenerator(player.inventory, (TileEntityFluidGenerator) entity);			
				}
				return null;
			case GuiIDRegistry.guiIDCropSqueezer:
				if (entity instanceof TileEntityCropSqueezer) {	
					return new GuiCropSqueezer(player.inventory, (TileEntityCropSqueezer) entity);			
				}
				return null;
			case GuiIDRegistry.guiMechanicalSqueezer:
				if (entity instanceof TileEntityMechanicalSqueezer) {	
					return new GuiMechanicalSqueezer(player.inventory, (TileEntityMechanicalSqueezer) entity);			
				}
				return null;
			case GuiIDRegistry.guiIDVacuumChest:
				if (entity instanceof TileEntityVacuumChest) {	
					return new GuiVacuumChest(player.inventory, (TileEntityVacuumChest) entity);
				
				}
				return null;
			case GuiIDRegistry.guiFermenter:
				if (entity instanceof TileEntityFermenter) {	
					return new GuiFermenter(player.inventory, (TileEntityFermenter) entity);
				
				}
				return null;
			case GuiIDRegistry.guiIDPoweredGrinder:
				if (entity instanceof TileEntityPoweredGrinder) {	
					return new GuiPoweredGrinder(player.inventory, (TileEntityPoweredGrinder) entity);					
					}
				return null;
			case GuiIDRegistry.guiPowerCell:
				if (entity instanceof TileEntityPowerCell) {	
					return new GuiPowerCell(player.inventory, (TileEntityPowerCell) entity);					
					}
				return null;
			case GuiIDRegistry.guiTimer:
				if (entity instanceof TileEntityTimer) {	
					return new GuiTimer(player.inventory, (TileEntityTimer) entity);					
					}
				return null;
			case GuiIDRegistry.guiIDChargingStation:
				if (entity instanceof TileEntityChargingStation) {	
					return new GuiChargingStation(player.inventory, (TileEntityChargingStation) entity);					
					}
				return null;
			case GuiIDRegistry.guiBasicFarmer:
				if (entity instanceof TileEntityBasicFarmer) {	
					return new GuiBasicFarmer(player.inventory, (TileEntityBasicFarmer) entity);					
				}
				return null;
			case GuiIDRegistry.guiIDPoweredFurnace:
				if (entity instanceof TileEntityPoweredFurnace) {	
					return new GuiPoweredFurnace(player.inventory, (TileEntityPoweredFurnace) entity);				
				}
				return null;
			case GuiIDRegistry.guiIDSolderingTable:
				if (entity instanceof TileEntitySolderingTable) {	
					return new GuiSolderingTable(player.inventory, (TileEntitySolderingTable) entity);			
				}
				return null;
			case GuiIDRegistry.guiIDFusionFurnace:
				if (entity instanceof TileEntityFusionFurnace) {	
					return new GuiFusionFurnace(player.inventory, (TileEntityFusionFurnace) entity);				
				}
				return null;
			case GuiIDRegistry.guiIDBattery:
				if (entity instanceof TileEntityBattery) {	
					return new GuiBattery(player.inventory, (TileEntityBattery) entity);			
				}
				return null;
			case GuiIDRegistry.guiIDQuarry:
				if (entity instanceof TileEntityQuarry) {	
					return new GuiQuarry(player.inventory, (TileEntityQuarry) entity);			
				}
				return null;
			case GuiIDRegistry.guiIDStaticChest:
				if (entity instanceof TileEntityStaticChest) {	
					return new GuiStaticChest(player.inventory, (TileEntityStaticChest) entity);				
				}
				return null;
			case GuiIDRegistry.guiIDEnergizedChest:
				if (entity instanceof TileEntityEnergizedChest) {	
					return new GuiEnergizedChest(player.inventory, (TileEntityEnergizedChest) entity);			
				}
				return null;
			case GuiIDRegistry.guiIDLumumChest:
				if (entity instanceof TileEntityLumumChest) {	
					return new GuiLumumChest(player.inventory, (TileEntityLumumChest) entity);				
				}
				return null;
			case GuiIDRegistry.guiIDSignalMultiplier:
				if (entity instanceof TileEntitySignalMultiplier) {	
					return new GuiSignalMultiplier(player.inventory, (TileEntitySignalMultiplier) entity);				
				}
				return null;
			}
		}else{
			switch(ID) {
			case GuiIDRegistry.guiIDItemFilter:
				ItemFilter filter = (ItemFilter)player.getHeldItemMainhand().getItem();
				return new GuiItemFilter(player.inventory, filter.TIER, new InventoryItemFilter(player.getHeldItemMainhand(), filter.TIER));
			
			case GuiIDRegistry.guiIDStaticBook:
				ItemFilter book = (ItemFilter)player.getHeldItemMainhand().getItem();
				return new GuiStaticBook();
			}
		}
		return null;
	}
}


