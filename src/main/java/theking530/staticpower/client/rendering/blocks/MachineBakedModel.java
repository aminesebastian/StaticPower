package theking530.staticpower.client.rendering.blocks;

import static net.minecraftforge.client.model.SimpleModelTransform.IDENTITY;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticpower.client.StaticPowerAdditionalSprites;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class MachineBakedModel extends AbstractBakedModel {

	private static final ModelProperty<Optional<MachineSideMode[]>> SIDE_CONFIG = new ModelProperty<>();

	public MachineBakedModel(IBakedModel baseModel) {
		super(baseModel);
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
		Optional<MachineSideMode[]> configurations = getSideConfigurations(world, pos);
		ModelDataMap modelDataMap = getEmptyIModelData();
		modelDataMap.setData(SIDE_CONFIG, configurations);
		return modelDataMap;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// Check if the data has the SIDE_CONFIG property. If not, something has gone
		// wrong.
		if (!data.hasProperty(SIDE_CONFIG)) {
			conditionallyLogError("Encountered invalid side configuration data when attempting to bake quads for machine.");
			return BaseModel.getQuads(state, side, rand);
		}
		// Attempt to get the side configuration.
		Optional<MachineSideMode[]> sideConfigurations = data.getData(SIDE_CONFIG);

		// If we didn't get a side configuration, skip this block and just return the
		// defaults.
		if (!sideConfigurations.isPresent()) {
			conditionallyLogError("Encountered no side configuration data when attempting to bake quads for machine.");
			return BaseModel.getQuads(state, side, rand);
		}
		// Capture the default quads and retexture the sides to match the desired side
		// textures based on the configuration.

		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data);
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		AtlasTexture blocksStitchedTextures = ModelLoader.instance().getSpriteMap().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		TextureAtlasSprite sideSprite = null;

		for (BakedQuad quad : baseQuads) {
			switch (sideConfigurations.get()[side.ordinal()]) {
			case Input:
				sideSprite = blocksStitchedTextures.getSprite(StaticPowerAdditionalSprites.MACHINE_SIDE_INPUT);
				break;
			case Output:
				sideSprite = blocksStitchedTextures.getSprite(StaticPowerAdditionalSprites.MACHINE_SIDE_OUTPUT);
				break;
			case Disabled:
				sideSprite = blocksStitchedTextures.getSprite(StaticPowerAdditionalSprites.MACHINE_SIDE_DISABLED);
				break;
			default:
				sideSprite = blocksStitchedTextures.getSprite(StaticPowerAdditionalSprites.MACHINE_SIDE_NORMAL);
				break;
			}
			if (sideConfigurations.get()[side.ordinal()] != MachineSideMode.Never) {
				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				BlockPartFace blockPartFace = new BlockPartFace(null, -1, sideSprite.getName().toString(), blockFaceUV);
				BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(0, 0, 0), new Vector3f(16.0f, 16.0f, 16.0f), blockPartFace, sideSprite, side, IDENTITY, null, true,
						new ResourceLocation("dummy_name"));
				newQuads.add(newQuad);
			} else {
				newQuads.add(quad);
			}
		}
		return newQuads.build();
	}

	protected ModelDataMap getEmptyIModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		builder.withInitial(SIDE_CONFIG, Optional.empty());
		ModelDataMap modelDataMap = builder.build();
		return modelDataMap;
	}

	protected Optional<MachineSideMode[]> getSideConfigurations(@Nonnull ILightReader world, @Nonnull BlockPos blockPos) {
		if (!world.getBlockState(blockPos).hasTileEntity()) {
			return Optional.empty();
		}

		TileEntity rawTileEntity = world.getTileEntity(blockPos);

		if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
			TileEntityBase configurable = (TileEntityBase) rawTileEntity;
			if (configurable.hasComponentOfType(SideConfigurationComponent.class)) {
				return Optional.of(configurable.getComponent(SideConfigurationComponent.class).getWorldSpaceConfiguration());
			}
		}
		return Optional.empty();
	}
}
