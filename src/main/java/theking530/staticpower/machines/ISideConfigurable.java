package theking530.staticpower.machines;

import net.minecraft.util.EnumFacing;
import theking530.staticpower.utils.SideModeList.Mode;
import theking530.staticpower.utils.SidePicker.BlockSide;
import theking530.staticpower.utils.SidePicker.Side;

public interface ISideConfigurable {

	public Mode getModeFromSide(Side side);
    public Mode getModeFromFacing(EnumFacing facing);
}
