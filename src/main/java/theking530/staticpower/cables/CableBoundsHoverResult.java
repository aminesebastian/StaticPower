package theking530.staticpower.cables;

import net.minecraft.util.Direction;

public class CableBoundsHoverResult {
	public enum CableBoundsHoverType {
		NONE, CABLE, HELD_ATTACHMENT, ATTACHED_ATTACHMENT, HELD_COVER, ATTACHED_COVER
	}

	public static final CableBoundsHoverResult EMPTY = new CableBoundsHoverResult(CableBoundsHoverType.NONE, null, null);
	public final CableBoundsHoverType type;
	public final Direction direction;
	public final Direction hitSide;

	public CableBoundsHoverResult(CableBoundsHoverType type, Direction hitSide, Direction direction) {
		super();
		this.type = type;
		this.direction = direction;
		this.hitSide = hitSide;
	}

	public boolean isEmpty() {
		return this == EMPTY || direction == null || type == null;
	}
}
