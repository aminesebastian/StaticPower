package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.fermenter.TileEntityFermenter;

public class TileEntityRenderFermenter extends BaseMachineTESR<TileEntityFermenter> {

	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/fermenter_front_on.png");
	private static final ResourceLocation frontOff = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/fermenter_front_off.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : frontOff;
	}
}