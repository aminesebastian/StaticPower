package theking530.staticpower.tileentities.utilities.interfaces;

import theking530.staticpower.tileentities.utilities.RedstoneModeList;
import theking530.staticpower.tileentities.utilities.RedstoneModeList.RedstoneMode;

public interface IRedstoneConfigurable {

	public RedstoneMode getRedstoneMode();

	public void setRedstoneMode(RedstoneMode newMode);

	public boolean isRedstoneControllable();

}
