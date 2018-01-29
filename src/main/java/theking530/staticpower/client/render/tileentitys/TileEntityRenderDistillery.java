package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.distillery.TileEntityDistillery;

public class TileEntityRenderDistillery extends BaseMachineTESR<TileEntityDistillery> {

	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/DistilleryOff.png");
	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/DistilleryOn.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : front;
	}
}