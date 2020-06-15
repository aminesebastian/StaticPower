package theking530.staticpower.tileentities.cables;

import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;

public class CableConnectionRenderState {
	public final CableConnectionState[] ConnectionStates;
	public final Boolean[] DisabledStates;

	public CableConnectionRenderState() {
		ConnectionStates = new CableConnectionState[6];
		DisabledStates = new Boolean[6];
	}

	public CableConnectionRenderState setDisabledState(Direction dir, boolean state) {
		DisabledStates[dir.ordinal()] = state;
		return this;
	}

	public CableConnectionRenderState setConnetionState(Direction dir, CableConnectionState state) {
		ConnectionStates[dir.ordinal()] = state;
		return this;
	}
}
