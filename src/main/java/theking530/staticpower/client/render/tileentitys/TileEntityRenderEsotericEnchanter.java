package theking530.staticpower.client.render.tileentitys;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.utilities.SideModeList;
import theking530.staticpower.machines.esotericenchanter.TileEsotericEnchanter;

public class TileEntityRenderEsotericEnchanter extends BaseMachineTESR<TileEsotericEnchanter> {

	private static final ResourceLocation front = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/esoteric_enchanter_front.png");
	
	private static final ResourceLocation enchanterSide = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/esoteric_enchanter_side.png");
	private static final ResourceLocation enchanterSideIn = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/esoteric_enchanter_side_input.png");
	private static final ResourceLocation enchanterSideOut = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/esoteric_enchanter_side_output.png");
	private static final ResourceLocation enchanterSideDis = new ResourceLocation(Reference.MOD_ID, "textures/blocks/machines/esoteric_enchanter_side_disabled.png");

	@Override
	protected ResourceLocation getFrontTexture(boolean machineOn) {
		return front;
	}
	@Override
	protected ResourceLocation getSideTexture(SideModeList.Mode mode) {
		switch(mode) {
			case Regular: return enchanterSide;
			case Input: return enchanterSideIn;
			case Output: return enchanterSideOut;
			case Disabled: return enchanterSideDis;
			default: return enchanterSide;
		}
	}
}