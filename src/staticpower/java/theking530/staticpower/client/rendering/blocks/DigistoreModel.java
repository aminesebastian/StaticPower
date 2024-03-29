package theking530.staticpower.client.rendering.blocks;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticcore.client.models.AbstractBakedModel;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.blockentities.digistorenetwork.digistore.BlockEntityDigistore;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.client.rendering.RotatedModelCache;
import theking530.staticpower.items.DigistoreCard;

@OnlyIn(Dist.CLIENT)
public class DigistoreModel extends AbstractBakedModel {
	public DigistoreModel(BakedModel baseModel) {
		super(baseModel);
	}

	@Override
	@Nonnull
	public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
		return tileData;
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromModelData(@Nullable BlockState state, Direction side, @Nonnull RandomSource rand, @Nonnull ModelData data, RenderType renderLayer) {
		// If the property is not there, return early.
		if (!data.has(BlockEntityDigistore.RENDERING_STATE)) {
			return Collections.emptyList();
		}

		// Get the data used in rendering.
		Direction facing = state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING);
		ItemStack card = data.get(BlockEntityDigistore.RENDERING_STATE).card;

		// Create the output array.
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		// Add all the base quads for the server rack to the output.
		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data, renderLayer);
		newQuads.addAll(baseQuads);

		// Render the card.
		if (!card.isEmpty()) {
			// Get the model of the card.
			BakedModel model = Minecraft.getInstance().getModelManager().getModel(((DigistoreCard) card.getItem()).model);
			Vector3f rotation = RotatedModelCache.getRotation(facing);
			rotation.set(0, rotation.y() + 180, 0);

			float zOffset = -0.5f * UNIT;
			Vector3f offset = SDMath.transformVectorByDirection(facing, new Vector3f(0, UNIT * 1.85f, zOffset));

			// Transform the card's quads.
			List<BakedQuad> bakedCardQuads = transformQuads(model, offset, new Vector3f(1.55f, 0.85f, 1.0f), Quaternion.fromXYZDegrees(rotation), side, state, rand, renderLayer);
			newQuads.addAll(bakedCardQuads);

		}

		return newQuads.build();
	}

	@Override
	public boolean usesBlockLight() {
		return BaseModel.usesBlockLight();
	}
}
