package theking530.api.multipart;

import theking530.staticpower.items.tools.chainsaw.ChainsawBlade;
import theking530.staticpower.items.tools.miningdrill.DrillBit;

public class MultiPartSlots {
	public static final BasicMultiPartSlot DRILL_BIT = new BasicMultiPartSlot("drill_bit", "slot.staticpower.drill_bit", false, DrillBit.class);
	public static final BasicMultiPartSlot CHAINSAW_BLADE = new BasicMultiPartSlot("chainsaw_blade", "slot.staticpower.chainsaw_blade", false, ChainsawBlade.class);
}
