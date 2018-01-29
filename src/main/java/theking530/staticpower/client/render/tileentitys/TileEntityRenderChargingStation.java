package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.chargingstation.TileEntityChargingStation;

public class TileEntityRenderChargingStation extends BaseMachineTESR<TileEntityChargingStation> {
	
	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/ChargingStationOff.png");
	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/ChargingStationOn.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : front;
	}	
}