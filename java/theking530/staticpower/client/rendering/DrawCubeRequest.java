package theking530.staticpower.client.rendering;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.Color;

@OnlyIn(Dist.CLIENT)
public class DrawCubeRequest {
	public Vector3f Position;
	public Vector3f Scale;
	public Color Color;

	public DrawCubeRequest(Vector3f position, Vector3f scale, theking530.staticcore.utilities.Color color) {
		Position = position;
		Scale = scale;
		Color = color;
	}

	public DrawCubeRequest(BlockPos position, Vector3f scale, theking530.staticcore.utilities.Color color) {
		this(new Vector3f(position.getX(), position.getY(), position.getZ()), scale, color);
	}
}
