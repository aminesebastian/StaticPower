package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.utilities.SideModeList;
import theking530.staticpower.machines.quarry.TileEntityQuarry;

public class TileEntityRenderQuarry extends BaseMachineTESR<TileEntityQuarry> {

	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/quarry_front.png");
	
	private static final ResourceLocation quarrySide = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/quarry_side.png");
	private static final ResourceLocation quarrySideIn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/quarry_side_input.png");
	private static final ResourceLocation quarrySideOut = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/quarry_side_output.png");
	private static final ResourceLocation quarrySideDis = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/quarry_side_disabled.png");

	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return front;
	}
	@Override
	protected ResourceLocation getSideTexture(SideModeList.Mode mode) {
		switch(mode) {
			case Regular: return quarrySide;
			case Input: return quarrySideIn;
			case Output: return quarrySideOut;
			case Disabled: return quarrySideDis;
			default: return quarrySide;
		}
	}
}