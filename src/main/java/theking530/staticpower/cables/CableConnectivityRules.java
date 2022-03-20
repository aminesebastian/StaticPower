package theking530.staticpower.cables;

import theking530.staticpower.cables.network.ServerCable;

public abstract class CableConnectivityRules {
	/**
	 * This is the condition checked when a cable is first placed in the world. This
	 * indicates whether or not the cable can automatically connect to the other
	 * cable.
	 * 
	 * @param otherCable
	 * @return
	 */
	abstract boolean canAutoConnectToCable(ServerCable otherCable);

	/**
	 * This is the condition checked when a user attempts to change the "disabled"
	 * state of a side of a cable.
	 * 
	 * @param otherCable
	 * @return
	 */
	abstract boolean canConnectToCable(ServerCable otherCable);
}
