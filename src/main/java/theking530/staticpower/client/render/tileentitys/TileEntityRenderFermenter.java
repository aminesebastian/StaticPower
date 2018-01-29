package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.fermenter.TileEntityFermenter;

public class TileEntityRenderFermenter extends BaseMachineTESR<TileEntityFermenter> {


	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/FermenterOff.png");
	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/FermenterOn.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : front;
	}
}