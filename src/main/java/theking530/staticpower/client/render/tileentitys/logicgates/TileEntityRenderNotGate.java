package theking530.staticpower.client.render.tileentitys.logicgates;

import theking530.staticpower.client.model.ModelSignalMultiplier;
import theking530.staticpower.client.render.TileEntitySpecialRendererTextures;

public class TileEntityRenderNotGate extends TileEntityRenderLogicGateBase {

	public TileEntityRenderNotGate() {
		super(new ModelSignalMultiplier(), TileEntitySpecialRendererTextures.LOGIC_GATE_BASE_ON, TileEntitySpecialRendererTextures.LOGIC_GATE_BASE_OFF);
	}
}
