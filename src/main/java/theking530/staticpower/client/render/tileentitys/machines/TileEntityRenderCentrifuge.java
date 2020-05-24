package theking530.staticpower.client.render.tileentitys.machines;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.machines.centrifuge.TileEntityCentrifuge;
import theking530.staticpower.utilities.Reference;

public class TileEntityRenderCentrifuge extends BaseMachineTESR<TileEntityCentrifuge> {

	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/centrifuge_front_on.png");
	private static final ResourceLocation frontOff = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/centrifuge_front_off.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : frontOff;
	}
}
