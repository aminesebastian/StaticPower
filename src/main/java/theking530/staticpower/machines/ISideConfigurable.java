package theking530.staticpower.machines;

import net.minecraft.util.EnumFacing;
import theking530.staticpower.utils.SideModeList.Mode;
import theking530.staticpower.utils.OldSidePicker.BlockSide;
import theking530.staticpower.utils.OldSidePicker.Side;

public interface ISideConfigurable {

	public Mode getModeFromSide(Side side);
    public Mode getModeFromFacing(EnumFacing facing);
}
