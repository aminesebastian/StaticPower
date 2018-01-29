package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.poweredfurnace.TileEntityPoweredFurnace;

public class TileEntityRenderPoweredFurnace extends BaseMachineTESR<TileEntityPoweredFurnace> {

	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/PoweredSmelterFront.png");
	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/PoweredSmelterFrontOn.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : front;
	}	
}