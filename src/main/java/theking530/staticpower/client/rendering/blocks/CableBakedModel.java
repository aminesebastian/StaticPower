package theking530.staticpower.client.rendering.blocks;

import static net.minecraftforge.client.model.SimpleModelTransform.IDENTITY;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import codechicken.lib.vec.Vector3;
import mcjty.theoneprobe.rendering.RenderHelper.Vector;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticpower.tileentities.cables.TileEntityPowerCable;

public class CableBakedModel extends AbstractBakedModel {
	private static final ModelProperty<Optional<Boolean[]>> ATTACHED_SIDES = new ModelProperty<>();

	public CableBakedModel(IBakedModel baseModel) {
		super(baseModel);
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		Optional<Boolean[]> configurations = getAdjacents(world, pos);
		ModelDataMap modelDataMap = getEmptyIModelData();
		modelDataMap.setData(ATTACHED_SIDES, configurations);
		return modelDataMap;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// Check if the data has the SIDE_CONFIG property. If not, something has gone
		// wrong.
		if (!data.hasProperty(ATTACHED_SIDES)) {
			conditionallyLogError("Encountered invalid adjacency data when attempting to bake quads for machine.");
			return BaseModel.getQuads(state, side, rand);
		}
		// Attempt to get the side configuration.
		Optional<Boolean[]> adjacencyMatrixOptional = data.getData(ATTACHED_SIDES);

		// If we didn't get a side configuration, skip this block and just return the
		// defaults.
		if (!adjacencyMatrixOptional.isPresent()) {
			conditionallyLogError("Encountered no adjacency data when attempting to bake quads for machine.");
			return BaseModel.getQuads(state, side, rand);
		}

		// Capture the default quads and retexture the sides to match the desired side
		// textures based on the configuration.
		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data);
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		if (side != null) {
			Boolean[] adjacencyMatrix = adjacencyMatrixOptional.get();

			TextureAtlasSprite sideSprite = baseQuads.get(0).func_187508_a();
			BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
			BlockPartFace blockPartFace = new BlockPartFace(null, -1, sideSprite.getName().toString(), blockFaceUV);
			BakedQuad newQuad;

			newQuad = FaceBaker.bakeQuad(new Vector3f(6.0f, 6.0f, 6.0f), new Vector3f(10.0f, 10.0f, 10.0f), blockPartFace, sideSprite, side, IDENTITY, null, true, new ResourceLocation("dummy_name"));
			newQuads.add(newQuad);

			if (side == Direction.UP) {
				for (int i = 2; i < 6; i++) {
					if (adjacencyMatrix[i]) {
						Direction dir = Direction.values()[i];

						Vector3f minOffset = new Vector3f(dir.getXOffset(), dir.getYOffset(), dir.getZOffset());
						minOffset.mul(-2.0f);
						Vector3f min = new Vector3f(8.0f, 6.0f, 8.0f);
						min.add(minOffset);

						Vector3f maxOffset = new Vector3f(dir.getXOffset(), dir.getYOffset(), dir.getZOffset());
						maxOffset.mul(8.0f);
						Vector3f max = new Vector3f(8.0f, 10.0f, 8.0f);
						max.add(maxOffset);

						newQuad = FaceBaker.bakeQuad(new Vector3f(6.0f, 6.0f, 10.0f), new Vector3f(10.0f, 10.0f, 16.0f), blockPartFace, sideSprite, side, IDENTITY, null, true, new ResourceLocation("dummy_name_2"));
						newQuads.add(newQuad);
					}
				}
			}
		}
		return newQuads.build();
	}

	protected ModelDataMap getEmptyIModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		builder.withInitial(ATTACHED_SIDES, Optional.empty());
		ModelDataMap modelDataMap = builder.build();
		return modelDataMap;
	}

	protected Optional<Boolean[]> getAdjacents(@Nonnull ILightReader world, @Nonnull BlockPos blockPos) {
		if (!world.getBlockState(blockPos).hasTileEntity()) {
			return Optional.empty();
		}

		Boolean[] output = new Boolean[] { false, false, false, false, false, false };

		for (Direction dir : Direction.values()) {
			BlockPos offset = blockPos.offset(dir);
			TileEntity adjacent = world.getTileEntity(offset);
			if (adjacent instanceof TileEntityPowerCable) {
				output[dir.ordinal()] = true;
			}
		}

		return Optional.of(output);
	}
}
