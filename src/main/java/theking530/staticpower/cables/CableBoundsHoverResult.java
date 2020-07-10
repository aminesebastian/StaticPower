package theking530.staticpower.cables;

import net.minecraft.util.Direction;

public class CableBoundsHoverResult {
	public enum CableBoundsHoverType {
		NONE, CABLE, HELD_ATTACHMENT, ATTACHED_ATTACHMENT, HELD_COVER, ATTACHED_COVER
	}

	public static final CableBoundsHoverResult EMPTY = new CableBoundsHoverResult(CableBoundsHoverType.NONE, null);
	public final CableBoundsHoverType type;
	public final Direction direction;

	public CableBoundsHoverResult(CableBoundsHoverType type, Direction direction) {
		super();
		this.type = type;
		this.direction = direction;
	}

	public boolean isEmpty() {
		return this == EMPTY || direction == null || type == null;
	}
}
