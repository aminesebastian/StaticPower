package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.poweredgrinder.TileEntityPoweredGrinder;

public class TileEntityRenderPoweredGrinder extends BaseMachineTESR<TileEntityPoweredGrinder> {

	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/PoweredGrinderFront.png");
	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/PoweredGrinderFrontOn.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : front;
	}
}
