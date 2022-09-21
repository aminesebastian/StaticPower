package theking530.staticpower.blockentities.machines.refinery;

import theking530.staticpower.blockentities.machines.refinery.controller.BlockEntityRefineryController;

public interface IRefineryBlockEntity {
	public void setController(BlockEntityRefineryController controller);

	public BlockEntityRefineryController getController();

	public default boolean refreshController() {
		if (getController() != null) {
			getController().requestMultiBlockRefresh();
			return true;
		}
		return false;
	}
}
