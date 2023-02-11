package theking530.staticcore.utilities;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelUtilities {
	public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
		if (from == to) {
			return shape;
		}

		shape = rotateAlongYAxis(from, to, shape);
		shape = rotateAlongXAxis(from, to, shape);

		return shape;
	}

	public static VoxelShape rotateAlongYAxis(Direction from, Direction to, VoxelShape shape) {
		VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };
		int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
		for (int i = 0; i < times; i++) {
			buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
			buffer[0] = buffer[1];
			buffer[1] = Shapes.empty();
		}
		return buffer[0];
	}

	public static VoxelShape rotateAlongXAxis(Direction from, Direction to, VoxelShape shape) {
		if (from.getAxis() != Axis.Y && to.getAxis() == Axis.Y) {
			VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };
			buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minX, minY, 1 - minZ, maxX, maxY)));
			buffer[0] = buffer[1];
			buffer[1] = Shapes.empty();
			return buffer[0];
		}
		return shape;
	}
}
