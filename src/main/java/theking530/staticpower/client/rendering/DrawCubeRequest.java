package theking530.staticpower.client.rendering;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.BlockPos;
import theking530.common.utilities.Color;

public class DrawCubeRequest {
	public Vector3f Position;
	public Vector3f Scale;
	public Color Color;

	public DrawCubeRequest(Vector3f position, Vector3f scale, theking530.common.utilities.Color color) {
		Position = position;
		Scale = scale;
		Color = color;
	}

	public DrawCubeRequest(BlockPos position, Vector3f scale, theking530.common.utilities.Color color) {
		this(new Vector3f(position.getX(), position.getY(), position.getZ()), scale, color);
	}
}
