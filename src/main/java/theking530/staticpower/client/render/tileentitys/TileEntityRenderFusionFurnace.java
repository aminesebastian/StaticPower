package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.machines.fusionfurnace.TileEntityFusionFurnace;

public class TileEntityRenderFusionFurnace extends BaseMachineTESR<TileEntityFusionFurnace> {
	
	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/FusionFurnaceFront.png");
	private static final ResourceLocation frontOn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/FusionFurnaceFrontOn.png");
	
	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return machineOn ? frontOn : front;
	}
}