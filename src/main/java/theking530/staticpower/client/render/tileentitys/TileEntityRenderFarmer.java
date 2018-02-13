package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.utilities.SideModeList;
import theking530.staticpower.machines.basicfarmer.TileEntityBasicFarmer;

public class TileEntityRenderFarmer extends BaseMachineTESR<TileEntityBasicFarmer> {

	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/farmer_front.png");
	
	private static final ResourceLocation farmerSide = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/farmer_side.png");
	private static final ResourceLocation farmerSideIn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/farmer_side_input.png");
	private static final ResourceLocation farmerSideOut = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/farmer_side_output.png");
	private static final ResourceLocation farmerSideDis = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/farmer_side_disabled.png");

	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return front;
	}
	@Override
	protected ResourceLocation getSideTexture(SideModeList.Mode mode) {
		switch(mode) {
			case Regular: return farmerSide;
			case Input: return farmerSideIn;
			case Output: return farmerSideOut;
			case Disabled: return farmerSideDis;
			default: return farmerSide;
		}
	}
}