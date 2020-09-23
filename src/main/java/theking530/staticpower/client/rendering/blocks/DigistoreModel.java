package theking530.staticpower.client.rendering.blocks;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.tileentities.digistorenetwork.digistore.TileEntityDigistore;

@OnlyIn(Dist.CLIENT)
public class DigistoreModel extends AbstractBakedModel {
	public DigistoreModel(IBakedModel baseModel) {
		super(baseModel);
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		return tileData;
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// If the property is not there, return early.
		if (!data.hasProperty(TileEntityDigistore.RENDERING_STATE)) {
			return Collections.emptyList();
		}

		// Get the data used in rendering.
		Direction facing = state.get(StaticPowerTileEntityBlock.FACING);
		ItemStack card = data.getData(TileEntityDigistore.RENDERING_STATE).card;

		// Create the output array.
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		// Add all the base quads for the server rack to the output.
		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data);
		newQuads.addAll(baseQuads);

		// Render the card.
		if (!card.isEmpty()) {
			// Get the model of the card.
			IBakedModel model = Minecraft.getInstance().getModelManager().getModel(((DigistoreCard) card.getItem()).model);

			// Calculate the offset for the current card's model.
			float xOffset = 0.0f;
			float yOffset = -UNIT * 0.415f;
			float zOffset = -0.75f * UNIT;

			// Create a vector from that offset.
			Vector3f offset = SDMath.transformVectorByDirection(facing, new Vector3f(xOffset, yOffset, zOffset));

			// Transform the card's quads.
			List<BakedQuad> bakedCardQuads = transformQuads(model, offset, new Vector3f(1.46f, .6f, 1.0f), FACING_ROTATIONS.get(facing), side, state, rand);
			newQuads.addAll(bakedCardQuads);

		}

		return newQuads.build();
	}

	protected ModelDataMap getEmptyIModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		ModelDataMap modelDataMap = builder.build();
		return modelDataMap;
	}
}
