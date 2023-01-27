package theking530.staticpower.client.rendering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticpower.StaticPower;

public class RotatedModelCache {
	protected static final Map<Direction, BlockModelRotation> SIDE_TRANSFORMS = new HashMap<>();
	protected static final Map<Direction, Vector3f> SIDE_ROTATIONS = new HashMap<>();
	static {
		SIDE_TRANSFORMS.put(Direction.UP, BlockModelRotation.X270_Y0);
		SIDE_TRANSFORMS.put(Direction.DOWN, BlockModelRotation.X90_Y0);
		SIDE_TRANSFORMS.put(Direction.EAST, BlockModelRotation.X0_Y90);
		SIDE_TRANSFORMS.put(Direction.WEST, BlockModelRotation.X0_Y270);
		SIDE_TRANSFORMS.put(Direction.NORTH, BlockModelRotation.X180_Y180);
		SIDE_TRANSFORMS.put(Direction.SOUTH, BlockModelRotation.X180_Y0);

		SIDE_ROTATIONS.put(Direction.UP, new Vector3f(270, 0, 0));
		SIDE_ROTATIONS.put(Direction.DOWN, new Vector3f(90, 0, 0));
		SIDE_ROTATIONS.put(Direction.EAST, new Vector3f(0, 90, 0));
		SIDE_ROTATIONS.put(Direction.WEST, new Vector3f(0, 270, 0));
		SIDE_ROTATIONS.put(Direction.NORTH, new Vector3f(180, 180, 0));
		SIDE_ROTATIONS.put(Direction.SOUTH, new Vector3f(180, 0, 0));
	}
	private static final Map<ResourceLocation, Map<Direction, BakedModel>> MODEL_MAP = new HashMap<>();

	public static void bakeForAllDirections(ResourceLocation modelLocation) {
		// This option exists to avoid concurrent modification exceptions for
		// multi-threaded rendering.
		for (Direction dir : Direction.values()) {
			get(modelLocation, dir);
		}
	}

	public static BakedModel get(ResourceLocation modelLocation, Direction direction) {
		if (!MODEL_MAP.containsKey(modelLocation)) {
			MODEL_MAP.put(modelLocation, new HashMap<>());
		}
		if (!MODEL_MAP.get(modelLocation).containsKey(direction)) {
			BakedModel model = Minecraft.getInstance().getModelManager().getModelBakery().bake(modelLocation, SIDE_TRANSFORMS.get(direction), (material) -> {
				return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(material.texture());
			});
			StaticPower.LOGGER.debug(String.format("Generating rotated model: %1$s facing direction: %2$s.", modelLocation, direction.toString()));
			MODEL_MAP.get(modelLocation).put(direction, model);
		}
		return MODEL_MAP.get(modelLocation).get(direction);
	}

	public static List<BakedQuad> getQuads(ResourceLocation modelLocation, Direction direction, @Nullable BlockState state, @Nullable Direction renderSide,
			@NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
		return get(modelLocation, direction).getQuads(state, renderSide, rand, data, renderType);
	}

	public static Vector3f getRotation(Direction direction) {
		return SIDE_ROTATIONS.get(direction).copy();
	}

	public void clear() {
		MODEL_MAP.clear();
	}
}
