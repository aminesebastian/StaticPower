package theking530.staticpower.blocks;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class StaticPowerBlockProperties {
	public enum TowerPiece implements StringRepresentable {
		FULL("full"), MIDDLE("middle"), TOP("top"), BOTTOM("bottom");

		private final String name;

		TowerPiece(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	public static final EnumProperty<TowerPiece> TOWER_POSITION = EnumProperty.create("position", TowerPiece.class);
}
