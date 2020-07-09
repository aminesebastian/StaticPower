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
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import theking530.common.utilities.SDMath;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack.TileEntityDigistoreServerRack;

public class ServerRackModel extends AbstractBakedModel {
	public ServerRackModel(IBakedModel baseModel) {
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
		if (!data.hasProperty(TileEntityDigistoreServerRack.CARD_RENDERING_STATE)) {
			return Collections.emptyList();
		}

		Direction facing = state.get(StaticPowerTileEntityBlock.FACING);
		ItemStack[] cards = data.getData(TileEntityDigistoreServerRack.CARD_RENDERING_STATE);
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data);
		newQuads.addAll(baseQuads);

		for (int i = 0; i < 8.0f; i++) {
			if (cards[i].isEmpty()) {
				continue;
			}

			// Cache the model.
			IBakedModel model = Minecraft.getInstance().getModelManager().getModel(((DigistoreCard) cards[i].getItem()).model);

			float xOffset = (UNIT * 3.25f) - ((i / 4) * UNIT * 6.5f);
			float yOffset = 2.125f * UNIT + (UNIT * 3.25f * (i % 4));
			yOffset = 1.0f - yOffset - (2.0f * UNIT);
			float zOffset = -1.0f * UNIT;

			Vector3f offset = SDMath.transformVectorByDirection(facing, new Vector3f(xOffset, yOffset, zOffset));
			newQuads.addAll(this.transformQuads(model, offset, FACING_ROTATIONS.get(facing), side, state, rand));
		}

		return newQuads.build();
	}

	protected ModelDataMap getEmptyIModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		ModelDataMap modelDataMap = builder.build();
		return modelDataMap;
	}
}
