package theking530.staticpower.tileentity.gates.transducer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;

public class ContainerMultiplier extends Container {

	public ContainerMultiplier(InventoryPlayer invPlayer, TileEntitySignalMultiplier teSignalMultiplier) {
		
	}
	
	public boolean enchantItem(EntityPlayer player, int action) {
		return true;
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

}
