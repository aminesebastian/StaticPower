package theking530.staticpower.client.rendering.blocks;

import static net.minecraftforge.client.model.SimpleModelTransform.IDENTITY;

import java.util.HashSet;
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
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
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
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.rendering.StaticPowerRendererTextures;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class MachineBakedModel implements IBakedModel {

	private static final ModelProperty<Optional<MachineSideMode[]>> SIDE_CONFIG = new ModelProperty<>();

	private final IBakedModel baseModel;
	private final HashSet<String> loggedErrors;
	private FaceBakery faceBakery = new FaceBakery();

	public MachineBakedModel(IBakedModel baseModel) {
		this.baseModel = baseModel;
		this.loggedErrors = new HashSet<String>();
	}

	@Override
	@Nonnull
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		return getBakedQuadsFromIModelData(state, side, rand, extraData);
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
	private List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
		// Check if the data has the SIDE_CONFIG property. If not, something has gone
		// wrong.
		if (!data.hasProperty(SIDE_CONFIG)) {
			conditionallyLogError("Encountered invalid side configuration data when attempting to bake quads for machine.");
			return baseModel.getQuads(state, side, rand);
		}
		// Attempt to get the side configuration.
		Optional<MachineSideMode[]> sideConfigurations = data.getData(SIDE_CONFIG);

		// If we didn't get a side configuration, skip this block and just return the
		// defaults.
		if (!sideConfigurations.isPresent()) {
			conditionallyLogError("Encountered no side configuration data when attempting to bake quads for machine.");
			return baseModel.getQuads(state, side, rand);
		}
		// Capture the default quads and retexture the sides to match the desired side
		// textures based on the configuration.

		List<BakedQuad> baseQuads = baseModel.getQuads(state, side, rand, data);
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		AtlasTexture blocksStitchedTextures = ModelLoader.instance().getSpriteMap().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		TextureAtlasSprite sideSprite = null;

		for (BakedQuad quad : baseQuads) {
			switch (sideConfigurations.get()[side.ordinal()]) {
			case Input:
				sideSprite = blocksStitchedTextures.getSprite(StaticPowerRendererTextures.MACHINE_SIDE_INPUT);
				break;
			case Output:
				sideSprite = blocksStitchedTextures.getSprite(StaticPowerRendererTextures.MACHINE_SIDE_OUTPUT);
				break;
			case Disabled:
				sideSprite = blocksStitchedTextures.getSprite(StaticPowerRendererTextures.MACHINE_SIDE_DISABLED);
				break;
			default:
				sideSprite = blocksStitchedTextures.getSprite(StaticPowerRendererTextures.MACHINE_SIDE_NORMAL);
				break;
			}
			if (sideConfigurations.get()[side.ordinal()] != MachineSideMode.Never) {
				BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
				BlockPartFace blockPartFace = new BlockPartFace(null, -1, sideSprite.getName().toString(), blockFaceUV);
				BakedQuad newQuad = faceBakery.bakeQuad(new Vector3f(0, 0, 0), new Vector3f(16.0f, 16.0f, 16.0f), blockPartFace, sideSprite, side, IDENTITY, null, true,
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

	protected void conditionallyLogError(String log) {
		if (!loggedErrors.contains(log)) {
			loggedErrors.add(log);
			StaticPower.LOGGER.error(log);
		}
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		throw new AssertionError("IBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
	}

	@Override
	public boolean isAmbientOcclusion() {
		return baseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseModel.isGui3d();
	}

	@Override
	public boolean func_230044_c_() {
		return baseModel.func_230044_c_();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return baseModel.isBuiltInRenderer();
	}

	@SuppressWarnings("deprecation")
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return baseModel.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return baseModel.getOverrides();
	}

}
