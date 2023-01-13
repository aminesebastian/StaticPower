package theking530.staticpower.client.rendering.utilities;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.util.TransformationHelper;
import theking530.staticpower.StaticPower;

public class QuadUtilities {
	protected static final Map<Direction, Quaternion> FACING_ROTATIONS = new EnumMap<Direction, Quaternion>(Direction.class);
	protected static final Map<Direction, Transformation> SIDE_TRANSFORMS = new EnumMap<>(Direction.class);
	static {
		for (Direction dir : Direction.values()) {
			Quaternion quaternion;
			if (dir == Direction.UP) {
				quaternion = TransformationHelper.quatFromXYZ(new Vector3f(90, 0, 0), true);
			} else if (dir == Direction.DOWN) {
				quaternion = TransformationHelper.quatFromXYZ(new Vector3f(270, 0, 0), true);
			} else {
				double r = Math.PI * (360 - dir.getOpposite().get2DDataValue() * 90) / 180d;
				quaternion = TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false);
			}
			FACING_ROTATIONS.put(dir, quaternion);
			SIDE_TRANSFORMS.put(dir, new Transformation(null, quaternion, null, null).blockCenterToCorner());
		}
	}

	public static Quaternion getRotationForDirection(Direction dir) {
		return FACING_ROTATIONS.get(dir);
	}

	public static Transformation getTransformForDirection(Direction dir) {
		return SIDE_TRANSFORMS.get(dir);
	}

	public static List<BakedQuad> rotateQuadsToFaceDirection(BakedModel model, Direction desiredRotation, Direction drawingSide, BlockState state, RandomSource rand) {
		Transformation transformation = SIDE_TRANSFORMS.get(desiredRotation);
		ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

		if (drawingSide != null && drawingSide.get2DDataValue() > -1) {
			int faceOffset = 4 + Direction.NORTH.get2DDataValue() - desiredRotation.get2DDataValue();
			drawingSide = Direction.from2DDataValue((drawingSide.get2DDataValue() + faceOffset) % 4);
		}

		// Build the output.
		try {
			IQuadTransformer transformer = QuadTransformers.applying(transformation);
			for (BakedQuad quad : model.getQuads(state, drawingSide, rand, ModelData.EMPTY, null)) {
				quads.add(transformer.process(quad));
			}
		} catch (Exception e) {
			StaticPower.LOGGER.error(String.format("An error occured when attempting to rotate a model to face the desired rotation. Model: %1$s.", model), e);
		}

		return quads.build();
	}

	public static BakedQuad rotateQuadToFaceDirection(BakedQuad quad, Direction desiredRotation, Direction drawingSide) {
		// Build the output.
		try {
			Transformation transformation = SIDE_TRANSFORMS.get(desiredRotation);
			IQuadTransformer transformer = QuadTransformers.applying(transformation);
			return transformer.process(quad);
		} catch (Exception e) {
			StaticPower.LOGGER.error(String.format("An error occured when attempting to rotate a quad to face the desired rotation."), e);
		}
		return quad;
	}
}
