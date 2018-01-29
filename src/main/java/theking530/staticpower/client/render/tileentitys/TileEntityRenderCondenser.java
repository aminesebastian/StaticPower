package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.condenser.TileEntityCondenser;

public class TileEntityRenderCondenser extends BaseMachineTESR<TileEntityCondenser> {

	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/CondenserOff.png");
	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/CondenserOn.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : front;
	}
}