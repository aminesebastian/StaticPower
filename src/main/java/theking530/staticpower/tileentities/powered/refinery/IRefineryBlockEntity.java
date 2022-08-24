package theking530.staticpower.tileentities.powered.refinery;

import theking530.staticpower.tileentities.powered.refinery.controller.TileEntityRefineryController;

public interface IRefineryBlockEntity {
	public void setController(TileEntityRefineryController controller);

	public TileEntityRefineryController getController();

	public default boolean refreshController() {
		if (getController() != null) {
			getController().requestMultiBlockRefresh();
			return true;
		}
		return false;
	}
}
