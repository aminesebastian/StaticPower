package theking530.staticpower.client.rendering.blocks;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.util.TransformationHelper;
import theking530.staticpower.blockentities.power.lightsocket.BlockEntityLightSocket;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.client.rendering.utilities.QuadUtilities;

@OnlyIn(Dist.CLIENT)
public class LightSocketModel extends AbstractBakedModel {
	public LightSocketModel(BakedModel baseModel) {
		super(baseModel);
	}

	@Override
	@Nonnull
	public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
		return tileData;
	}

	@SuppressWarnings("resource")
	@Override
	protected List<BakedQuad> getBakedQuadsFromModelData(@Nullable BlockState state, Direction side, @Nonnull RandomSource rand, @Nonnull ModelData data, RenderType renderLayer) {
		// If the property is not there, return early.
		if (!data.has(BlockEntityLightSocket.LIGHT_SOCKET_RENDERING_STATE)) {
			return Collections.emptyList();
		}

		// If there is no bulb, just return the base
		// model.
		ItemStack bulb = data.get(BlockEntityLightSocket.LIGHT_SOCKET_RENDERING_STATE).bulb();
		if (bulb.isEmpty()) {
			return BaseModel.getQuads(state, side, rand, data, renderLayer);
		}

		// Create the output array.
		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data, renderLayer);
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();
		newQuads.addAll(baseQuads);

		Direction facing = state.getValue(StaticPowerBlockEntityBlock.FACING);
		Vector3f offset = Vector3f.ZERO;
		Vector3f scale = new Vector3f(1, 1, 1);
		Vector3f rotation = QuadUtilities.getRotationForDirection(facing).toXYZDegrees();

		if (facing.getAxis() == Axis.X) {
			rotation.setZ(rotation.z() - (90 * facing.getAxisDirection().getStep()));
			offset = new Vector3f(0.075f * facing.getAxisDirection().getStep(), 0, 0);
		} else if (facing.getAxis() == Axis.Z) {
			rotation.setX(rotation.x() + (90 * facing.getAxisDirection().getStep()));
			offset = new Vector3f(0, 0f, 0.075f * facing.getAxisDirection().getStep());
		} else if (facing == Direction.UP) {
			rotation.setX(rotation.x() - (90 * facing.getAxisDirection().getStep()));
			offset = new Vector3f(0, 0.075f, 0);
		} else {
			rotation.setX(rotation.x() + (90 * facing.getAxisDirection().getStep()));
			offset = new Vector3f(0, -0.075f, 0);
		}

		// Transform the bulbs quads.
		BakedModel bulbModel = Minecraft.getInstance().getItemRenderer().getModel(bulb, Minecraft.getInstance().level, null, 0);
		List<BakedQuad> bakedCardQuads = transformQuads(bulbModel, offset, scale, TransformationHelper.quatFromXYZ(rotation, true), side, state, rand, renderLayer);
		newQuads.addAll(bakedCardQuads);
		return newQuads.build();
	}

	@Override
	public boolean usesBlockLight() {
		return BaseModel.usesBlockLight();
	}
}
