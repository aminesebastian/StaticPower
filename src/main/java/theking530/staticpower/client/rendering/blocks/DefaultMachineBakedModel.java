package theking530.staticpower.client.rendering.blocks;

import static net.minecraftforge.client.model.SimpleModelTransform.IDENTITY;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

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
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class DefaultMachineBakedModel extends AbstractBakedModel {
	@SuppressWarnings("deprecation")
	protected static final AtlasTexture BLOCKS_TEXTURE = ModelLoader.instance().getSpriteMap().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
	private static final Logger LOGGER = LogManager.getLogger(DefaultMachineBakedModel.class);
	private static final ModelProperty<Optional<MachineSideMode[]>> SIDE_CONFIG = new ModelProperty<>();

	public DefaultMachineBakedModel(IBakedModel baseModel) {
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

		// Iterate through all the quads.
		for (BakedQuad quad : baseQuads) {
			Direction renderingSide = side == null ? quad.getFace() : side;

			MachineSideMode sideMode = sideConfigurations.get()[renderingSide.ordinal()];
			try {
				AtlasTexture blocksTexture = ModelLoader.instance().getSpriteMap().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

				// Get the texture sprite for the side.
				TextureAtlasSprite sideSprite = getSpriteForMachineSide(sideMode, blocksTexture);

				// Attempt to render the quad for the side.
				renderQuadsForSide(newQuads, renderingSide, sideSprite, quad, sideMode);
			} catch (Exception e) {
				LOGGER.warn("An error occured when attempting to render the model.", e);
			}

		}
		return newQuads.build();
	}

	protected void renderQuadsForSide(Builder<BakedQuad> newQuads, Direction side, TextureAtlasSprite sideSprite, BakedQuad originalQuad, MachineSideMode sideConfiguration) {
		if (sideConfiguration != MachineSideMode.Never) {
			BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
			BlockPartFace blockPartFace = new BlockPartFace(null, -1, sideSprite.getName().toString(), blockFaceUV);
			BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(0, 0, 0), new Vector3f(16.0f, 16.0f, 16.0f), blockPartFace, sideSprite, side, IDENTITY, null, true, new ResourceLocation("dummy_name"));
			newQuads.add(newQuad);
		} else {
			newQuads.add(originalQuad);
		}
	}

	protected TextureAtlasSprite getSpriteForMachineSide(MachineSideMode mode, AtlasTexture blocksStitchedTextures) {
		switch (mode) {
		case Input:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_INPUT);
		case Output:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_OUTPUT);
		case Disabled:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_DISABLED);
		default:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_NORMAL);
		}
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
